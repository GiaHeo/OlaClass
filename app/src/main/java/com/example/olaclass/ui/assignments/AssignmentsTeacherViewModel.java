package com.example.olaclass.ui.assignments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.olaclass.data.model.QuestionSet;
import com.example.olaclass.data.repository.QuestionRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AssignmentsTeacherViewModel extends ViewModel {

    private final QuestionRepository questionRepository;
    private final MutableLiveData<List<QuestionSet>> _questionSets = new MutableLiveData<>();
    public final LiveData<List<QuestionSet>> questionSets = _questionSets;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public final LiveData<String> errorMessage = _errorMessage;

    private final FirebaseAuth mAuth;

    public AssignmentsTeacherViewModel() {
        questionRepository = new QuestionRepository();
        mAuth = FirebaseAuth.getInstance();
        loadQuestionSets();
    }

    public void loadQuestionSets() {
        questionRepository.getAllQuestionSets()
            .addOnSuccessListener(questionSetList -> {
                _questionSets.setValue(questionSetList);
            })
            .addOnFailureListener(e -> {
                _errorMessage.setValue("Lỗi tải bộ câu hỏi: " + e.getMessage());
            });
    }
}