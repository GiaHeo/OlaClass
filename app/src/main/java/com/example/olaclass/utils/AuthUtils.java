package com.example.olaclass.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.olaclass.ui.auth.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class AuthUtils {
    
    public static void signOut(Context context) {
        try {
            // Đăng xuất khỏi Firebase
            FirebaseAuth.getInstance().signOut();
            
            // Cấu hình Google Sign-In để xóa tài khoản đã lưu
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(com.example.olaclass.R.string.default_web_client_id))
                    .requestEmail()
                    .setAccountName(null) // Xóa tài khoản đã lưu
                    .build();
                    
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, gso);
            
            // Xóa quyền truy cập và đăng xuất
            googleSignInClient.revokeAccess().addOnCompleteListener(revokeTask -> {
                googleSignInClient.signOut().addOnCompleteListener(signOutTask -> {
                    // Chuyển về màn hình đăng nhập với cờ LOGOUT
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.putExtra("LOGOUT", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
                                  Intent.FLAG_ACTIVITY_CLEAR_TOP | 
                                  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    
                    context.startActivity(intent);
                    
                    if (context instanceof Activity) {
                        ((Activity) context).finishAffinity();
                    }
                    
                    Toast.makeText(context, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                });
            });
        } catch (Exception e) {
            // Nếu có lỗi, vẫn cố gắng chuyển về màn hình đăng nhập
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            
            if (context instanceof Activity) {
                ((Activity) context).finishAffinity();
            }
        }
    }
}
