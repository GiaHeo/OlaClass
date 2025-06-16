package com.example.olaclass.ui.classroom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.olaclass.R;

public class ClassroomInfoFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classroom_info, container, false);
        String classroomId = getArguments() != null ? getArguments().getString("classroomId") : "";
        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvDescription = view.findViewById(R.id.tv_description);
        TextView tvInviteCode = view.findViewById(R.id.tv_invite_code);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("classrooms").document(classroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String inviteCode = document.getString("inviteCode");
                    tvName.setText(document.getString("name"));
                    tvDescription.setText(document.getString("description"));
                    tvInviteCode.setText("Mã mời: " + (inviteCode != null ? inviteCode : "N/A"));

                    // Add click listener to copy invite code
                    String finalInviteCode = inviteCode; // Effective final for lambda
                    tvInviteCode.setOnClickListener(v -> {
                        if (finalInviteCode != null && !finalInviteCode.isEmpty()) {
                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                            android.content.ClipData clip = android.content.ClipData.newPlainText("Mã mời lớp học", finalInviteCode);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(requireContext(), "Đã sao chép mã mời: " + finalInviteCode, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Không có mã mời để sao chép", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(getActivity(), "Lớp học không tồn tại", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
