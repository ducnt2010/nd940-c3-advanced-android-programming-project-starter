package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

// Notification ID.
private const val NOTIFICATION_ID = 0
private const val REQUEST_CODE = 0

fun NotificationManager.sendNotification(
    messageBody: String,
    isDownloadSuccess: Boolean,
    applicationContext: Context
) {
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(Constant.IS_SUCCESS, isDownloadSuccess)
    contentIntent.putExtra(Constant.FILE_NAME, messageBody)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )

        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setAutoCancel(true)
        .setContentIntent(contentPendingIntent)
        .addAction(
            R.drawable.ic_baseline_details_24,
            applicationContext.getString(R.string.detail),
            contentPendingIntent
        )

    notify(NOTIFICATION_ID, builder.build())
}

