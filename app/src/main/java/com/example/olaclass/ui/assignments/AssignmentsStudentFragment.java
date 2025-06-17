package com.example.olaclass.ui.assignments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olaclass.R;
import com.example.olaclass.data.model.Quiz;
import com.example.olaclass.ui.assignments.QuizAdapter;
import com.example.olaclass.ui.assignments.QuizAttemptActivity;
import com.example.olaclass.ui.assignments.QuizDetailTeacherActivity;
import com.example.olaclass.ui.classroom.ClassroomDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AssignmentsStudentFragment extends Fragment implements QuizAdapter.OnItemClickListener {

    private AssignmentsStudentViewModel viewModel;
    private QuizAdapter adapter;
    private RecyclerView recyclerView;
    private TextView noQuizzesTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignments_student, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_quizzes);
        noQuizzesTextView = view.findViewById(R.id.text_view_no_quizzes);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new QuizAdapter();
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(AssignmentsStudentViewModel.class);
        viewModel.unattemptedQuizzes.observe(getViewLifecycleOwner(), quizzes -> {
            if (quizzes != null && !quizzes.isEmpty()) {
                adapter.setQuizzes(quizzes);
                recyclerView.setVisibility(View.VISIBLE);
                noQuizzesTextView.setVisibility(View.GONE);
            } else {
                adapter.setQuizzes(new ArrayList<>()); // Clear existing data
                recyclerView.setVisibility(View.GONE);
                noQuizzesTextView.setVisibility(View.VISIBLE);
            }
        });

        viewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadUnattemptedQuizzes(); // Reload data when returning to the fragment
    }

    @Override
    public void onItemClick(Quiz quiz) {
        // Handle quiz click: Navigate to QuizAttemptActivity for students
        Toast.makeText(getContext(), "Bắt đầu bài kiểm tra: " + quiz.getTitle(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), QuizAttemptActivity.class);
        intent.putExtra("quizId", quiz.getId());
        intent.putExtra("classroomId", quiz.getClassroomId()); // Pass classroomId if needed
        startActivity(intent);
    }
} 