package com.zvonimirplivelic.pomodorotimer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zvonimirplivelic.pomodorotimer.MainActivity
import com.zvonimirplivelic.pomodorotimer.util.PrefUtil

class TimerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        PrefUtil.setTimerState(MainActivity.TimerState.STOPPED, context)
        PrefUtil.setAlarmSetTime(0, context)
    }
}