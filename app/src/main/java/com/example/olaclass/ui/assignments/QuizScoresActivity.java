package com.example.olaclass.ui.assignments;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olaclass.R;
import com.example.olaclass.data.model.QuizScoreDisplayItem; // Changed from QuizAttempt
import java.util.ArrayList;
import java.util.List;

public class QuizScoresActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuizScoresAdapter quizScoresAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_scores);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(""); // Clear default title
            
            // Set navigation icon tint programmatically
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
        quizScoresAdapter = new QuizScoresAdapter(new ArrayList<>()); // Truyền dữ liệu trống ban đầu
        recyclerView.setAdapter(quizScoresAdapter);

        // TODO: Load actual quiz attempt data from Firestore here
        // For now, populate with dummy data
        List<QuizScoreDisplayItem> dummyScores = createDummyQuizAttempts(); // Changed type
        quizScoresAdapter.setQuizAttempts(dummyScores);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<QuizScoreDisplayItem> createDummyQuizAttempts() {
        List<QuizScoreDisplayItem> attempts = new ArrayList<>();
        // Example dummy data using QuizScoreDisplayItem constructor
        attempts.add(new QuizScoreDisplayItem("student1", "Nguyễn Văn A", 80.0, 4, 5, 120000, System.currentTimeMillis() - 3600000));
        attempts.add(new QuizScoreDisplayItem("student2", "Trần Thị B", 95.0, 5, 5, 180000, System.currentTimeMillis() - 7200000));
        attempts.add(new QuizScoreDisplayItem("student3", "Lê Văn C", 60.0, 3, 5, 90000, System.currentTimeMillis() - 1800000));
        return attempts;
    }
} 