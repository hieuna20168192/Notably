package com.example.notably.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.notably.R;
import com.example.notably.ui.main.MainActivity;

import java.util.Objects;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ReminderReceiver", "onReceive");
        // Get notification id & message from intent
        int notificationId = intent.getIntExtra("notificationId", 0);
        String title = intent.getStringExtra("title");
        String subtitle = intent.getStringExtra("subtitle");

        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra("fragment", "reminders");
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For API 26 and above
            CharSequence channel_name = "Reminder Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel("REMINDER_CHANNEL", channel_name, importance);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }

        // Prepare notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "REMINDER_CHANNEL");
        builder.setSmallIcon(R.drawable.icon_alarm)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL);

        // Notify
        Objects.requireNonNull(notificationManager).notify(notificationId, builder.build());
    }
}
