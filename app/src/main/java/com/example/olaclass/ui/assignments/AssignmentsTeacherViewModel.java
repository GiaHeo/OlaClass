package com.example.olaclass.ui.assignments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.olaclass.data.model.Quiz;
import com.example.olaclass.data.repository.QuizRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AssignmentsTeacherViewModel extends ViewModel {

    private final QuizRepository quizRepository;
    private final MutableLiveData<List<Quiz>> _quizzes = new MutableLiveData<>();
    public LiveData<List<Quiz>> getQuizzes() {
        return _quizzes;
    }

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    private final FirebaseAuth mAuth;

    public AssignmentsTeacherViewModel() {
        quizRepository = new QuizRepository();
        mAuth = FirebaseAuth.getInstance();
        loadTeacherQuizzes();
    }

    public void loadTeacherQuizzes() {
        String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (currentUserId == null) {
            _errorMessage.setValue("Không tìm thấy ID người dùng.");
            return;
        }

        quizRepository.getQuizzesByCreator(currentUserId)
            .addOnSuccessListener(quizList -> {
                _quizzes.setValue(quizList);
            })
            .addOnFailureListener(e -> {
                _errorMessage.setValue("Lỗi tải bài kiểm tra: " + e.getMessage());
            });
    }
} 