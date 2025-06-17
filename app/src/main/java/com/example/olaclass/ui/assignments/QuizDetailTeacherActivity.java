package com.example.olaclass.ui.assignments;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.olaclass.R;
import com.example.olaclass.data.model.Quiz;
import com.example.olaclass.data.repository.QuizRepository;
import com.google.firebase.firestore.DocumentSnapshot;

public class QuizDetailTeacherActivity extends AppCompatActivity {
    private static final String TAG = "QuizDetailTeacherActivity";
    private QuizRepository quizRepository;
    private String quizId;
    private String classroomId;
    private Quiz currentQuiz;
    private TextView quizContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_detail_teacher);
        
        // Get quiz ID from intent
        quizId = getIntent().getStringExtra("quizId");
        classroomId = getIntent().getStringExtra("classroomId");
        
        if (quizId == null || quizId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy bài kiểm tra", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize views
        quizContent = findViewById(R.id.quiz_content);
        
        // Setup toolbar
        setupToolbar();
        
        // Initialize repository
        quizRepository = new QuizRepository();
        
        // Load quiz details
        loadQuizDetails();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }
    
    private void loadQuizDetails() {
        quizRepository.getQuiz(quizId)
            .addOnSuccessListener(quiz -> {
                if (quiz != null) {
                    currentQuiz = quiz;
                    updateUI();
                } else {
                    Toast.makeText(this, "Không tìm thấy thông tin bài kiểm tra", Toast.LENGTH_SHORT).show();
                    finish();
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading quiz: " + e.getMessage(), e);
                Toast.makeText(this, "Lỗi tải thông tin bài kiểm tra", Toast.LENGTH_SHORT).show();
                finish();
            });
    }
    
    private void updateUI() {
        if (currentQuiz != null) {
            // Set toolbar title
            TextView toolbarTitle = findViewById(R.id.toolbar_title);
            toolbarTitle.setText(currentQuiz.getTitle());
            
            // Show a toast with the activity name
            Toast.makeText(this, "QuizDetailActivity: " + currentQuiz.getTitle(), 
                Toast.LENGTH_SHORT).show();
                
            // Update quiz content
            StringBuilder content = new StringBuilder();
            content.append("Người tạo: ").append(currentQuiz.getCreatorName()).append("\n\n");
            content.append("Thời gian bắt đầu: ").append(formatDate(currentQuiz.getStartTime())).append("\n");
            content.append("Thời gian kết thúc: ").append(formatDate(currentQuiz.getEndTime())).append("\n");
            content.append("Thời gian làm bài: ").append(currentQuiz.getDuration()).append(" phút\n");
            
            // Add question set info if available
            if (currentQuiz.getQuestionSet() != null) {
                content.append("\nBộ câu hỏi: ").append(currentQuiz.getQuestionSet().getTitle()).append("\n");
                content.append("Số câu hỏi: ").append(
                    currentQuiz.getQuestionSet().getQuestions() != null ? 
                    currentQuiz.getQuestionSet().getQuestions().size() : 0
                );
            }
                
            quizContent.setText(content.toString());
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_quiz_detail, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_view_quiz) {
            android.content.Intent intent = new android.content.Intent(this, com.example.olaclass.ui.assignments.ViewQuizActivity.class);
            intent.putExtra("quizId", quizId);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_delete_quiz) {
            showDeleteConfirmation();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void showDeleteConfirmation() {
        if (currentQuiz == null) return;
        
        new AlertDialog.Builder(this)
            .setTitle("Xóa bài kiểm tra")
            .setMessage("Bạn có chắc chắn muốn xóa bài kiểm tra này?")
            .setPositiveButton("Xóa", (dialog, which) -> deleteQuiz())
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    private String formatDate(long timestamp) {
        if (timestamp <= 0) {
            return "Chưa đặt";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    private void deleteQuiz() {
        if (currentQuiz == null) return;
        
        // TODO: Implement delete quiz
        Toast.makeText(this, "Đang xóa bài kiểm tra...", Toast.LENGTH_SHORT).show();
        
        // Example deletion code (uncomment and implement as needed)
        /*
        quizRepository.deleteQuiz(quizId)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Đã xóa bài kiểm tra", Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error deleting quiz: " + e.getMessage(), e);
                Toast.makeText(this, "Lỗi khi xóa bài kiểm tra", Toast.LENGTH_SHORT).show();
            });
        */
    }
}
