package com.example.olaclass.ui.classroom;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.olaclass.R;
import com.example.olaclass.data.repository.ClassroomRepository;
import com.google.firebase.auth.FirebaseAuth;

public class CreateClassroomActivity extends AppCompatActivity {
    private EditText etClassName, etClassDesc, etClassSubject;
    private Button btnCreateClass;
    private ClassroomRepository repository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_classroom);
        etClassName = findViewById(R.id.etClassName);
        etClassDesc = findViewById(R.id.etClassDesc);
        etClassSubject = findViewById(R.id.etClassSubject);
        btnCreateClass = findViewById(R.id.btnCreateClass);
        repository = new ClassroomRepository();
        btnCreateClass.setOnClickListener(v -> onCreateClassClicked());
    }

    private void onCreateClassClicked() {
        String name = etClassName.getText().toString().trim();
        String desc = etClassDesc.getText().toString().trim();
        String subject = etClassSubject.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(subject)) {
            Toast.makeText(this, "Vui lòng nhập đủ tên lớp và môn học", Toast.LENGTH_SHORT).show();
            return;
        }
        String teacherId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        repository.createClassroom(name, desc, subject, teacherId)
            .addOnSuccessListener(docRef -> {
                repository.generateAndSaveUniqueInviteCode(docRef.getId())
                    .addOnSuccessListener(code -> {
                        Toast.makeText(this, "Tạo lớp thành công! Mã tham gia: " + code, Toast.LENGTH_LONG).show();
                        finish();
                    });
            })
            .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tạo lớp: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
