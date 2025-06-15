package com.example.olaclass.ui.auth;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import com.example.olaclass.databinding.ActivityAuthBinding;
import com.example.olaclass.ui.base.BaseActivity;
import com.example.olaclass.ui.base.BaseViewModel;
import com.example.olaclass.ui.auth.AuthViewModel;

public class AuthActivity extends BaseActivity<ActivityAuthBinding, AuthViewModel> {
    private static final String TAG = "StartupTime";

    @Override
    protected int getLayoutRes() {
        return com.example.olaclass.R.layout.activity_auth;
    }

    @Override
    protected Class<AuthViewModel> getViewModelClass() {
        return AuthViewModel.class;
    }

    @Override
    protected void setupViews() {
        // Đánh dấu thời điểm màn hình đầu tiên đã render
        long elapsed = System.currentTimeMillis() - com.example.olaclass.OlaClassApp.appStartTime;
        Log.d(TAG, "Thời gian khởi động app (ms): " + elapsed);
    }

    @Override
    protected void setupObservers() {
        // Chưa cần observer cho màn hình auth mẫu
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Các khởi tạo khác nếu cần
    }
}
