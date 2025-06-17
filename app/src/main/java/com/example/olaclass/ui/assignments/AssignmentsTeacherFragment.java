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
import com.example.olaclass.data.model.QuestionSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AssignmentsTeacherFragment extends Fragment implements QuestionSetAdapter.OnItemClickListener {

    private AssignmentsTeacherViewModel viewModel;
    private QuestionSetAdapter adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignments_teacher, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view_question_sets);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new QuestionSetAdapter();
        adapter.setOnItemClickListener(this); // Set the click listener
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(AssignmentsTeacherViewModel.class);
        viewModel.questionSets.observe(getViewLifecycleOwner(), questionSets -> {
            adapter.setQuestionSets(questionSets);
        });

        viewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        FloatingActionButton fabAddAssignment = view.findViewById(R.id.fab_add_assignment);
        fabAddAssignment.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateQuestionSetActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadQuestionSets(); // Reload data when returning to the fragment
    }

    @Override
    public void onItemClick(QuestionSet questionSet) {
        // Handle item click: open CreateQuestionSetActivity for editing
        Intent intent = new Intent(getActivity(), CreateQuestionSetActivity.class);
        intent.putExtra("questionSetId", questionSet.getId());
        startActivity(intent);
    }
} 