package com.example.olaclass.notifications;

import android.content.Context;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.olaclass.R;

public class AssignmentPublishNotifier {
    private static final String CHANNEL_ID = "assignment_publish_channel";

    public static void notifyAssignmentPublished(Context context, String assignmentTitle) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Assignment Published", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Bài tập mới đã được phát hành")
                .setContentText("Bài tập: " + assignmentTitle + " đã được phát hành. Hãy kiểm tra ngay!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        manager.notify((int) System.currentTimeMillis(), notification);
    }
}
