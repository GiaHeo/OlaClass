package com.example.olaclass.ui.classroom;

import android.os.Bundle;
import android.util.Log;
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
import com.example.olaclass.data.model.Student;
import com.example.olaclass.data.repository.ClassroomRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import android.widget.PopupMenu;
import android.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class StudentListFragment extends Fragment implements StudentListAdapter.OnStudentClickListener {

    private RecyclerView recyclerView;
    private StudentListAdapter adapter;
    private StudentListViewModel viewModel;
    private String classroomId;
    private ClassroomRepository classroomRepository;

    public static StudentListFragment newInstance(String classroomId) {
        StudentListFragment fragment = new StudentListFragment();
        Bundle args = new Bundle();
        args.putString("classroomId", classroomId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroomId = getArguments().getString("classroomId");
            Log.d("StudentListFragment", "Classroom ID received in onCreate: " + classroomId);
        } else {
            Log.d("StudentListFragment", "Arguments are null in onCreate.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_students);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new StudentListAdapter(this);
        recyclerView.setAdapter(adapter);

        classroomRepository = new ClassroomRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());
        viewModel = new ViewModelProvider(this, new StudentListViewModel.Factory(classroomId, classroomRepository)).get(StudentListViewModel.class);

        viewModel.students.observe(getViewLifecycleOwner(), students -> {
            Log.d("StudentListFragment", "Students LiveData observed. Number of students: " + (students != null ? students.size() : "null"));
            adapter.setStudents(students);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (classroomId == null || classroomId.isEmpty()) {
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy ID lớp học", Toast.LENGTH_SHORT).show();
            Log.e("StudentListFragment", "Classroom ID is null or empty in onViewCreated.");
            return;
        } else {
            Log.d("StudentListFragment", "Classroom ID is present in onViewCreated: " + classroomId);
        }
    }

    @Override
    public void onStudentClick(Student student) {
        PopupMenu popup = new PopupMenu(requireContext(), recyclerView);
        popup.getMenu().add(0, 0, 0, "Xóa học sinh").setIcon(R.drawable.ic_delete);

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == 0) {
                new AlertDialog.Builder(requireContext())
                    .setTitle("Xóa học sinh")
                    .setMessage("Bạn có chắc chắn muốn xóa học sinh " + student.getName() + " khỏi lớp này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        classroomRepository.removeStudentFromClass(classroomId, student.getUserId())
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(requireContext(), "Đã xóa " + student.getName() + " khỏi lớp.", Toast.LENGTH_SHORT).show();
                                viewModel.loadStudents();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("StudentListFragment", "Lỗi khi xóa học sinh: " + e.getMessage(), e);
                                Toast.makeText(requireContext(), "Lỗi khi xóa học sinh: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
                return true;
            }
            return false;
        });
        popup.show();
    }
}
