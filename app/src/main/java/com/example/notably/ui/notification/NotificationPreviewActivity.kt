package com.example.notably.ui.notification

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notably.R
import com.example.notably.repos.entities.Notification
import com.example.notably.ui.main.MainActivity

class NotificationPreviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_preview)
    }

    companion object {
        private const val EXTRA_OBJECT = "key.EXTRA_OBJECT"
        private const val EXTRA_FROM_NOTIF = "key.EXTRA_FROM_NOTIF"

        fun getMainIntent(context: Context, obj: Notification, fromNotif: Boolean): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(EXTRA_OBJECT, obj)
            intent.putExtra(EXTRA_FROM_NOTIF, fromNotif)
            return intent
        }
    }
}
