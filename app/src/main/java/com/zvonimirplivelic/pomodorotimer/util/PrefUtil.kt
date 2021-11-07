package com.zvonimirplivelic.pomodorotimer.util

import android.content.Context
import androidx.preference.PreferenceManager.*
import com.zvonimirplivelic.pomodorotimer.MainActivity

class PrefUtil {
    companion object {
        fun getTimerLength(context: Context): Int {
            return 25
        }

        private const val PREVIOUS_TIMER_LENGTH_ID = "PREV_TIME_LENGTH"

        fun getPreviousTimerLength(context: Context): Long {
            val preferences = getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_ID, 0)
        }

        fun setPreviousTimerLength(seconds: Long, context: Context) {
            val editor = getDefaultSharedPreferences(context).edit()
            editor.apply()
        }

        private const val TIMER_STATE_ID = "TIMER_STATE"

        fun getTimerState(context: Context): MainActivity.TimerState {
            val preferences = getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return MainActivity.TimerState.values()[ordinal]
        }

        fun setTimerState(state: MainActivity.TimerState, context: Context) {
            val editor = getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }

        private const val SECONDS_REMAINING_ID = "SECONDS_REMAINING"

        fun getSecondsRemaining(context: Context): Long {
            val preferences = getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setSecondsRemaining(seconds: Long, context: Context) {
            val editor = getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }

        private const val ALARM_SET_TIME_ID = "ALARM_SET_TIME"
        fun getAlarmSetTime(context: Context): Long {
            val preferences = getDefaultSharedPreferences(context)
            return preferences.getLong(ALARM_SET_TIME_ID, 0)
        }

        fun setAlarmSetTime(time: Long, context: Context) {
            val editor = getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }
    }
}