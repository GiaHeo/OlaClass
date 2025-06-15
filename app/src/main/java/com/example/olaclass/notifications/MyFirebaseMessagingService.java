package com.example.olaclass.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.olaclass.R;
import com.example.olaclass.utils.PreferenceKeys;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            String type = remoteMessage.getData().get("type");
            if (shouldShowNotification(type)) {
                showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage.getData());
            }
        }
    }

    private boolean shouldShowNotification(String type) {
        SharedPreferences prefs = getSharedPreferences(PreferenceKeys.NOTIFICATION_PREFS, MODE_PRIVATE);
        if (type == null) return true;
        switch (type) {
            case "assignment": return prefs.getBoolean(PreferenceKeys.ASSIGNMENT, true);
            case "attendance": return prefs.getBoolean(PreferenceKeys.ATTENDANCE, true);
            case "custom":
            default: return prefs.getBoolean(PreferenceKeys.GENERAL, true);
        }
    }

    private void showNotification(String title, String body, java.util.Map<String, String> data) {
        String channelId = "olaclass_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "OlaClass Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);
        NotificationManagerCompat.from(this).notify((int) System.currentTimeMillis(), builder.build());
    }
}
