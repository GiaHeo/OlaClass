package com.example.olaclass.ui.classroom;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.olaclass.data.repository.ClassroomRepository;

public class ClassroomViewModelFactory implements ViewModelProvider.Factory {
    private final String userRole;
    private final ClassroomRepository classroomRepository;

    public ClassroomViewModelFactory(ClassroomRepository classroomRepository, String userRole) {
        this.classroomRepository = classroomRepository;
        this.userRole = userRole;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ClassroomViewModel.class)) {
            return (T) new ClassroomViewModel(classroomRepository, userRole);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
} 