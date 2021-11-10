package com.zvonimirplivelic.pomodorotimer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zvonimirplivelic.pomodorotimer.activity.MainActivity
import com.zvonimirplivelic.pomodorotimer.util.NotificationUtil
import com.zvonimirplivelic.pomodorotimer.util.PrefUtil

class TimerDoneReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtil.showTimerExpired(context)
        PrefUtil.setTimerState(MainActivity.TimerState.STOPPED, context)
        PrefUtil.setAlarmSetTime(0, context)
    }
}