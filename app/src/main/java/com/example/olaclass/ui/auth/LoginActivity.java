package com.example.olaclass.ui.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.olaclass.R;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private void goToMain(String role) {
        Intent intent;
        if ("teacher".equals(role)) {
            intent = new Intent(this, MainActivityTeacher.class);
        } else {
            intent = new Intent(this, MainActivityStudent.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Chuẩn bị cho các bước tích hợp Firebase Auth, Google Sign-In, chọn vai trò...
        EditText etEmail = findViewById(R.id.et_email);
        EditText etPassword = findViewById(R.id.et_password);
        RadioGroup rgRole = findViewById(R.id.rg_role);
        RadioButton rbTeacher = findViewById(R.id.rb_teacher);
        RadioButton rbStudent = findViewById(R.id.rb_student);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnRegister = findViewById(R.id.btn_register);
        Button btnGoogle = findViewById(R.id.btn_google_signin);
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String role = rgRole.getCheckedRadioButtonId() == R.id.rb_teacher ? "teacher" : "student";
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            db.collection("users").document(user.getUid())
                                .set(new UserProfile(email, role))
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                    goToMain(role);
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi lưu vai trò", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            db.collection("users").document(user.getUid()).get()
                                .addOnSuccessListener(snapshot -> {
                                    String role = snapshot.getString("role");
                                    goToMain(role);
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi lấy vai trò", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        });

                // Google Sign-In
        btnGoogle.setOnClickListener(v -> {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 9001);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            db.collection("users").document(user.getUid()).get()
                                    .addOnSuccessListener(snapshot -> {
                                        String role = snapshot.getString("role");
                                        if (role == null) {
                                            // Lần đầu đăng nhập Google, yêu cầu chọn vai trò và lưu lại
                                            showRoleDialogAndSave(user, db);
                                        } else {
                                            goToMain(role);
                                        }
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi lấy vai trò", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showRoleDialogAndSave(FirebaseUser user, FirebaseFirestore db) {
        // Hiển thị dialog chọn vai trò nếu lần đầu đăng nhập Google
        // Đơn giản: lấy vai trò đang chọn trên UI
        RadioGroup rgRole = findViewById(R.id.rg_role);
        String role = rgRole.getCheckedRadioButtonId() == R.id.rb_teacher ? "teacher" : "student";
        db.collection("users").document(user.getUid())
                .set(new UserProfile(user.getEmail(), role))
                .addOnSuccessListener(unused -> goToMain(role))
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi lưu vai trò", Toast.LENGTH_SHORT).show());
    }

    }
}
