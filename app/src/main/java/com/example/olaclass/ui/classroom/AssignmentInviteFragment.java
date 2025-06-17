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
import com.example.olaclass.ui.assignments.QuizAdapter;
import com.example.olaclass.ui.assignments.QuizDetailTeacherActivity;
import com.example.olaclass.ui.assignments.QuizAttemptActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AssignmentInviteFragment extends Fragment implements QuizAdapter.OnItemClickListener {

    private RecyclerView quizzesRecyclerView;
    private QuizAdapter quizAdapter;
    private QuizRepository quizRepository;
    private String classroomId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserRole;

    private static final String TAG = "AssignmentInviteFrag";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignment_invite, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

        fetchUserRole();
        loadQuizzes();

        return view;
    }

    private void fetchUserRole() {
        if (mAuth.getCurrentUser() != null) {
            db.collection("users").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentUserRole = documentSnapshot.getString("role");
                        Log.d(TAG, "Vai trò người dùng đã tải trong Fragment: " + currentUserRole);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi tải vai trò người dùng trong Fragment: " + e.getMessage(), e);
                });
        }
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
        String currentClassroomId = null;
        if (getActivity() != null && getActivity() instanceof ClassroomDetailActivity) {
            currentClassroomId = ((ClassroomDetailActivity) getActivity()).getClassroomId();
        }

        if ("student".equals(currentUserRole)) {
            Intent intent = new Intent(getContext(), QuizAttemptActivity.class);
            intent.putExtra("quizId", quiz.getId());
            if (currentClassroomId != null) {
                intent.putExtra("classroomId", currentClassroomId);
            }
            startActivity(intent);
        } else if ("teacher".equals(currentUserRole)) {
            Intent intent = new Intent(getContext(), QuizDetailTeacherActivity.class);
            intent.putExtra("quizId", quiz.getId());
            if (currentClassroomId != null) {
                intent.putExtra("classroomId", currentClassroomId);
            }
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Không thể xác định vai trò người dùng.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Vai trò người dùng không xác định hoặc chưa được tải khi nhấp vào bài kiểm tra.");
        }
    }
}
