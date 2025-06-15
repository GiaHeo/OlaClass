package com.example.olaclass.utils;

public class DeadlineUtils {
    public static boolean isBeforeDeadline(long deadlineMillis) {
        return System.currentTimeMillis() < deadlineMillis;
    }
    public static boolean isNearDeadline(long deadlineMillis, long warningMillis) {
        return deadlineMillis - System.currentTimeMillis() <= warningMillis && isBeforeDeadline(deadlineMillis);
    }
    public static boolean isLate(long deadlineMillis) {
        return System.currentTimeMillis() > deadlineMillis;
    }
}
