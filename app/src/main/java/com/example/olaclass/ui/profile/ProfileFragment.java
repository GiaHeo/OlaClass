package com.example.olaclass.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import com.example.olaclass.R;
import com.example.olaclass.utils.PreferenceKeys;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        
        TextView tvEmail = view.findViewById(R.id.tv_email);
        TextView tvRole = view.findViewById(R.id.tv_role);
        Button btnLogout = view.findViewById(R.id.btn_logout);
        SwitchCompat switchAutoLogin = view.findViewById(R.id.switch_auto_login);

        // Luôn đọc lại trạng thái auto login khi vào hồ sơ
        switchAutoLogin.setChecked(requireContext().getSharedPreferences("olaclass_prefs", android.content.Context.MODE_PRIVATE).getBoolean("auto_login", false));
        switchAutoLogin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            requireContext().getSharedPreferences("olaclass_prefs", android.content.Context.MODE_PRIVATE)
                .edit().putBoolean("auto_login", isChecked).apply();
        });
        
        if (currentUser != null) {
            tvEmail.setText(currentUser.getEmail());
            // Lấy role từ Firestore Database
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    android.util.Log.d("ProfileFragment", "Firestore snapshot: " + documentSnapshot.getData());
                    if (documentSnapshot.exists() && documentSnapshot.contains("role")) {
                        String role = documentSnapshot.getString("role");
                        if (role != null) {
                            String roleLabel = role.equals("teacher") ? "Giáo viên" : (role.equals("student") ? "Học sinh" : role);
                            tvRole.setText("Chức vụ: " + roleLabel);
                        } else {
                            tvRole.setText("Chức vụ: (không xác định)");
                        }
                    } else {
                        tvRole.setText("Chức vụ: (không xác định)");
                        android.util.Log.w("ProfileFragment", "Không tìm thấy trường role trong document hoặc document không tồn tại");
                    }
                })
                .addOnFailureListener(e -> {
                    tvRole.setText("Chức vụ: (lỗi tải dữ liệu)");
                    android.util.Log.e("ProfileFragment", "Lỗi tải dữ liệu hồ sơ từ Firestore: " + e.getMessage(), e);
                });
        }
        
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            requireActivity().finishAffinity();
            startActivity(new Intent(requireContext(), getActivity().getClass()));
        });
        
        return view;
    }
}
