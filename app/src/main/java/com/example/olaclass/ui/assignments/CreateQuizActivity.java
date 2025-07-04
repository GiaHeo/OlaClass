package com.example.olaclass.ui.assignments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.olaclass.R;
import com.example.olaclass.data.model.QuestionSet;
import com.example.olaclass.data.model.Quiz;
import com.example.olaclass.data.repository.QuestionRepository;
import com.example.olaclass.data.repository.QuizRepository;
import com.example.olaclass.ui.auth.UserProfile; // Assuming UserProfile exists for creator info
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateQuizActivity extends AppCompatActivity {

    private EditText quizTitleEditText;
    private Spinner questionSetSpinner;
    private Button startTimeButton;
    private Button endTimeButton;
    private EditText durationEditText;
    private Button createQuizButton;
    private Button createQuestionSetButton;
    private Button showQuestionSetsButton;
    private TextView selectedQuestionSetInfo;

    private QuestionRepository questionRepository;
    private QuizRepository quizRepository;

    private List<QuestionSet> questionSets = new ArrayList<>();
    private ArrayAdapter<String> questionSetAdapter;

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();

    private String classroomId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tạo bài kiểm tra");
        }

        quizTitleEditText = findViewById(R.id.edit_text_quiz_title);
        questionSetSpinner = findViewById(R.id.spinner_question_sets);
        startTimeButton = findViewById(R.id.button_start_time);
        endTimeButton = findViewById(R.id.button_end_time);
        durationEditText = findViewById(R.id.edit_text_duration);
        createQuizButton = findViewById(R.id.button_create_quiz);
        createQuestionSetButton = findViewById(R.id.button_create_question_set);
        showQuestionSetsButton = findViewById(R.id.button_show_question_sets);
        selectedQuestionSetInfo = findViewById(R.id.text_selected_question_set_info);

        questionRepository = new QuestionRepository();
        quizRepository = new QuizRepository();

        // classroomId is optional - if not provided, the quiz won't be associated with a specific classroom
        classroomId = getIntent().getStringExtra("classroomId");

        // Update UI to show if this is a classroom-specific quiz
        if (classroomId == null || classroomId.isEmpty()) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Tạo bộ câu hỏi mới");
            }
        }
        
        loadQuestionSets();
        setupDateTimePickers();

        createQuizButton.setOnClickListener(v -> createNewQuiz());
        
        // Setup button listeners
        createQuestionSetButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreateQuizActivity.this, CreateQuestionSetActivity.class);
            startActivity(intent);
        });
        
        showQuestionSetsButton.setOnClickListener(v -> showQuestionSetsDialog());
    }

    private void loadQuestionSets() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Lỗi: Người dùng chưa đăng nhập.", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = currentUser.getUid();
        
        questionRepository.getQuestionSetsByCreator(currentUserId)
            .addOnSuccessListener(sets -> {
                questionSets.clear();
                questionSets.addAll(sets);
                List<String> titles = new ArrayList<>();
                for (QuestionSet set : sets) {
                    titles.add(set.getTitle());
                }
                
                if (titles.isEmpty()) {
                    Toast.makeText(this, "Bạn chưa có bộ câu hỏi nào. Vui lòng tạo bộ câu hỏi trước.", Toast.LENGTH_LONG).show();
                }
                
                questionSetAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, titles);
                questionSetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                questionSetSpinner.setAdapter(questionSetAdapter);
                
                // Add listener to spinner to update info text
                questionSetSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                        updateSelectedQuestionSetInfo(position);
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {
                        selectedQuestionSetInfo.setVisibility(View.GONE);
                    }
                });
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Lỗi tải bộ câu hỏi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private void setupDateTimePickers() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        startTimeButton.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
                startCalendar.set(Calendar.YEAR, year);
                startCalendar.set(Calendar.MONTH, monthOfYear);
                startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                    startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    startCalendar.set(Calendar.MINUTE, minute);
                    startTimeButton.setText("Bắt đầu: " + dateFormat.format(startCalendar.getTime()));
                }, startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE), true).show();
            }, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        endTimeButton.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
                endCalendar.set(Calendar.YEAR, year);
                endCalendar.set(Calendar.MONTH, monthOfYear);
                endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                    endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    endCalendar.set(Calendar.MINUTE, minute);
                    endTimeButton.setText("Kết thúc: " + dateFormat.format(endCalendar.getTime()));
                }, endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE), true).show();
            }, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void createNewQuiz() {
        String title = quizTitleEditText.getText().toString().trim();
        String durationStr = durationEditText.getText().toString().trim();

        if (title.isEmpty()) {
            quizTitleEditText.setError("Tên bài kiểm tra không được trống");
            quizTitleEditText.requestFocus();
            return;
        }

        if (questionSetSpinner.getSelectedItemPosition() == Spinner.INVALID_POSITION) {
            Toast.makeText(this, "Vui lòng chọn một bộ câu hỏi.", Toast.LENGTH_SHORT).show();
            return;
        }

        QuestionSet selectedQuestionSet = questionSets.get(questionSetSpinner.getSelectedItemPosition());

        if (startTimeButton.getText().toString().equals("Chọn thời gian bắt đầu") ||
            endTimeButton.getText().toString().equals("Chọn thời gian kết thúc")) {
            Toast.makeText(this, "Vui lòng chọn thời gian bắt đầu và kết thúc.", Toast.LENGTH_SHORT).show();
            return;
        }

        long startTimeMillis = startCalendar.getTimeInMillis();
        long endTimeMillis = endCalendar.getTimeInMillis();

        if (endTimeMillis <= startTimeMillis) {
            Toast.makeText(this, "Thời gian kết thúc phải sau thời gian bắt đầu.", Toast.LENGTH_SHORT).show();
            return;
        }

        long duration;
        try {
            duration = Long.parseLong(durationStr);
            if (duration <= 0) {
                durationEditText.setError("Thời gian làm bài phải lớn hơn 0.");
                durationEditText.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            durationEditText.setError("Thời gian làm bài không hợp lệ.");
            durationEditText.requestFocus();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Lỗi: Người dùng chưa đăng nhập.", Toast.LENGTH_SHORT).show();
            return;
        }

        String creatorId = currentUser.getUid();
        String creatorName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Unknown Creator";
        // Fallback if DisplayName is null (e.g., email-only login)
        if (creatorName.equals("Unknown Creator") && currentUser.getEmail() != null) {
            creatorName = currentUser.getEmail();
        }

        Quiz newQuiz = new Quiz(
            null, // ID will be set by repository
            title,
            creatorId,
            creatorName,
            classroomId,
            selectedQuestionSet,
            startTimeMillis,
            endTimeMillis,
            duration,
            System.currentTimeMillis()
        );

        quizRepository.saveQuiz(newQuiz)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Tạo bài kiểm tra thành công!", Toast.LENGTH_SHORT).show();
                // Navigate to QuizDetailTeacherActivity
                Intent intent = new Intent(CreateQuizActivity.this, QuizDetailTeacherActivity.class);
                intent.putExtra("quizId", newQuiz.getId());
                intent.putExtra("classroomId", newQuiz.getClassroomId());
                startActivity(intent);
                finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Lỗi khi tạo bài kiểm tra: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private void showQuestionSetsDialog() {
        if (questionSets.isEmpty()) {
            Toast.makeText(this, "Bạn chưa có bộ câu hỏi nào.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] questionSetTitles = new String[questionSets.size()];
        for (int i = 0; i < questionSets.size(); i++) {
            QuestionSet set = questionSets.get(i);
            int questionCount = set.getQuestions() != null ? set.getQuestions().size() : 0;
            questionSetTitles[i] = set.getTitle() + " (" + questionCount + " câu hỏi)";
        }
        
        new AlertDialog.Builder(this)
            .setTitle("Chọn bộ câu hỏi")
            .setItems(questionSetTitles, (dialog, which) -> {
                QuestionSet selectedSet = questionSets.get(which);
                // Tìm vị trí của bộ câu hỏi được chọn trong spinner
                for (int i = 0; i < questionSets.size(); i++) {
                    if (questionSets.get(i).getId().equals(selectedSet.getId())) {
                        questionSetSpinner.setSelection(i);
                        break;
                    }
                }
                dialog.dismiss();
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void updateSelectedQuestionSetInfo(int position) {
        if (position >= 0 && position < questionSets.size()) {
            QuestionSet selectedSet = questionSets.get(position);
            int questionCount = selectedSet.getQuestions() != null ? selectedSet.getQuestions().size() : 0;
            String info = "Bộ câu hỏi: " + selectedSet.getTitle() + "\nSố câu hỏi: " + questionCount;
            selectedQuestionSetInfo.setText(info);
            selectedQuestionSetInfo.setVisibility(View.VISIBLE);
        } else {
            selectedQuestionSetInfo.setVisibility(View.GONE);
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
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh question sets when returning from CreateQuestionSetActivity
        loadQuestionSets();
    }
} 