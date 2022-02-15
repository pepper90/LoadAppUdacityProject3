package com.udacity.utils

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R

private const val NOTIFICATION_ID = 0
const val FILE_NAME = " FILE_NAME"
const val STATUS = "STATUS"

@SuppressLint("UnspecifiedImmutableFlag")
fun NotificationManager.sendNotification(fileName: String, status: String, applicationContext: Context) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
        .putExtra(FILE_NAME, fileName)
        .putExtra(STATUS, status)

    val contentPendingIntent = getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(
            applicationContext
            .getString(R.string.notification_title)
        )
        .setContentText(fileName)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent)

        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

//fun NotificationManager.cancelNotifications() {
//    cancelAll()
//}