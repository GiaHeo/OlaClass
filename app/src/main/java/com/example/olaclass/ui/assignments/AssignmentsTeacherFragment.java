package com.example.olaclass.ui.assignments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.olaclass.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AssignmentsTeacherFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignments_teacher, container, false);
        
        FloatingActionButton fabAddAssignment = view.findViewById(R.id.fab_add_assignment);
        fabAddAssignment.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateQuestionSetActivity.class);
            startActivity(intent);
        });

        // TODO: Implement teacher-specific assignment creation and management logic here
        // This could include a RecyclerView for assignments, a FAB for creating new assignments,
        // and logic to handle assignment events.

        return view;
    }
} 