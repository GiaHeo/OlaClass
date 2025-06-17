package com.example.olaclass.ui.assignments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.olaclass.R;
import com.example.olaclass.data.model.Question;
import com.example.olaclass.data.model.QuestionSet;
import com.example.olaclass.data.repository.QuestionRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CreateQuestionSetActivity extends AppCompatActivity {

    private EditText questionSetTitleEditText;
    private LinearLayout questionsContainer;
    private Toolbar toolbar;

    private QuestionRepository questionRepository;
    private QuestionSet currentQuestionSet;
    private String questionSetId; // ID của bộ câu hỏi hiện tại
    private Disposable saveDisposable; // For debouncing saves

    private static final String TAG = "CreateQuestionSetActivity";
    private static final long SAVE_DEBOUNCE_TIME = 1000; // 1 second

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question_set);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tạo bộ câu hỏi");
        }

        questionSetTitleEditText = findViewById(R.id.edit_text_question_set_title);
        questionsContainer = findViewById(R.id.questions_container);

        questionRepository = new QuestionRepository();

        // Get questionSetId from Intent, if editing an existing set
        questionSetId = getIntent().getStringExtra("questionSetId");

        if (questionSetId != null && !questionSetId.isEmpty()) {
            loadQuestionSet(questionSetId);
        } else {
            // Create a new question set and save it to get an ID for auto-saving
            currentQuestionSet = new QuestionSet();
            currentQuestionSet.setTitle("");
            currentQuestionSet.setQuestions(new ArrayList<>());
            saveQuestionSet(currentQuestionSet); // Initial save to get an ID
        }

        // Listen for title changes to trigger auto-save
        questionSetTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (currentQuestionSet != null) {
                    currentQuestionSet.setTitle(s.toString());
                    debounceSave();
                }
            }
        });
    }

    private void loadQuestionSet(String id) {
        questionRepository.getQuestionSet(id)
            .addOnSuccessListener(questionSet -> {
                if (questionSet != null) {
                    currentQuestionSet = questionSet;
                    questionSetTitleEditText.setText(questionSet.getTitle());
                    renderQuestions();
                } else {
                    Toast.makeText(this, "Không tìm thấy bộ câu hỏi", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Question set not found for ID: " + id);
                    finish();
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Lỗi tải bộ câu hỏi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error loading question set: " + e.getMessage(), e);
                finish();
            });
    }

    private void renderQuestions() {
        questionsContainer.removeAllViews(); // Clear existing views
        if (currentQuestionSet != null && currentQuestionSet.getQuestions() != null) {
            for (Question question : currentQuestionSet.getQuestions()) {
                addQuestionLayout(question); // Re-render existing questions
            }
        }
    }

    private void addQuestionLayout(@Nullable Question existingQuestion) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View questionView = inflater.inflate(R.layout.item_question_content, questionsContainer, false);

        EditText questionContentEditText = questionView.findViewById(R.id.edit_text_question_content);
        Button removeQuestionButton = questionView.findViewById(R.id.button_remove_question);
        LinearLayout optionsContainer = questionView.findViewById(R.id.options_container);
        Button addOptionButton = questionView.findViewById(R.id.button_add_option);

        Question question; // Declare question here

        if (existingQuestion == null) {
            question = new Question();
            question.setType("text"); // Default type
            question.setContent("");
            if (currentQuestionSet.getQuestions() == null) {
                currentQuestionSet.setQuestions(new ArrayList<>());
            }
            currentQuestionSet.getQuestions().add(question);
        } else {
            question = existingQuestion;
            questionContentEditText.setText(question.getContent());
        }

        questionsContainer.addView(questionView);

        // Set up click listener for adding options
        addOptionButton.setOnClickListener(v -> {
            addQuestionOptionLayout(optionsContainer, question, null); // Add a new empty option
        });

        // If existing question, render its options
        if (existingQuestion != null && existingQuestion.getOptions() != null) {
            for (Question.Option option : existingQuestion.getOptions()) {
                addQuestionOptionLayout(optionsContainer, question, option);
            }
        }

        // Listen for content changes for this question to trigger auto-save
        final Question finalQuestion = question; // Effective final for lambda
        questionContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                finalQuestion.setContent(s.toString());
                debounceSave();
            }
        });

        removeQuestionButton.setOnClickListener(v -> {
            removeQuestionLayout(questionView, finalQuestion);
        });

        debounceSave(); // Trigger save after adding a new question or re-rendering
    }

    private void addQuestionOptionLayout(LinearLayout container, Question question, @Nullable Question.Option existingOption) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View optionView = inflater.inflate(R.layout.item_question_option, container, false);

        EditText optionContentEditText = optionView.findViewById(R.id.edit_text_option_content);
        CheckBox correctAnswerCheckBox = optionView.findViewById(R.id.checkbox_correct_answer);
        Button removeOptionButton = optionView.findViewById(R.id.button_remove_option);

        final Question.Option currentOption;

        if (existingOption == null) {
            currentOption = new Question.Option("", false); // Create a new empty option
            if (question.getOptions() == null) {
                question.setOptions(new ArrayList<>());
            }
            question.getOptions().add(currentOption);
        } else {
            currentOption = existingOption;
            optionContentEditText.setText(existingOption.getContent());
            correctAnswerCheckBox.setChecked(existingOption.isCorrect());
        }

        container.addView(optionView);

        // Handle option content changes
        optionContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentOption.setContent(s.toString());
                debounceSave();
            }
        });

        // Handle correct answer checkbox changes
        correctAnswerCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentOption.setCorrect(isChecked);
            debounceSave();
        });

        // Handle remove option button click
        removeOptionButton.setOnClickListener(v -> {
            container.removeView(optionView);
            question.getOptions().remove(currentOption);
            debounceSave();
        });

        debounceSave();
    }

    private void removeQuestionLayout(View questionView, Question questionToRemove) {
        questionsContainer.removeView(questionView);
        if (currentQuestionSet != null && currentQuestionSet.getQuestions() != null) {
            currentQuestionSet.getQuestions().remove(questionToRemove);
            debounceSave(); // Trigger save after removing a question
        }
    }

    private void saveQuestionSet(QuestionSet questionSet) {
        if (questionSet == null || questionSet.getId() == null || questionSet.getId().isEmpty()) {
            Log.e(TAG, "Cannot save: QuestionSet or its ID is null/empty.");
            return;
        }

        questionRepository.saveQuestionSet(questionSet)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Question set saved successfully: " + questionSet.getId());
                // No Toast or finish() here, as this is for auto-save/initial save
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error saving question set: " + e.getMessage(), e);
                // No Toast here, as this is for auto-save/initial save
            });
    }

    private QuestionSet createNewQuestionSetCopy(QuestionSet original, String newTitle) {
        QuestionSet newSet = new QuestionSet();
        newSet.setTitle(newTitle);
        newSet.setQuestions(new ArrayList<>(original.getQuestions())); // Deep copy
        newSet.setId(null); // Ensure a new ID is generated by Firestore
        return newSet;
    }

    private void debounceSave() {
        if (saveDisposable != null && !saveDisposable.isDisposed()) {
            saveDisposable.dispose();
        }
        saveDisposable = Observable.timer(SAVE_DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(aLong -> {
                saveQuestionSet(currentQuestionSet);
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (saveDisposable != null && !saveDisposable.isDisposed()) {
            saveDisposable.dispose();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_question_set, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId(); // Get the ID once

        if (itemId == R.id.action_add_question) {
            addQuestionLayout(null);
            return true;
        } else if (itemId == R.id.action_cancel) {
            finish();
            return true;
        } else if (itemId == R.id.action_save) {
            if (currentQuestionSet != null) {
                String title = questionSetTitleEditText.getText().toString();
                currentQuestionSet.setTitle(title);

                final QuestionSet questionSetToSave = currentQuestionSet;

                questionRepository.saveQuestionSet(questionSetToSave)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Question set saved successfully via Save button: " + currentQuestionSet.getId());
                        Toast.makeText(this, "Đã lưu bộ câu hỏi", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error saving question set via Save button: " + e.getMessage(), e);
                        Toast.makeText(this, "Lỗi lưu bộ câu hỏi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
            } else {
                Toast.makeText(this, "Không thể lưu: Bộ câu hỏi chưa được khởi tạo.", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_save_as) {
            if (currentQuestionSet != null) {
                String title = questionSetTitleEditText.getText().toString();
                currentQuestionSet.setTitle(title);

                final QuestionSet questionSetToSave = createNewQuestionSetCopy(currentQuestionSet, title);

                questionRepository.saveQuestionSet(questionSetToSave)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Question set saved as new successfully: " + questionSetToSave.getId());
                        Toast.makeText(this, "Đã lưu bộ câu hỏi thành bộ mới", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error saving question set as new: " + e.getMessage(), e);
                        Toast.makeText(this, "Lỗi lưu bộ câu hỏi thành bộ mới: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
            } else {
                Toast.makeText(this, "Không thể lưu thành: Bộ câu hỏi chưa được khởi tạo.", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_delete_question_set) {
            if (currentQuestionSet != null && currentQuestionSet.getId() != null) {
                new AlertDialog.Builder(this)
                    .setTitle("Xóa bộ câu hỏi")
                    .setMessage("Bạn có chắc chắn muốn xóa bộ câu hỏi này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        questionRepository.deleteQuestionSet(currentQuestionSet.getId())
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Đã xóa bộ câu hỏi", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Lỗi xóa bộ câu hỏi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e(TAG, "Error deleting question set: " + e.getMessage(), e);
                            });
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            } else {
                Toast.makeText(this, "Không thể xóa: Bộ câu hỏi chưa được lưu.", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
} 