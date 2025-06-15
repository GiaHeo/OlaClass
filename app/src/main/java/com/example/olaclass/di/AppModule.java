package com.example.olaclass.di;

import android.app.Application;
import com.example.olaclass.data.repository.UserRepository;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {
    @Provides
    @Singleton
    public UserRepository provideUserRepository() {
        return new UserRepository();
    }
    
    @Provides
    @Singleton
    public Application provideApplication(Application application) {
        return application;
    }
}
