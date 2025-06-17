package com.example.olaclass.ui.assignments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olaclass.R;
import com.example.olaclass.data.model.Quiz;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AssignmentsTeacherFragment extends Fragment implements QuizAdapter.OnItemClickListener {

    private AssignmentsTeacherViewModel viewModel;
    private QuizAdapter adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignments_teacher, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view_question_sets); // Assuming this ID is for quizzes now
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new QuizAdapter(); // Change to QuizAdapter
        adapter.setOnItemClickListener(this); // Set the click listener
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(AssignmentsTeacherViewModel.class);
        viewModel.getQuizzes().observe(getViewLifecycleOwner(), quizzes -> { // Observe quizzes
            if (quizzes != null) {
                adapter.setQuizzes(quizzes);
            }
        });

        viewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        FloatingActionButton fabAddAssignment = view.findViewById(R.id.fab_add_assignment);
        fabAddAssignment.setOnClickListener(v -> {
            // This FAB should now lead to creating a new Quiz, not QuestionSet
            Intent intent = new Intent(getActivity(), CreateQuizActivity.class); // Change to CreateQuizActivity
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadTeacherQuizzes(); // New method to load teacher's quizzes
    }

    @Override
    public void onItemClick(Quiz quiz) {
        // Handle item click: open QuizDetailTeacherActivity for viewing quiz details
        Intent intent = new Intent(getActivity(), QuizDetailTeacherActivity.class);
        intent.putExtra("quizId", quiz.getId());
        // Pass classroomId if needed for QuizDetailTeacherActivity (it seems to be)
        intent.putExtra("classroomId", quiz.getClassroomId()); 
        startActivity(intent);
    }
} 