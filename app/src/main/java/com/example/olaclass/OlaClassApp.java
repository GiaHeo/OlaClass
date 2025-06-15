package com.example.olaclass;

import android.app.Application;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class OlaClassApp extends Application {
    public static long appStartTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        appStartTime = System.currentTimeMillis();
        // Các khởi tạo khác nếu cần
    }
}
