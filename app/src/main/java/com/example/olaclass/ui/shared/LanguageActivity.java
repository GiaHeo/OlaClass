package com.example.olaclass.ui.shared;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.olaclass.R;
import com.example.olaclass.utils.LanguageManager;

public class LanguageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        Button btnVi = findViewById(R.id.btn_vietnamese);
        Button btnEn = findViewById(R.id.btn_english);

        btnVi.setOnClickListener(v -> {
            LanguageManager.setLocale(this, "vi");
            recreateApp();
        });

        btnEn.setOnClickListener(v -> {
            LanguageManager.setLocale(this, "en");
            recreateApp();
        });
    }

    private void recreateApp() {
        Intent intent = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        finish();
    }
}
