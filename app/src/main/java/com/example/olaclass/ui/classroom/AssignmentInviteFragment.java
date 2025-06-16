package com.example.olaclass.ui.classroom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.olaclass.R;

public class AssignmentInviteFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignment_invite, container, false);
        String classroomId = getArguments() != null ? getArguments().getString("classroomId") : "";
        TextView tvAssignment = view.findViewById(R.id.tv_assignment);
        tvAssignment.setText("ID lớp: " + classroomId + "\n(Bài tập & lời mời sẽ hiển thị ở đây)");
        // TODO: Load assignments, show invite code, manage invites
        return view;
    }
}
