package com.example.olaclass.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.olaclass.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        TextInputEditText etName = findViewById(R.id.et_name);
        TextInputEditText etEmail = findViewById(R.id.et_email);
        TextInputEditText etPassword = findViewById(R.id.et_password);
        Button btnRegister = findViewById(R.id.btn_register);
        Button btnLogin = findViewById(R.id.btn_login);

        // Handle register button click
        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String role = ((RadioButton) findViewById(R.id.rb_teacher)).isChecked() ? "teacher" : "student";

            if (validateInputs(name, email, password)) {
                registerUser(name, email, password, role);
            }
        });

        // Handle login button click
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private boolean validateInputs(String name, String email, String password) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void registerUser(String name, String email, String password, String role) {
        Log.d(TAG, "Starting registration process for: " + email);
        Log.d(TAG, "Name: " + name + ", Role: " + role);
        
        // Show loading dialog
        android.app.AlertDialog loadingDialog = new android.app.AlertDialog.Builder(this)
                .setView(R.layout.dialog_loading)
                .setCancelable(false)
                .create();
        loadingDialog.show();

        Log.d(TAG, "Attempting to create user with email and password");
        // Create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success - User ID: " + 
                            (mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "null"));
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Log.d(TAG, "User created. Email verified: " + user.isEmailVerified());
                            // Save user data to Firestore
                            saveUserToFirestore(user.getUid(), name, email, role);
                            // Send email verification
                            sendEmailVerification(user);
                        } else {
                            Log.e(TAG, "Current user is null after successful registration");
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        String errorMsg = "createUserWithEmail:failure - " + 
                            (task.getException() != null ? task.getException().getMessage() : "Unknown error");
                        Log.e(TAG, errorMsg, task.getException());
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " +
                                (task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định"), 
                                Toast.LENGTH_LONG).show();
                    }
                    loadingDialog.dismiss();
                });
    }

    private void saveUserToFirestore(String userId, String name, String email, String role) {
        Log.d(TAG, "Saving user data to Firestore. UserID: " + userId);
        
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("role", role);
        user.put("createdAt", com.google.firebase.Timestamp.now());

        Log.d(TAG, "User data to save: " + user.toString());
        
        // Add a new document with a generated ID
        db.collection("users")
                .document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User data successfully saved to Firestore");
                    // Navigate to login after successful registration
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this,
                                "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.",
                                Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    });
                })
                .addOnFailureListener(e -> {
                    String errorMsg = "Error saving user data to Firestore: " + e.getMessage();
                    Log.e(TAG, errorMsg, e);
                    runOnUiThread(() -> 
                        Toast.makeText(RegisterActivity.this,
                            "Lỗi khi lưu thông tin người dùng: " + e.getMessage(),
                            Toast.LENGTH_LONG).show()
                    );
                });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Email verification sent.");
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, navigate to main activity
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        }
    }
}
