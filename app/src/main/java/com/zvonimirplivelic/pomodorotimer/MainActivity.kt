package com.zvonimirplivelic.pomodorotimer

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zvonimirplivelic.pomodorotimer.util.PrefUtil
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var fabStart: FloatingActionButton
    private lateinit var fabStop: FloatingActionButton
    private lateinit var fabPause: FloatingActionButton
    private lateinit var progressBar: MaterialProgressBar
    private lateinit var tvTime: TextView

    enum class TimerState {
        STOPPED, PAUSED, RUNNING
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds: Long = 0L
    private var timerState = TimerState.STOPPED

    private var secondsRemaining = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setIcon(R.drawable.ic_tomato)
        supportActionBar?.title = "      Pomodoro Timer"

        fabStart = findViewById(R.id.fab_play)
        fabStop = findViewById(R.id.fab_stop)
        fabPause = findViewById(R.id.fab_pause)
        progressBar = findViewById(R.id.mpb_progress_bar)
        tvTime = findViewById(R.id.tv_timer_countdown)

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
        initializeTimer()
    }

    override fun onPause() {
        super.onPause()

        if (timerState == TimerState.RUNNING) {
            timer.cancel()
        } else if (timerState == TimerState.PAUSED) {

            PrefUtil.setPreviousTimerLength(timerLengthSeconds, this)
            PrefUtil.setSecondsRemaining(secondsRemaining, this)
            PrefUtil.setTimerState(timerState, this)

        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        PrefUtil.getTimerLength(this)
//    }

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

        if (timerState == TimerState.RUNNING) {
            startTimer()
            updateButtons()
        }
    }

    private fun onTimerFinished() {
        timerState = MainActivity.TimerState.STOPPED
        setNewTimerLength()

        progressBar.progress = 0

        PrefUtil.setSecondsRemaining(timerLengthSeconds, this)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()
    }

    private fun startTimer() {
        timerState = MainActivity.TimerState.RUNNING
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
            }
            TimerState.PAUSED -> {
                fabStart.isEnabled = true
                fabPause.isEnabled = false
                fabStop.isEnabled = true
            }
            TimerState.STOPPED -> {
                fabStart.isEnabled = true
                fabPause.isEnabled = false
                fabStop.isEnabled = true
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}