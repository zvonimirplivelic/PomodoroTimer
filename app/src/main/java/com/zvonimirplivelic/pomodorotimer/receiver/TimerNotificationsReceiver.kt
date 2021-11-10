package com.zvonimirplivelic.pomodorotimer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zvonimirplivelic.pomodorotimer.MainActivity
import com.zvonimirplivelic.pomodorotimer.util.Constants
import com.zvonimirplivelic.pomodorotimer.util.NotificationUtil
import com.zvonimirplivelic.pomodorotimer.util.PrefUtil

class TimerNotificationsReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Constants.ACTION_STOP -> {
                MainActivity.removeAlarm(context)
                PrefUtil.setTimerState(MainActivity.TimerState.STOPPED, context)
                NotificationUtil.hideTimerNotification(context)
            }

            Constants.ACTION_PAUSE -> {
                var secondsRemaining = PrefUtil.getSecondsRemaining(context)
                val alarmSetTime = PrefUtil.getAlarmSetTime(context)
                val currentSeconds = MainActivity.currentSeconds

                secondsRemaining -= currentSeconds - alarmSetTime
                PrefUtil.setSecondsRemaining(secondsRemaining, context)

                MainActivity.removeAlarm(context)
                PrefUtil.setTimerState(MainActivity.TimerState.PAUSED, context)
                NotificationUtil.showTimerPaused(context)
            }

            Constants.ACTION_RESUME -> {
                val secondsRemaining = PrefUtil.getSecondsRemaining(context)
                val countdownEndTime =
                    MainActivity.setAlarm(context, MainActivity.currentSeconds, secondsRemaining)
                PrefUtil.setTimerState(MainActivity.TimerState.RUNNING, context)
                NotificationUtil.showTimerRunning(context, countdownEndTime)
            }

            Constants.ACTION_START -> {
                val minutesRemaining = PrefUtil.getTimerLength(context)
                val secondsRemaining = minutesRemaining * 60L
                val countdownEndTime =
                    MainActivity.setAlarm(context, MainActivity.currentSeconds, secondsRemaining)
                PrefUtil.setTimerState(MainActivity.TimerState.RUNNING, context)
                PrefUtil.setSecondsRemaining(secondsRemaining, context)
                NotificationUtil.showTimerRunning(context, countdownEndTime)
            }
        }
    }
}