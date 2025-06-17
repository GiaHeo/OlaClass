package com.example.olaclass.ui.classroom;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olaclass.R;
import com.example.olaclass.data.model.Quiz;
import com.example.olaclass.data.repository.QuizRepository;
import com.example.olaclass.ui.assignments.QuizAdapter;

import java.util.ArrayList;
import java.util.List;

public class AssignmentInviteFragment extends Fragment implements QuizAdapter.OnItemClickListener {

    private RecyclerView quizzesRecyclerView;
    private QuizAdapter quizAdapter;
    private QuizRepository quizRepository;
    private String classroomId;

    private static final String TAG = "AssignmentInviteFrag";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignment_invite, container, false);

        if (getArguments() != null) {
            classroomId = getArguments().getString("classroomId");
            Log.d(TAG, "Classroom ID received: " + classroomId);
        }

        if (classroomId == null || classroomId.isEmpty()) {
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy ID lớp học cho bài kiểm tra.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Classroom ID is null or empty.");
            return view; // Or handle this error more gracefully
        }

        quizzesRecyclerView = view.findViewById(R.id.recycler_view_quizzes);
        quizzesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        quizAdapter = new QuizAdapter();
        quizAdapter.setOnItemClickListener(this);
        quizzesRecyclerView.setAdapter(quizAdapter);

        quizRepository = new QuizRepository();

        loadQuizzes();

        return view;
    }

    private void loadQuizzes() {
        quizRepository.getQuizzesByClassroomId(classroomId)
            .addOnSuccessListener(quizzes -> {
                Log.d(TAG, "Quizzes loaded successfully. Count: " + quizzes.size());
                quizAdapter.setQuizzes(quizzes);
                if (quizzes.isEmpty()) {
                    Toast.makeText(getContext(), "Chưa có bài kiểm tra nào.", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading quizzes: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Lỗi tải bài kiểm tra: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    @Override
    public void onItemClick(Quiz quiz) {
        // TODO: Handle quiz item click (e.g., open quiz details/attempt activity)
        Toast.makeText(getContext(), "Clicked on quiz: " + quiz.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
