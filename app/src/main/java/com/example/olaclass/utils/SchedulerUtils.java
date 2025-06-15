package com.example.olaclass.utils;

import android.os.Handler;
import android.os.Looper;

public class SchedulerUtils {
    public static void schedule(Runnable task, long delayMillis) {
        new Handler(Looper.getMainLooper()).postDelayed(task, delayMillis);
    }
}
