package com.example.olaclass.ui.assignments;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olaclass.R;
import com.example.olaclass.data.model.QuizScoreDisplayItem;
import com.example.olaclass.data.model.QuizAttempt;
import com.example.olaclass.data.repository.QuizAttemptRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizScoresActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuizScoresAdapter quizScoresAdapter;
    private QuizAttemptRepository quizAttemptRepository;
    private FirebaseFirestore db;
    private String quizId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_scores);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp);
            if (upArrow != null) {
                upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryLight), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Bảng điểm bài kiểm tra");

        recyclerView = findViewById(R.id.recycler_view_quiz_scores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizScoresAdapter = new QuizScoresAdapter(new ArrayList<>());
        recyclerView.setAdapter(quizScoresAdapter);

        quizAttemptRepository = new QuizAttemptRepository();
        db = FirebaseFirestore.getInstance();
        quizId = getIntent().getStringExtra("quizId");
        if (quizId == null || quizId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy quizId", Toast.LENGTH_LONG).show();
            return;
        }
        loadQuizAttempts();
    }

    private void loadQuizAttempts() {
        quizAttemptRepository.getQuizAttemptsByQuizId(quizId)
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<QuizScoreDisplayItem> scoreItems = new ArrayList<>();
                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    QuizAttempt attempt = doc.toObject(QuizAttempt.class);
                    // Lấy tên học sinh từ bảng users
                    db.collection("users").document(attempt.getStudentId()).get()
                        .addOnSuccessListener(userDoc -> {
                            String studentName = userDoc.getString("displayName");
                            String studentEmail = userDoc.getString("email");
                            if (studentName == null || studentName.isEmpty()) {
                                studentName = attempt.getStudentId();
                            }
                            if (studentEmail == null || studentEmail.isEmpty()) {
                                studentEmail = "(không có email)";
                            }
                            int correct = 0;
                            int total = 0;
                            if (attempt.getAnswers() != null) {
                                total = attempt.getAnswers().size();
                                for (QuizAttempt.Answer ans : attempt.getAnswers()) {
                                    if (ans.isCorrect()) correct++;
                                }
                            }
                            double score = attempt.getScore() != null ? attempt.getScore() : 0;
                            long timeTaken = attempt.getSubmissionTime() != null ? attempt.getSubmissionTime() : 0;
                            long startTime = attempt.getSubmissionTime() != null ? attempt.getSubmissionTime() : 0;
                            QuizScoreDisplayItem item = new QuizScoreDisplayItem(
                                attempt.getStudentId(),
                                studentName,
                                score,
                                correct,
                                total,
                                timeTaken,
                                startTime
                            );
                            scoreItems.add(item);
                            quizScoresAdapter.setQuizAttempts(new ArrayList<>(scoreItems));
                        });
                }
                if (queryDocumentSnapshots.isEmpty()) {
                    Toast.makeText(this, "Chưa có học sinh nào nộp bài.", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Lỗi tải danh sách học sinh làm bài: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("QuizScoresActivity", "Error: " + e.getMessage());
            });
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