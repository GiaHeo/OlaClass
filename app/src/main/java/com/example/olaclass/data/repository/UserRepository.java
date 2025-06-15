package com.example.olaclass.data.repository;

import androidx.annotation.NonNull;
import com.example.olaclass.data.model.Resource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import io.reactivex.Single;

public class UserRepository implements BaseRepository {
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;

    public UserRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }

    public Single<Resource<QuerySnapshot>> getAllUsers() {
        return Single.create(emitter -> {
            firestore.collection("users")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    emitter.onSuccess(Resource.success(querySnapshot));
                })
                .addOnFailureListener(e -> {
                    emitter.onSuccess(Resource.error(e.getMessage(), null));
                });
        });
    }

    public DocumentReference getCurrentUserRef() {
        String uid = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getUid() : null;
        if (uid == null) return null;
        return firestore.collection("users").document(uid);
    }

    // Thêm các phương thức khác như đăng ký, đăng nhập, cập nhật hồ sơ...
}
