package com.example.olaclass.ui.assignment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import com.example.olaclass.R;
import java.util.Calendar;

public class DeadlinePickerDialog extends Dialog {
    private DatePicker datePicker;
    private TimePicker timePicker;
    private OnDeadlineSetListener listener;

    public interface OnDeadlineSetListener {
        void onDeadlineSet(long deadlineMillis);
    }

    public DeadlinePickerDialog(@NonNull Context context, OnDeadlineSetListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_deadline_picker);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        findViewById(android.R.id.button1).setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                    timePicker.getHour(), timePicker.getMinute(), 0);
            listener.onDeadlineSet(cal.getTimeInMillis());
            dismiss();
        });
        findViewById(android.R.id.button2).setOnClickListener(v -> dismiss());
    }
}
