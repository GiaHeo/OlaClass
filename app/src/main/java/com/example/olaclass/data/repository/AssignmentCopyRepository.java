package com.example.olaclass.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class AssignmentCopyRepository {
    private final CollectionReference assignmentsRef = FirebaseFirestore.getInstance().collection("assignments");

    public Task<DocumentReference> copyAssignmentToClass(String assignmentId, String targetClassId) {
        // Lấy assignment gốc
        return assignmentsRef.document(assignmentId).get().continueWithTask(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                Map<String, Object> data = new HashMap<>(task.getResult().getData());
                data.put("classId", targetClassId);
                data.remove("id"); // Xóa id cũ nếu có
                return assignmentsRef.add(data);
            }
            throw new Exception("Assignment not found");
        });
    }
}
