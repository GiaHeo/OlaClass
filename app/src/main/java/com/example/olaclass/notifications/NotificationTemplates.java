package com.example.olaclass.notifications;

public class NotificationTemplates {
    public static String assignmentTitle(String subject) {
        return "Bài tập mới môn " + subject;
    }
    public static String assignmentBody(String title, String deadline) {
        return "Bạn có bài tập: " + title + "\nHạn nộp: " + deadline;
    }
    public static String attendanceTitle() {
        return "Điểm danh lớp học";
    }
    public static String attendanceBody(String className) {
        return "Vui lòng điểm danh vào lớp: " + className;
    }
    public static String genericTitle(String custom) {
        return custom;
    }
    public static String genericBody(String content) {
        return content;
    }
}
