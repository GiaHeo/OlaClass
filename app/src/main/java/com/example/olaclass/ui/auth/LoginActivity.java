package com.example.olaclass.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.olaclass.R;
import com.example.olaclass.ui.student.MainActivityStudent;
import com.example.olaclass.ui.teacher.MainActivityTeacher;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.Map;
import java.util.HashMap;

import java.util.Collections;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private View loadingOverlay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Kiểm tra nếu đang đăng xuất
        if (getIntent().getBooleanExtra("LOGOUT", false)) {
            // Clear any existing tasks and start fresh
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
                         Intent.FLAG_ACTIVITY_NEW_TASK | 
                         Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        
        setContentView(R.layout.activity_login);
        
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        // Check if user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, check their role and redirect
            checkUserRoleAndRedirect(currentUser.getUid());
        }
        
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
                
        // Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        
        // Initialize views
        EditText etEmail = findViewById(R.id.et_email);
        EditText etPassword = findViewById(R.id.et_password);
        RadioGroup rgRole = findViewById(R.id.rg_role);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnGoogle = findViewById(R.id.btn_google_signin);
        Button btnRegister = findViewById(R.id.btn_register);
        CheckBox cbAutoLogin = findViewById(R.id.cb_auto_login);

        // Đọc trạng thái auto login từ SharedPreferences
        boolean autoLogin = getSharedPreferences("olaclass_prefs", MODE_PRIVATE).getBoolean("auto_login", false);
        cbAutoLogin.setChecked(autoLogin);

        // Initialize loading overlay
        loadingOverlay = findViewById(R.id.loading_overlay);
        
        // Nếu đã đăng nhập và auto login, chuyển vào app luôn
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null && autoLogin) {
            showLoading(true);
            checkUserRoleAndRedirect(currentUser.getUid());
            return;
        }
        
        // Set up click listeners
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            boolean autoLoginChecked = cbAutoLogin.isChecked();
            // Lưu trạng thái auto login
            getSharedPreferences("olaclass_prefs", MODE_PRIVATE).edit().putBoolean("auto_login", autoLoginChecked).apply();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            
            int selectedId = rgRole.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Vui lòng chọn vai trò", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String role = ((RadioButton) findViewById(selectedId)).getText().toString().toLowerCase();
            
            // Sign in with email and password
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRoleAndRedirect(user.getUid());
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + 
                            task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        });
        
        btnGoogle.setOnClickListener(v -> {
            // Luôn yêu cầu chọn tài khoản mỗi lần đăng nhập
            mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
                // Sau khi đăng xuất, mở màn hình chọn tài khoản
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            });
        });
        
        btnRegister.setOnClickListener(v -> 
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                } else {
                    Log.e(TAG, "Google Sign-In account is null");
                    Toast.makeText(this, "Không thể lấy thông tin tài khoản Google.", Toast.LENGTH_LONG).show();
                }
            } catch (ApiException e) {
                // Google Sign In failed
                String errorMessage = "Mã lỗi: " + e.getStatusCode() + " - ";
                switch (e.getStatusCode()) {
                    case 10:
                        errorMessage += "Lỗi cấu hình. Vui lòng kiểm tra lại cấu hình Google Sign-In trong Google Cloud Console.";
                        break;
                    case 12501:
                        errorMessage += "Đã hủy đăng nhập bằng Google.";
                        break;
                    case 12502:
                        errorMessage += "Lỗi trong quá trình đăng nhập. Vui lòng thử lại.";
                        break;
                    case 12500:
                        errorMessage += "Lỗi kết nối. Vui lòng kiểm tra kết nối mạng và thử lại.";
                        break;
                    default:
                        errorMessage += "Lỗi không xác định: " + e.getLocalizedMessage();
                        break;
                }
                
                Log.e(TAG, "Google sign in failed: " + errorMessage, e);
                final String finalErrorMessage = errorMessage; // Tạo biến final mới
                runOnUiThread(() -> 
                    Toast.makeText(LoginActivity.this, finalErrorMessage, Toast.LENGTH_LONG).show()
                );
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        String methodTag = "firebaseAuthWithGoogle";
        Log.d(TAG, methodTag + ": Starting Google authentication for email: " + acct.getEmail());
        
        if (acct.getIdToken() == null) {
            String errorMsg = "Google ID token is null";
            Log.e(TAG, methodTag + ": " + errorMsg);
            Toast.makeText(this, "Lỗi xác thực Google: Không thể lấy thông tin đăng nhập", Toast.LENGTH_LONG).show();
            return;
        }
        
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        Log.d(TAG, methodTag + ": Credential created, signing in with Firebase");
        
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Sign in success
                    Log.d(TAG, methodTag + ": Firebase signInWithCredential:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    
                    if (user != null) {
                        Log.d(TAG, methodTag + ": User signed in - UID: " + user.getUid() + 
                              ", Email: " + user.getEmail() + 
                              ", Email verified: " + user.isEmailVerified());
                        
                        // Check if user is new
                        if (task.getResult().getAdditionalUserInfo() != null && 
                            task.getResult().getAdditionalUserInfo().isNewUser()) {
                            Log.d(TAG, methodTag + ": New user detected, showing role selection");
                            showRoleSelectionDialog(user);
                        } else {
                            Log.d(TAG, methodTag + ": Existing user, checking role");
                            // Existing user, check role and redirect
                            checkUserRoleAndRedirect(user.getUid());
                        }
                    } else {
                        String errorMsg = "FirebaseUser is null after successful sign in";
                        Log.e(TAG, methodTag + ": " + errorMsg);
                        Toast.makeText(LoginActivity.this, "Lỗi: Không thể lấy thông tin người dùng", 
                            Toast.LENGTH_LONG).show();
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    String errorMsg = "signInWithCredential:failure - " + 
                        (task.getException() != null ? task.getException().getMessage() : "Unknown error");
                    Log.e(TAG, methodTag + ": " + errorMsg, task.getException());
                    
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, 
                            "Đăng nhập Google thất bại: " + 
                            (task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định"), 
                            Toast.LENGTH_LONG).show();
                    });
                }
            });
    }

    private void showRoleSelectionDialog(FirebaseUser user) {
        new AlertDialog.Builder(this)
            .setTitle("Chọn vai trò")
            .setItems(new String[]{"Giáo viên", "Học sinh"}, (dialog, which) -> {
                String role = (which == 0) ? "teacher" : "student";
                saveUserRole(user.getUid(), role);
                goToMain(role);
            })
            .setCancelable(false)
            .show();
    }

    private void saveUserRole(String userId, String role) {
        // Lấy thông tin người dùng hiện tại
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        // Lưu thông tin cá nhân và role lên Firestore
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("role", role);
        userInfo.put("email", user.getEmail());
        userInfo.put("displayName", user.getDisplayName());
        userInfo.put("photoUrl", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);

        Log.d(TAG, "Saving user info to Firestore: " + userInfo);
        db.collection("users").document(userId)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener(aVoid -> Log.d(TAG, "User info saved successfully to Firestore for userId: " + userId))
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error saving user info to Firestore for userId: " + userId + ", error: " + e.getMessage(), e);
            });
    }

    private void checkUserRoleAndRedirect(String userId) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener(documentSnapshot -> {
                String role = "student"; // Default role
                if (documentSnapshot.exists() && documentSnapshot.contains("role")) {
                    role = documentSnapshot.getString("role");
                }
                goToMain(role);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Lỗi khi kiểm tra vai trò người dùng: " + e.getMessage());
                // Mặc định là student nếu không lấy được vai trò
                goToMain("student");
            });
    }
    
    private void showLoading(boolean show) {
        runOnUiThread(() -> {
            if (loadingOverlay != null) {
                loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
            }
            // Disable all interactive elements while loading
            findViewById(R.id.et_email).setEnabled(!show);
            findViewById(R.id.et_password).setEnabled(!show);
            findViewById(R.id.cb_auto_login).setEnabled(!show);
            findViewById(R.id.rg_role).setEnabled(!show);
            findViewById(R.id.btn_login).setEnabled(!show);
            findViewById(R.id.btn_register).setEnabled(!show);
            findViewById(R.id.btn_google_signin).setEnabled(!show);
        });
    }

    private void goToMain(String role) {
        runOnUiThread(() -> {
            showLoading(false);
            Intent intent;
            if ("teacher".equals(role)) {
                intent = new Intent(LoginActivity.this, MainActivityTeacher.class);
                Log.d(TAG, "Chuyển hướng đến MainActivityTeacher");
            } else {
                intent = new Intent(LoginActivity.this, MainActivityStudent.class);
                Log.d(TAG, "Chuyển hướng đến MainActivityStudent");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            showLoading(true);
            checkUserRoleAndRedirect(currentUser.getUid());
        } else {
            showLoading(false);
        }
    }
}
