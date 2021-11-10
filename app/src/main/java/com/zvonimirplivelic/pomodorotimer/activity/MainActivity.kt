package com.zvonimirplivelic.pomodorotimer.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zvonimirplivelic.pomodorotimer.R
import com.zvonimirplivelic.pomodorotimer.receiver.TimerDoneReceiver
import com.zvonimirplivelic.pomodorotimer.util.Constants
import com.zvonimirplivelic.pomodorotimer.util.MediaPlayerUtil.playSound
import com.zvonimirplivelic.pomodorotimer.util.NotificationUtil
import com.zvonimirplivelic.pomodorotimer.util.PrefUtil
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var timer: CountDownTimer

    private lateinit var fabStart: FloatingActionButton
    private lateinit var fabStop: FloatingActionButton
    private lateinit var fabPause: FloatingActionButton
    private lateinit var progressBar: MaterialProgressBar
    private lateinit var tvTime: TextView
    private lateinit var ivTomato: ImageView

    companion object {
        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long {
            val countdownEndTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerDoneReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, countdownEndTime, pendingIntent)

            PrefUtil.setAlarmSetTime(nowSeconds, context)
            return countdownEndTime
        }

        fun removeAlarm(context: Context) {
            val intent = Intent(context, TimerDoneReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
        }

        val currentSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }

    enum class TimerState {
        STOPPED, PAUSED, RUNNING
    }

    private var timerLengthSeconds: Long = 0L
    private var timerState = TimerState.STOPPED

    private var secondsRemaining = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            setIcon(R.drawable.ic_tomato)
            title = "Pomodoro Timer"
        }

        fabStart = findViewById(R.id.fab_play)
        fabStop = findViewById(R.id.fab_stop)
        fabPause = findViewById(R.id.fab_pause)
        progressBar = findViewById(R.id.mpb_progress_bar)
        tvTime = findViewById(R.id.tv_timer_countdown)
        ivTomato = findViewById(R.id.iv_tomato)

        fabStart.setOnClickListener {
            startTimer()
            timerState = TimerState.RUNNING
            updateButtons()

            Log.d(TAG, "onCreate: ")
        }
        fabPause.setOnClickListener {
            timer.cancel()
            timerState = TimerState.PAUSED
            updateButtons()
        }
        fabStop.setOnClickListener {
            timer.cancel()
            onTimerFinished()
        }
    }

    override fun onResume() {
        super.onResume()
//
//        val secondsOnProgressBar = PrefUtil.getSecondsRemaining(this).toInt()
//        progressBar.progress = secondsOnProgressBar
        initializeTimer()
        removeAlarm(this)
        NotificationUtil.hideTimerNotification(this)
    }

    override fun onPause() {
        super.onPause()

        if (timerState == TimerState.RUNNING) {
            timer.cancel()
            val countdownEndTime = setAlarm(this, currentSeconds, secondsRemaining)
            NotificationUtil.showTimerRunning(this, countdownEndTime)
        } else if (timerState == TimerState.PAUSED) {
            NotificationUtil.showTimerPaused(this)
        }
        PrefUtil.setPreviousTimerLength(timerLengthSeconds, this)
        PrefUtil.setSecondsRemaining(secondsRemaining, this)
        PrefUtil.setTimerState(timerState, this)
    }

    private fun initializeTimer() {
        timerState = PrefUtil.getTimerState(this)
        if (timerState == TimerState.STOPPED) {
            setNewTimerLength()
        } else {
            setPreviousTimerLength()
        }

        secondsRemaining =
            if (timerState == TimerState.RUNNING || timerState == TimerState.PAUSED) {
                PrefUtil.getSecondsRemaining(this)
            } else {
                timerLengthSeconds
            }

        val alarmSetTime = PrefUtil.getAlarmSetTime(this)

        if (alarmSetTime > 0) {
            secondsRemaining -= currentSeconds - alarmSetTime
        }

        if (secondsRemaining <= 0) {
            onTimerFinished()
        } else if (timerState == TimerState.RUNNING) {
            startTimer()
        }

        updateButtons()
        updateCountdownUI()
    }

    private fun onTimerFinished() {
        playSound(this, PrefUtil.getAlarmSoundEnabled(this))
        // animate IV
        timerState = TimerState.STOPPED
        setNewTimerLength()

        progressBar.progress = 0

        PrefUtil.setSecondsRemaining(timerLengthSeconds, this)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()
    }

    private fun startTimer() {
        timerState = TimerState.RUNNING
        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun setNewTimerLength() {
        val lengthInMinutes = PrefUtil.getTimerLength(this)
        timerLengthSeconds = (lengthInMinutes * 60L)
        progressBar.max = timerLengthSeconds.toInt()
    }

    private fun setPreviousTimerLength() {
        timerLengthSeconds = PrefUtil.getPreviousTimerLength(this)
        progressBar.max = timerLengthSeconds.toInt()
    }

    private fun updateCountdownUI() {
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60

        val secondsString = secondsInMinuteUntilFinished.toString()
        tvTime.text = "$minutesUntilFinished:${
            if (secondsString.length == 2) secondsString
            else "0" + secondsString
        }"

        progressBar.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    private fun updateButtons() {
        when (timerState) {
            TimerState.RUNNING -> {
                fabStart.isEnabled = false
                fabPause.isEnabled = true
                fabStop.isEnabled = true
                ivTomato.visibility = View.GONE
            }
            TimerState.PAUSED -> {
                fabStart.isEnabled = true
                fabPause.isEnabled = false
                fabStop.isEnabled = true
            }
            TimerState.STOPPED -> {
                fabStart.isEnabled = true
                fabPause.isEnabled = false
                fabStop.isEnabled = false
                ivTomato.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_info -> {
                val infoURL = Constants.INFO_URL
                val infoIntent = Intent(Intent.ACTION_VIEW)
                infoIntent.data = Uri.parse(infoURL)
                startActivity(infoIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}