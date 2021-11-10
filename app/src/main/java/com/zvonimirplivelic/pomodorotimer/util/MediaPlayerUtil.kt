package com.zvonimirplivelic.pomodorotimer.util

import android.content.Context
import android.media.MediaPlayer
import com.zvonimirplivelic.pomodorotimer.R

object MediaPlayerUtil {
    fun playSound(context: Context) {
        var player = MediaPlayer.create(context, R.raw.tomato_splash)
        player.start()
    }
}