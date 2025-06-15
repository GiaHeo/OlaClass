package com.example.olaclass.ui.base;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

public abstract class BaseActivity<DB extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity {

    protected DB binding;
    protected VM viewModel;

    @LayoutRes
    protected abstract int getLayoutRes();

    protected abstract Class<VM> getViewModelClass();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getLayoutRes());
        binding.setLifecycleOwner(this);
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(getViewModelClass());
        
        setupViews();
        setupObservers();
    }

    protected abstract void setupViews();

    protected abstract void setupObservers();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
