package com.example.olaclass.notifications;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class NotificationSender {
    // Gửi thông báo tự động bằng cách lưu vào collection "notifications" trên Firestore (giả lập server push)
    public static void sendAssignmentNotification(String userId, String subject, String title, String deadline) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("title", NotificationTemplates.assignmentTitle(subject));
        data.put("body", NotificationTemplates.assignmentBody(title, deadline));
        data.put("type", "assignment");
        data.put("timestamp", System.currentTimeMillis());
        FirebaseFirestore.getInstance().collection("notifications").add(data);
    }
    public static void sendAttendanceNotification(String userId, String className) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("title", NotificationTemplates.attendanceTitle());
        data.put("body", NotificationTemplates.attendanceBody(className));
        data.put("type", "attendance");
        data.put("timestamp", System.currentTimeMillis());
        FirebaseFirestore.getInstance().collection("notifications").add(data);
    }
    public static void sendCustomNotification(String userId, String title, String body) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("title", title);
        data.put("body", body);
        data.put("type", "custom");
        data.put("timestamp", System.currentTimeMillis());
        FirebaseFirestore.getInstance().collection("notifications").add(data);
    }
}
