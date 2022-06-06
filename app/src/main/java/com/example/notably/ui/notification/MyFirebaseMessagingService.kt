package com.example.notably.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.notably.R
import com.example.notably.extensions.SharedPref
import com.example.notably.repos.entities.Notification
import com.example.notably.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

const val TAG = "MyFMService"


@RequiresApi(Build.VERSION_CODES.O)
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var sharePref: SharedPref

    override fun onNewToken(t: String) {
        super.onNewToken(t)
        sharePref = SharedPref(this)
        sharePref.apply {
            setFCMRregisterID(t)
            setNeedRegister(true)
            setSubscribeNotifications(false)
        }
        Log.d(TAG, t)
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        Log.d(TAG, "data ${msg.data}")
        var notification: Notification? = null

        msg.notification?.let {
            notification = Notification(title = it.title ?: "", content = it.body ?: "")
        }

        if (msg.data.isNotEmpty()) {
            notification = Gson().fromJson(Gson().toJson(msg.data), Notification::class.java)
        }

        notification?.apply {
//            id = System.currentTimeMillis()
            createdAt = System.currentTimeMillis()
            isRead = false

            val notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.sendNotification(this, applicationContext)
        }
    }
}

const val NOTIFICATION_ID = 0
const val REQUEST_CODE = 0
const val FLAGS = 0

@RequiresApi(Build.VERSION_CODES.O)
fun NotificationManager.sendNotification(notification: Notification, applicationContext: Context) {
    var channel: NotificationChannel? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "channel1"
        val descriptionText = "hello"
        channel = NotificationChannel(applicationContext.getString(R.string.note_channel), name, NotificationManager.IMPORTANCE_HIGH).apply {
            description = descriptionText
        }
    }
    channel?.let { createNotificationChannel(it) }
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    contentIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
    contentIntent.putExtra("notification", notification)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val image = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.ic_logo)
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(image)
        .bigLargeIcon(null)

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.note_channel)
    )
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle(notification.title)
        .setContentText(notification.content)
        .setContentIntent(contentPendingIntent)
        .setStyle(bigPicStyle)
        .setLargeIcon(image)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
    notify(NOTIFICATION_ID, builder.build())
}
