package com.example.olaclass.ui.classroom;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.TextView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagingData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.olaclass.R;
import com.example.olaclass.data.model.Classroom;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.Toolbar;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

import com.example.olaclass.data.repository.ClassroomRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClassroomListFragmentTeacher extends Fragment implements ClassroomListAdapter.OnClassroomClickListener {

    private ClassroomViewModel viewModel;
    private ClassroomListAdapter adapter;
    private CompositeDisposable disposables = new CompositeDisposable();
    private ClassroomRepository classroomRepository;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userRole; // To store the user's role
    private TextView fragmentTitle;

    public static ClassroomListFragmentTeacher newInstance(String userRole) {
        ClassroomListFragmentTeacher fragment = new ClassroomListFragmentTeacher();
        Bundle args = new Bundle();
        args.putString("userRole", userRole);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userRole = getArguments().getString("userRole");
        }
        // Removed classroomRepository and viewModel initialization from here to onCreateView
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classroom_list_teacher, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (userRole == null) {
            Log.w("ClassroomListFragmentTeacher", "userRole is null in onCreateView, defaulting to 'student'");
            userRole = "student"; // Default to student if userRole is null
        }
        toolbar.setTitle(userRole.equals("teacher") ? "Lớp học của giáo viên" : "Lớp học đã tham gia");

        try {
            RecyclerView recyclerView = view.findViewById(R.id.recycler_classrooms);
            if (recyclerView == null) {
                throw new IllegalStateException("RecyclerView not found in layout");
            }

            adapter = new ClassroomListAdapter(this);

            FloatingActionButton fabAddClassroom = view.findViewById(R.id.fab_add_classroom);

            if (fabAddClassroom != null) {
                fabAddClassroom.setVisibility(View.VISIBLE);
                fabAddClassroom.setOnClickListener(v -> showCreateClassroomDialog());
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(adapter);

            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            classroomRepository = new ClassroomRepository(db, mAuth); // Khởi tạo đúng ở đây
            ClassroomViewModelFactory factory = new ClassroomViewModelFactory(classroomRepository, userRole);
            viewModel = new ViewModelProvider(this, factory).get(ClassroomViewModel.class);

            viewModel.getClassrooms().observe(getViewLifecycleOwner(), pagingData -> {
                adapter.submitData(getViewLifecycleOwner().getLifecycle(), pagingData);
            });

            adapter.addLoadStateListener(loadStates -> {
                if (loadStates.getRefresh() instanceof androidx.paging.LoadState.Error) {
                    androidx.paging.LoadState.Error errorState = (androidx.paging.LoadState.Error) loadStates.getRefresh();
                    String errorMessage = errorState.getError() != null ?
                        errorState.getError().getMessage() : "Đã xảy ra lỗi khi tải dữ liệu";

                    android.widget.Toast.makeText(requireContext(),
                        "Lỗi: " + errorMessage,
                        android.widget.Toast.LENGTH_LONG).show();
                }
                return null;
            });
        } catch (Exception e) {
            android.util.Log.e("ClassroomListFragmentTeacher", "Lỗi trong onViewCreated: " + e.getMessage(), e);
            android.widget.Toast.makeText(requireContext(),
                "Đã xảy ra lỗi: " + e.getMessage(),
                android.widget.Toast.LENGTH_LONG).show();
        }
        return view;
    }

    private String currentClassroomId;

    private void showCreateClassroomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Tạo lớp học mới");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Tên lớp học");
        builder.setView(input);

        builder.setPositiveButton("Tạo", (dialog, which) -> {
            String classroomName = input.getText().toString().trim();
            if (!classroomName.isEmpty()) {
                createClassroom(classroomName);
            } else {
                Toast.makeText(requireContext(), "Tên lớp học không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void createClassroom(String classroomName) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String teacherId = currentUser.getUid();
            String teacherEmail = currentUser.getEmail();

            String inviteCode = generateInviteCode();
            String classroomId = UUID.randomUUID().toString();

            Map<String, Object> classroomData = new HashMap<>();
            classroomData.put("name", classroomName);
            classroomData.put("teacherId", teacherId);
            classroomData.put("teacherEmail", teacherEmail);
            classroomData.put("inviteCode", inviteCode);
            classroomData.put("createdAt", System.currentTimeMillis());
            classroomData.put("students", Collections.emptyList()); // Initialize with an empty list of students

            // Sử dụng classroomRepository đã được khởi tạo trong onCreateView
            classroomRepository.addClassroom(classroomId, classroomData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ClassroomListFragmentTeacher", "Lớp học đã được tạo thành công với ID: " + classroomId); // Sửa lỗi ở đây
                        Toast.makeText(requireContext(), "Lớp học đã được tạo thành công!", Toast.LENGTH_SHORT).show();
                        viewModel.refreshClassrooms();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Lỗi khi tạo lớp học: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private String generateInviteCode() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int CODE_LENGTH = 6;
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    @Override
    public void onClassroomClick(Classroom classroom) {
        // Handle classroom click for teacher
        // (e.g., navigate to ClassroomDetailActivity)
        android.content.Intent intent = new android.content.Intent(requireContext(), com.example.olaclass.ui.classroom.ClassroomDetailActivity.class);
        intent.putExtra("classroomId", classroom.getId());
        startActivity(intent);
    }

    private void confirmDeleteClassroom(Classroom classroom) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa lớp học")
                .setMessage("Bạn có chắc chắn muốn xóa lớp học \'" + classroom.getName() + "\' không? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteClassroom(classroom))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteClassroom(Classroom classroom) {
        classroomRepository.deleteClassroom(classroom.getId())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Lớp học đã được xóa thành công!", Toast.LENGTH_SHORT).show();
                    viewModel.refreshClassrooms();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Lỗi khi xóa lớp học: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.refreshClassrooms();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
    }
}