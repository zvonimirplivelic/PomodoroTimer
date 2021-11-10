package com.zvonimirplivelic.pomodorotimer.util

import android.content.Context
import android.media.MediaPlayer
import com.zvonimirplivelic.pomodorotimer.R

object MediaPlayerUtil {
    fun playSound(context: Context, soundEnabled: Boolean) {
        if (soundEnabled) {
            return MediaPlayer.create(context, R.raw.tomato_splash).start()
        }
    }
}