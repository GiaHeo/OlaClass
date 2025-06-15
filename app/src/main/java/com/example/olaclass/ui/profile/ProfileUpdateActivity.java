package com.example.olaclass.ui.profile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.olaclass.R;

import android.text.TextUtils;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileUpdateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        EditText etFullName = findViewById(R.id.et_full_name);
        EditText etEmail = findViewById(R.id.et_email);
        EditText etPhone = findViewById(R.id.et_phone);
        RadioGroup rgGender = findViewById(R.id.rg_gender);
        RadioButton rbMale = findViewById(R.id.rb_male);
        RadioButton rbFemale = findViewById(R.id.rb_female);
        Button btnUpdate = findViewById(R.id.btn_update_profile);
        ImageView imgAvatar = findViewById(R.id.img_avatar);
        Button btnChooseAvatar = findViewById(R.id.btn_choose_avatar);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (user != null) {
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    etEmail.setText(snapshot.getString("email"));
                    etFullName.setText(snapshot.getString("fullName"));
                    etPhone.setText(snapshot.getString("phone"));
                    String gender = snapshot.getString("gender");
                    if ("male".equals(gender)) rbMale.setChecked(true);
                    else if ("female".equals(gender)) rbFemale.setChecked(true);
                    String avatarUrl = snapshot.getString("avatarUrl");
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Picasso.get().load(avatarUrl).placeholder(R.drawable.ic_person).into(imgAvatar);
                    }
                } else {
                    etEmail.setText(user.getEmail());
                }
            });
        }

        btnUpdate.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String gender = rgGender.getCheckedRadioButtonId() == R.id.rb_male ? "male" : "female";
            if (TextUtils.isEmpty(fullName)) {
                Toast.makeText(this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.isEmpty(phone) && phone.length() < 8) {
                Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (user != null) {
                db.collection("users").document(user.getUid())
                        .update("fullName", fullName, "phone", phone, "gender", gender)
                        .addOnSuccessListener(unused -> Toast.makeText(this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Lỗi cập nhật hồ sơ", Toast.LENGTH_SHORT).show());
            }
        });
        btnChooseAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 101);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imgAvatar.setImageURI(imageUri);
            uploadAvatarToFirebase(imageUri);
        }
    }

    private void uploadAvatarToFirebase(Uri imageUri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || imageUri == null) return;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("avatars/" + user.getUid() + ".jpg");
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                            .update("avatarUrl", uri.toString())
                            .addOnSuccessListener(unused -> Toast.makeText(this, "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Lỗi lưu ảnh đại diện", Toast.LENGTH_SHORT).show());
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi upload ảnh đại diện", Toast.LENGTH_SHORT).show());
    }
}
