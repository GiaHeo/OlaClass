package com.example.olaclass.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.example.olaclass.R;
import com.example.olaclass.utils.PreferenceKeys;

public class NotificationSettingsActivity extends AppCompatActivity {
    private SwitchCompat swAssignment, swAttendance, swGeneral;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        prefs = getSharedPreferences(PreferenceKeys.NOTIFICATION_PREFS, MODE_PRIVATE);
        swAssignment = findViewById(R.id.swAssignment);
        swAttendance = findViewById(R.id.swAttendance);
        swGeneral = findViewById(R.id.swGeneral);
        swAssignment.setChecked(prefs.getBoolean(PreferenceKeys.ASSIGNMENT, true));
        swAttendance.setChecked(prefs.getBoolean(PreferenceKeys.ATTENDANCE, true));
        swGeneral.setChecked(prefs.getBoolean(PreferenceKeys.GENERAL, true));
        swAssignment.setOnCheckedChangeListener(this::onSwitchChanged);
        swAttendance.setOnCheckedChangeListener(this::onSwitchChanged);
        swGeneral.setOnCheckedChangeListener(this::onSwitchChanged);
    }
    private void onSwitchChanged(CompoundButton button, boolean isChecked) {
        SharedPreferences.Editor editor = prefs.edit();
        switch (button.getId()) {
            case R.id.swAssignment: editor.putBoolean(PreferenceKeys.ASSIGNMENT, isChecked); break;
            case R.id.swAttendance: editor.putBoolean(PreferenceKeys.ATTENDANCE, isChecked); break;
            case R.id.swGeneral: editor.putBoolean(PreferenceKeys.GENERAL, isChecked); break;
        }
        editor.apply();
    }
}
