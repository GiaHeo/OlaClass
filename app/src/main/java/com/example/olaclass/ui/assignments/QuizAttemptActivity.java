package com.example.olaclass.ui.assignments;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.olaclass.R;
import com.example.olaclass.data.model.Question;
import com.example.olaclass.data.model.Quiz;
import com.example.olaclass.data.model.QuestionSet;
import com.example.olaclass.data.model.QuizAttempt;
import com.example.olaclass.data.repository.QuizRepository;
import com.example.olaclass.data.repository.QuizAttemptRepository;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;

public class QuizAttemptActivity extends AppCompatActivity {
    private RecyclerView recyclerViewQuestions;
    private QuestionAdapter questionAdapter;
    private QuizRepository quizRepository;
    private QuizAttemptRepository quizAttemptRepository;
    private FirebaseAuth mAuth;
    private String quizId;
    private List<Question> currentQuestions = new ArrayList<>();
    private Quiz currentQuiz;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_attempt);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Làm bài kiểm tra");

        recyclerViewQuestions = findViewById(R.id.recycler_view_questions);
        recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(this));

        quizRepository = new QuizRepository();
        quizAttemptRepository = new QuizAttemptRepository();
        mAuth = FirebaseAuth.getInstance();
        quizId = getIntent().getStringExtra("quizId");
        if (quizId == null || quizId.isEmpty()) {
            Log.e("QuizAttemptActivity", "quizId is null or empty");
            return;
        }
        loadQuizAndQuestions();

        Button btnSubmitQuiz = findViewById(R.id.btn_submit_quiz);
        btnSubmitQuiz.setOnClickListener(v -> {
            // Lưu kết quả bài làm lên Firestore
            String studentId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
            if (studentId == null) {
                Toast.makeText(this, "Không xác định được người dùng.", Toast.LENGTH_LONG).show();
                return;
            }
            List<QuizAttempt.Answer> answers = new ArrayList<>();
            int correct = 0;
            int total = currentQuestions.size();
            for (int i = 0; i < total; i++) {
                Question q = currentQuestions.get(i);
                int checkedId = ((android.widget.RadioGroup) recyclerViewQuestions.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.radio_group_options)).getCheckedRadioButtonId();
                String selectedContent = null;
                boolean isCorrect = false;
                if (checkedId != -1 && q.getOptions() != null) {
                    android.widget.RadioButton rb = recyclerViewQuestions.findViewHolderForAdapterPosition(i).itemView.findViewById(checkedId);
                    selectedContent = rb.getText().toString();
                    for (Question.Option op : q.getOptions()) {
                        if (op.getContent().equals(selectedContent) && op.isCorrect()) {
                            isCorrect = true;
                            break;
                        }
                    }
                }
                if (isCorrect) correct++;
                answers.add(new QuizAttempt.Answer(q.getContent(), selectedContent, isCorrect));
            }
            int score = (int) Math.round((correct * 1.0 / total) * 100);
            long now = System.currentTimeMillis();
            QuizAttempt attempt = new QuizAttempt(null, studentId, quizId, currentQuiz != null ? currentQuiz.getClassroomId() : null, now, score, answers);
            quizAttemptRepository.saveQuizAttempt(attempt)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Bạn đã nộp bài thành công!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi nộp bài: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
        });
    }

    private void loadQuizAndQuestions() {
        quizRepository.getQuiz(quizId)
            .addOnSuccessListener(quiz -> {
                if (quiz == null) {
                    Log.e("QuizAttemptActivity", "Quiz not found");
                    return;
                }
                currentQuiz = quiz;
                QuestionSet questionSet = quiz.getQuestionSet();
                List<Question> questions = (questionSet != null && questionSet.getQuestions() != null)
                        ? questionSet.getQuestions() : new ArrayList<>();
                currentQuestions = questions;
                questionAdapter = new QuestionAdapter(questions);
                recyclerViewQuestions.setAdapter(questionAdapter);
            })
            .addOnFailureListener(e -> {
                Log.e("QuizAttemptActivity", "Error loading quiz: " + e.getMessage());
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