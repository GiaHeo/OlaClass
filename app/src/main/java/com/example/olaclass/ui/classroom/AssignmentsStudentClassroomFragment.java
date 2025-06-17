package com.example.olaclass.ui.classroom;

import android.content.Intent;
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
import com.example.olaclass.data.repository.QuizAttemptRepository;
import com.example.olaclass.ui.assignments.StudentQuizAdapter;
import com.example.olaclass.ui.assignments.QuizAttemptActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AssignmentsStudentClassroomFragment extends Fragment implements StudentQuizAdapter.OnItemClickListener {

    private RecyclerView quizzesRecyclerView;
    private StudentQuizAdapter quizAdapter;
    private QuizRepository quizRepository;
    private QuizAttemptRepository quizAttemptRepository;
    private String classroomId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;
    private TextView tvNoQuizzes;

    private static final String TAG = "AssignmentsStudentClassroomFragment";

    public static AssignmentsStudentClassroomFragment newInstance(String classroomId) {
        AssignmentsStudentClassroomFragment fragment = new AssignmentsStudentClassroomFragment();
        Bundle args = new Bundle();
        args.putString("classroomId", classroomId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignments_student_classroom, container, false);
        Log.d(TAG, "Inflating layout: fragment_assignments_student_classroom.xml");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (getArguments() != null) {
            classroomId = getArguments().getString("classroomId");
            Log.d(TAG, "Classroom ID received: " + classroomId);
        }

        if (classroomId == null || classroomId.isEmpty()) {
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy ID lớp học cho bài kiểm tra.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Classroom ID is null or empty.");
            return view;
        }

        quizzesRecyclerView = view.findViewById(R.id.recycler_view_quizzes_student);
        quizzesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        quizAdapter = new StudentQuizAdapter();
        quizAdapter.setOnItemClickListener(this);
        quizzesRecyclerView.setAdapter(quizAdapter);

        tvNoQuizzes = view.findViewById(R.id.tv_no_quizzes_student);
        tvNoQuizzes.setVisibility(View.GONE);

        quizRepository = new QuizRepository();
        quizAttemptRepository = new QuizAttemptRepository();

        loadQuizzes();

        return view;
    }

    private void loadQuizzes() {
        if (currentUserId == null) {
            Log.e(TAG, "Current user ID is null. Cannot load quizzes.");
            return;
        }

        quizRepository.getQuizzesByClassroomId(classroomId)
            .addOnSuccessListener(allQuizzes -> {
                Log.d(TAG, "All quizzes loaded for classroom. Count: " + allQuizzes.size());
                if (allQuizzes.isEmpty()) {
                    tvNoQuizzes.setVisibility(View.VISIBLE);
                    quizzesRecyclerView.setVisibility(View.GONE);
                    return;
                }

                quizAttemptRepository.getQuizAttemptsForStudentAndClassroom(currentUserId, classroomId)
                    .addOnSuccessListener(quizAttemptsSnapshot -> {
                        List<String> attemptedQuizIds = quizAttemptsSnapshot.toObjects(com.example.olaclass.data.model.QuizAttempt.class).stream()
                            .map(quizAttempt -> quizAttempt.getQuizId())
                            .collect(Collectors.toList());

                        List<Quiz> unattemptedQuizzes = allQuizzes.stream()
                            .filter(quiz -> !attemptedQuizIds.contains(quiz.getId()))
                            .collect(Collectors.toList());

                        Log.d(TAG, "Unattempted quizzes count: " + unattemptedQuizzes.size());
                        quizAdapter.setQuizzes(unattemptedQuizzes);

                        if (unattemptedQuizzes.isEmpty()) {
                            tvNoQuizzes.setVisibility(View.VISIBLE);
                            quizzesRecyclerView.setVisibility(View.GONE);
                        } else {
                            tvNoQuizzes.setVisibility(View.GONE);
                            quizzesRecyclerView.setVisibility(View.VISIBLE);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading quiz attempts: " + e.getMessage(), e);
                        Toast.makeText(getContext(), "Lỗi tải lịch sử làm bài: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading quizzes: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Lỗi tải bài kiểm tra: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    @Override
    public void onItemClick(Quiz quiz) {
        Intent intent = new Intent(getContext(), QuizAttemptActivity.class);
        intent.putExtra("quizId", quiz.getId());
        intent.putExtra("classroomId", classroomId);
        startActivity(intent);
    }
} 