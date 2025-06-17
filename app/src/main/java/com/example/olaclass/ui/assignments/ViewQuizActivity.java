package com.example.olaclass.ui.assignments;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.olaclass.R;
import com.example.olaclass.data.model.Question;
import com.example.olaclass.data.model.Quiz;
import com.example.olaclass.data.repository.QuizRepository;

public class ViewQuizActivity extends AppCompatActivity {
    private static final String TAG = "ViewQuizActivity";
    private QuizRepository quizRepository;
    private String quizId;
    private TextView quizContentDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_quiz);

        quizId = getIntent().getStringExtra("quizId");

        if (quizId == null || quizId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy bài kiểm tra", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        quizContentDisplay = findViewById(R.id.quiz_content_display);

        setupToolbar();

        quizRepository = new QuizRepository();

        loadQuizDetails();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Đề kiểm tra");
    }

    private void loadQuizDetails() {
        quizRepository.getQuiz(quizId)
                .addOnSuccessListener(quiz -> {
                    if (quiz != null) {
                        displayQuizContent(quiz);
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

    private void displayQuizContent(Quiz quiz) {
        StringBuilder content = new StringBuilder();
        if (quiz.getQuestionSet() != null && quiz.getQuestionSet().getQuestions() != null) {
            content.append("Tên bài kiểm tra: ").append(quiz.getTitle()).append("\n\n");
            content.append("Người tạo: ").append(quiz.getCreatorName()).append("\n\n");
            content.append("---");
            content.append("\nĐề kiểm tra:");
            int questionNumber = 1;
            for (Question question : quiz.getQuestionSet().getQuestions()) {
                content.append("\n\nCâu ").append(questionNumber++).append(": ").append(question.getContent());
                if (question.getOptions() != null) {
                    char optionLetter = 'A';
                    for (Question.Option option : question.getOptions()) {
                        content.append("\n").append(optionLetter++).append(". ").append(option.getContent());
                        if (option.isCorrect()) {
                            content.append(" (Đáp án đúng)");
                        }
                    }
                }
            }
            content.append("\n---");
        } else {
            content.append("Không có câu hỏi nào để hiển thị.");
        }
        quizContentDisplay.setText(content.toString());
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