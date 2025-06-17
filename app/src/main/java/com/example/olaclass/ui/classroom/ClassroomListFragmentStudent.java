package com.example.olaclass.ui.classroom;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.TextView;

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

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

import com.example.olaclass.data.repository.ClassroomRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.widget.Toolbar;

public class ClassroomListFragmentStudent extends Fragment implements ClassroomListAdapter.OnClassroomClickListener {

    private ClassroomViewModel viewModel;
    private ClassroomListAdapter adapter;
    private CompositeDisposable disposables = new CompositeDisposable();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userRole; // To store the user's role
    private TextView fragmentTitle;

    public static ClassroomListFragmentStudent newInstance(String userRole) {
        ClassroomListFragmentStudent fragment = new ClassroomListFragmentStudent();
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
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classroom_list_student, container, false);
        fragmentTitle = view.findViewById(R.id.fragment_title);
        // Add null check for userRole
        if (userRole == null) {
            Log.w("ClassroomListFragmentStudent", "userRole is null in onCreateView, defaulting to 'student'");
            userRole = "student"; // Default to student if userRole is null
        }
        fragmentTitle.setText(userRole.equals("teacher") ? "Lớp học của giáo viên" : "Lớp học đã tham gia");

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (userRole == null) {
            Log.w("ClassroomListFragmentStudent", "userRole is null in onCreateView, defaulting to 'student'");
            userRole = "student"; // Default to student if userRole is null
        }
        toolbar.setTitle(userRole.equals("teacher") ? "Lớp học của giáo viên" : "Lớp học đã tham gia");

        try {
            RecyclerView recyclerView = view.findViewById(R.id.recycler_classrooms);
            if (recyclerView == null) {
                throw new IllegalStateException("RecyclerView not found in layout");
            }

            adapter = new ClassroomListAdapter(this);

            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(adapter);

            ClassroomRepository classroomRepository = new ClassroomRepository(db, mAuth);
            ClassroomViewModelFactory factory = new ClassroomViewModelFactory(classroomRepository, userRole);
            viewModel = new ViewModelProvider(this, factory).get(ClassroomViewModel.class);

            viewModel.getClassrooms().observe(getViewLifecycleOwner(), pagingData -> {
                try {
                    if (pagingData != null) {
                        android.util.Log.d("ClassroomListFragmentStudent", "Nhận được pagingData mới từ ViewModel");
                        adapter.submitData(getLifecycle(), pagingData);
                    } else {
                        android.util.Log.w("ClassroomListFragmentStudent", "Nhận được pagingData null từ ViewModel");
                    }
                } catch (Exception e) {
                    android.util.Log.e("ClassroomListFragmentStudent", "Lỗi khi cập nhật dữ liệu: " + e.getMessage(), e);
                }
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
            android.util.Log.e("ClassroomListFragmentStudent", "Lỗi trong onViewCreated: " + e.getMessage(), e);
            android.widget.Toast.makeText(requireContext(),
                "Đã xảy ra lỗi: " + e.getMessage(),
                android.widget.Toast.LENGTH_LONG).show();
        }

        FloatingActionButton fabJoinClassroom = view.findViewById(R.id.fab_join_classroom);
        fabJoinClassroom.setOnClickListener(v -> showPasteInviteCodeDialog());

        return view;
    }


    private void showPasteInviteCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Tham gia lớp học bằng mã mời");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Nhập mã mời");
        builder.setView(input);

        // Add paste button
        ImageButton pasteButton = new ImageButton(requireContext());
        pasteButton.setImageResource(R.drawable.ic_content_copy); // Assuming you have this drawable
        pasteButton.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null && clipboard.hasPrimaryClip()) {
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                if (item != null && item.getText() != null) {
                    input.setText(item.getText().toString());
                }
            } else {
                Toast.makeText(requireContext(), "Không có gì để dán từ clipboard.", Toast.LENGTH_SHORT).show();
            }
        });

        // Add paste button next to EditText in a horizontal layout
        // This part needs a bit more work if you want a custom layout in the AlertDialog.
        // For simplicity, for now, we'll just set the view to the EditText.
        // If you want a more complex layout, you'd inflate a custom layout XML.


        builder.setPositiveButton("Tham gia", (dialog, which) -> {
            String inviteCode = input.getText().toString().trim();
            if (!inviteCode.isEmpty()) {
                joinClassWithInviteCode(inviteCode);
            } else {
                Toast.makeText(requireContext(), "Mã mời không được để trống.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void joinClassWithInviteCode(String inviteCode) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String studentId = currentUser.getUid();
            ClassroomRepository classroomRepository = new ClassroomRepository(db, mAuth);
            classroomRepository.joinClassroom(inviteCode, studentId)
                    .addOnSuccessListener(classroom -> {
                        Toast.makeText(requireContext(), "Tham gia lớp học thành công!", Toast.LENGTH_SHORT).show();
                        viewModel.refreshClassrooms();
                    })
                    .addOnFailureListener(e -> {
                        if (e instanceof ClassroomRepository.AlreadyJoinedClassroomException) {
                            Classroom alreadyJoinedClassroom = ((ClassroomRepository.AlreadyJoinedClassroomException) e).getClassroom();
                            Toast.makeText(requireContext(), "Bạn đã vào lớp " + alreadyJoinedClassroom.getName() + " từ trước rồi.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(requireContext(), "Lỗi khi tham gia lớp học: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        Log.e("ClassroomListFragmentStudent", "Lỗi khi tham gia lớp học: " + e.getMessage());
                    });
        }
    }

    @Override
    public void onClassroomClick(Classroom classroom) {
        android.content.Intent intent = new android.content.Intent(requireContext(), com.example.olaclass.ui.classroom.ClassroomDetailActivity.class);
        intent.putExtra("classroomId", classroom.getId());
        startActivity(intent);
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