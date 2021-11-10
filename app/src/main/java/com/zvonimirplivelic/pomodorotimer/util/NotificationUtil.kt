package com.zvonimirplivelic.pomodorotimer.util

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.zvonimirplivelic.pomodorotimer.MainActivity
import com.zvonimirplivelic.pomodorotimer.R
import com.zvonimirplivelic.pomodorotimer.receiver.TimerNotificationsReceiver
import java.text.SimpleDateFormat
import java.util.*

class NotificationUtil {
    companion object {
        private const val CHANNEL_ID_TIMER = "POMODORO_TIMER_NOTIFICATION_CHANNEL_ID"
        private const val CHANNEL_NAME_TIMER = "Pomodoro Timer"
        private const val TIMER_ID = 0

        fun showTimerExpired(context: Context) {
            val startIntent = Intent(context, TimerNotificationsReceiver::class.java)
            startIntent.action = Constants.ACTION_START

            val startPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                startIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notificationBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
            notificationBuilder.setContentTitle("Timer Expired!")
                .setContentText("Start Again?")
                .setContentIntent(getPendingIntentWithStack(context, MainActivity::class.java))
                .addAction(R.drawable.ic_play, "Start", startPendingIntent)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                CHANNEL_ID_TIMER,
                CHANNEL_NAME_TIMER,
                true
            )
            notificationManager.notify(TIMER_ID, notificationBuilder.build())
        }

        fun showTimerRunning(context: Context, countdownEndTime: Long) {
            val stopIntent = Intent(context, TimerNotificationsReceiver::class.java)
            stopIntent.action = Constants.ACTION_STOP
            val stopPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val pauseIntent = Intent(context, TimerNotificationsReceiver::class.java)
            pauseIntent.action = Constants.ACTION_PAUSE
            val pausePendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val timeFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

            val notificationBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
            notificationBuilder.setContentTitle("Timer is Running!")
                .setContentText("End: ${timeFormat.format(Date(countdownEndTime))}")
                .setContentIntent(getPendingIntentWithStack(context, MainActivity::class.java))
                .setOngoing(true)
                .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
                .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                CHANNEL_ID_TIMER,
                CHANNEL_NAME_TIMER,
                true
            )
            notificationManager.notify(TIMER_ID, notificationBuilder.build())
        }


        fun showTimerPaused(context: Context) {
            val resumeIntent = Intent(context, TimerNotificationsReceiver::class.java)
            resumeIntent.action = Constants.ACTION_RESUME

            val resumePendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                resumeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notificationBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
            notificationBuilder.setContentTitle("Timer is paused.")
                .setContentText("Resume?")
                .setContentIntent(getPendingIntentWithStack(context, MainActivity::class.java))
                .addAction(R.drawable.ic_play, "Resume", resumePendingIntent)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                CHANNEL_ID_TIMER,
                CHANNEL_NAME_TIMER,
                true
            )
            notificationManager.notify(TIMER_ID, notificationBuilder.build())
        }

        fun hideTimerNotification(context: Context) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(TIMER_ID)
        }

        private fun getBasicNotificationBuilder(
            context: Context,
            channelId: String,
            playSound: Boolean
        ): NotificationCompat.Builder {
            val notificationSound: Uri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_tomato)
                .setAutoCancel(true)
                .setDefaults(0)

            if (playSound) {
                notificationBuilder.setSound(notificationSound)
            }

            return notificationBuilder
        }

        private fun <T> getPendingIntentWithStack(
            context: Context,
            javaClass: Class<T>
        ): PendingIntent {
            val resultIntent = Intent(context, javaClass)
            resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(javaClass)
            stackBuilder.addNextIntent(resultIntent)

            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        @TargetApi(26)
        private fun NotificationManager.createNotificationChannel(
            channelId: String,
            channelName: String,
            playSound: Boolean
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelImportance = if (playSound) {
                    NotificationManager.IMPORTANCE_DEFAULT
                } else {
                    NotificationManager.IMPORTANCE_LOW
                }

                val notificationChannel =
                    NotificationChannel(channelId, channelName, channelImportance)

                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.BLUE
                this.createNotificationChannel(notificationChannel)
            }
        }
    }
}