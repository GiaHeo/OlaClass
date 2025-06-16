package com.example.olaclass.ui.classroom;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.olaclass.R;
import com.example.olaclass.data.model.Classroom;
import com.example.olaclass.data.repository.ClassroomRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
        repository = new ClassroomRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());
        btnCreateClass.setOnClickListener(v -> onCreateClassClicked());
    }

    private void onCreateClassClicked() {
        Log.d("CreateClass", "onCreateClassClicked() method entered.");
        Log.d("CreateClass", "Bắt đầu tạo lớp mới...");
        
        String name = etClassName.getText().toString().trim();
        String desc = etClassDesc.getText().toString().trim();
        String subject = etClassSubject.getText().toString().trim();
        
        // Validate input
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(subject)) {
            Toast.makeText(this, "Vui lòng nhập đủ tên lớp và môn học", Toast.LENGTH_SHORT).show();
            return;
        }
        
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String teacherId = user.getUid();
        
        // Generate invite code
        String inviteCode = generateInviteCode();
        Log.d("CreateClass", "Mã mời đã tạo: " + inviteCode);
        
        // Create classroom object
        Classroom classroom = new Classroom();
        classroom.setName(name);
        classroom.setDescription(desc);
        classroom.setSubject(subject);
        classroom.setTeacherId(teacherId);
        classroom.setInviteCode(inviteCode);
        
        Log.d("CreateClass", "Đối tượng Classroom đã tạo: " + classroom.toString());
        
        // Add to Firestore
        addClassroomToFirestore(classroom, inviteCode);
    }
    
    private String generateInviteCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    private void addClassroomToFirestore(Classroom classroom, String inviteCode) {
        Log.d("CreateClass", "Đang thêm lớp học vào Firestore...");
        
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> classData = new HashMap<>();
        classData.put("name", classroom.getName());
        classData.put("description", classroom.getDescription());
        classData.put("subject", classroom.getSubject());
        classData.put("teacherId", classroom.getTeacherId());
        classData.put("inviteCode", inviteCode);
        classData.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());
        
        Log.d("CreateClass", "Dữ liệu lớp học sẽ lưu: " + classData.toString());
        
        // Add classroom to Firestore
        db.collection("classrooms")
                .add(classData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("CreateClass", "Lớp học đã được tạo với ID: " + documentReference.getId());
                    Toast.makeText(this, "Tạo lớp thành công! Mã tham gia: " + inviteCode, Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("CreateClass", "Lỗi khi tạo lớp: " + e.getMessage(), e);
                    Toast.makeText(this, "Lỗi tạo lớp: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
