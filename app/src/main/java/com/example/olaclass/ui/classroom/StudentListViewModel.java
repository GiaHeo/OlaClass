package com.example.olaclass.ui.classroom;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.example.olaclass.data.model.Student;
import com.example.olaclass.data.repository.ClassroomRepository;

import java.util.List;

public class StudentListViewModel extends ViewModel {
    private final ClassroomRepository classroomRepository;
    private final MutableLiveData<List<Student>> _students = new MutableLiveData<>();
    public LiveData<List<Student>> students = _students;

    private final String classroomId;

    public StudentListViewModel(String classroomId, ClassroomRepository classroomRepository) {
        this.classroomId = classroomId;
        this.classroomRepository = classroomRepository;
        loadStudents();
    }

    public void loadStudents() {
        Log.d("StudentListViewModel", "loadStudents() called for classroomId: " + classroomId);
        if (classroomId == null || classroomId.isEmpty()) {
            Log.e("StudentListViewModel", "Classroom ID is null or empty. Cannot load students.");
            _students.setValue(null);
            return;
        }

        classroomRepository.getStudentsForClass(classroomId)
            .addOnSuccessListener(studentsList -> {
                Log.d("StudentListViewModel", "Successfully loaded " + studentsList.size() + " students.");
                _students.setValue(studentsList);
            })
            .addOnFailureListener(e -> {
                Log.e("StudentListViewModel", "Error loading students: " + e.getMessage(), e);
                _students.setValue(null);
            });
    }

    // Factory for StudentListViewModel
    public static class Factory implements ViewModelProvider.Factory {
        private final String classroomId;
        private final ClassroomRepository classroomRepository;

        public Factory(String classroomId, ClassroomRepository classroomRepository) {
            this.classroomId = classroomId;
            this.classroomRepository = classroomRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(StudentListViewModel.class)) {
                return (T) new StudentListViewModel(classroomId, classroomRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
} 