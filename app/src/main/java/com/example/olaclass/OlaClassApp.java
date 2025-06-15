package com.example.olaclass;

import android.app.Application;

import com.example.olaclass.di.AppComponent;
import com.example.olaclass.di.DaggerAppComponent;

public class OlaClassApp extends Application {
    
    private static OlaClassApp instance;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        // Initialize Dagger
        appComponent = DaggerAppComponent.factory().create(this);
        
        // Initialize Firebase
        // FirebaseApp.initializeApp(this);
    }


    public static OlaClassApp getInstance() {
        return instance;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
