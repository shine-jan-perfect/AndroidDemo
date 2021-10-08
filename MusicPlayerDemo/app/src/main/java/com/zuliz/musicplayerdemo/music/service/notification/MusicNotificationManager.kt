package com.zuliz.musicplayerdemo.music.service.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zuliz.musicplayerdemo.MainActivity
import com.zuliz.musicplayerdemo.R

class MusicNotificationManager(val context: Context) {
    companion object {
        private const val NOTIFICATION_ID = 12345
        private const val CHANNEL_ID = "playerMedia"
        private const val CHANNEL_NAME = "音频播放"
    }

    fun initNotification(): Notification {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(CHANNEL_ID, CHANNEL_NAME)
            createNotification(CHANNEL_ID)
        } else {
            createNotification()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(channelId: String, channelName: String) {
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN)
        channel.setSound(null, null)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(channelId: String = CHANNEL_ID): Notification {
        val remoteViews = RemoteViews(context.packageName, R.layout.default_notification_layout)

        // TODO set click pending intent

        val intent = Intent(context, MainActivity::class.java)
        val contentClickPendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        remoteViews.setOnClickPendingIntent(R.id.rl_content, contentClickPendingIntent)

        val notification = NotificationCompat.Builder(context, channelId)
            .setWhen(System.currentTimeMillis())
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
//            .setLargeIcon(largeIcon)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContent(remoteViews)
            .setCustomBigContentView(remoteViews)
//            .setCustomContentView(smallRemoteViews)
            .setContentIntent(contentClickPendingIntent)
            .setOnlyAlertOnce(true)
            .setVibrate(null)
            .setSound(null)
            .setLights(0, 0, 0)
            .build()
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE
        return notification
    }

    fun update() {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(NOTIFICATION_ID, initNotification())
    }

    fun stopNotification() {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(NOTIFICATION_ID)
    }
}