package com.example.olaclass.ui.assignments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.olaclass.data.model.QuestionSet;
import com.example.olaclass.data.repository.QuestionRepository;

import java.util.List;

public class AssignmentsTeacherViewModel extends ViewModel {

    private final QuestionRepository questionRepository;
    private final MutableLiveData<List<QuestionSet>> _questionSets = new MutableLiveData<>();
    public LiveData<List<QuestionSet>> questionSets = _questionSets;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    public AssignmentsTeacherViewModel() {
        questionRepository = new QuestionRepository();
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