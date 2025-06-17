package com.example.olaclass.ui.classroom;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.olaclass.R;
import com.example.olaclass.data.model.Announcement;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateAnnouncementActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etContent;
    private TextInputLayout titleInputLayout, contentInputLayout;
    private MaterialButton btnPost;
    private ProgressBar progressBar;
    private String classroomId;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_announcement);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get classroom ID from intent
        classroomId = getIntent().getStringExtra("classroomId");
        if (classroomId == null) {
            finish();
            return;
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initialize views
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        titleInputLayout = findViewById(R.id.title_input_layout);
        contentInputLayout = findViewById(R.id.content_input_layout);
        btnPost = findViewById(R.id.btn_post_announcement);
        progressBar = findViewById(R.id.progress_bar);

        // Set click listener for post button
        btnPost.setOnClickListener(v -> validateAndPostAnnouncement());
    }

    private void validateAndPostAnnouncement() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        // Reset errors
        titleInputLayout.setError(null);
        contentInputLayout.setError(null);

        boolean isValid = true;

        // Validate title
        if (TextUtils.isEmpty(title)) {
            titleInputLayout.setError("Vui lòng nhập tiêu đề");
            isValid = false;
        }

        // Validate content
        if (TextUtils.isEmpty(content)) {
            contentInputLayout.setError("Vui lòng nhập nội dung");
            isValid = false;
        }

        if (isValid) {
            postAnnouncement(title, content);
        }
    }

    private void postAnnouncement(String title, String content) {
        // Show progress
        setInProgress(true);

        // Get current user info
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(this, "Lỗi xác thực người dùng", Toast.LENGTH_SHORT).show();
            setInProgress(false);
            return;
        }

        // Create announcement data
        Map<String, Object> announcement = new HashMap<>();
        announcement.put("title", title);
        announcement.put("content", content);
        announcement.put("classroomId", classroomId);
        announcement.put("createdBy", userId);
        announcement.put("createdAt", new Timestamp(new Date()));

        // Add announcement to Firestore
        db.collection("announcements")
                .add(announcement)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Đã đăng thông báo", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    setInProgress(false);
                });
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            btnPost.setEnabled(false);
            btnPost.setAlpha(0.5f);
        } else {
            progressBar.setVisibility(View.GONE);
            btnPost.setEnabled(true);
            btnPost.setAlpha(1f);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
