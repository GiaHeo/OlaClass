package com.example.olaclass.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AssignmentRepository {
    private final CollectionReference assignmentsRef = FirebaseFirestore.getInstance().collection("assignments");

    public Task<Void> extendDeadline(String assignmentId, String studentId, long newDeadlineMillis) {
        Map<String, Object> data = new HashMap<>();
        data.put("extendedDeadline", newDeadlineMillis);
        return assignmentsRef.document(assignmentId)
                .collection("submissions")
                .document(studentId)
                .set(data, com.google.firebase.firestore.SetOptions.merge());
    }
}
