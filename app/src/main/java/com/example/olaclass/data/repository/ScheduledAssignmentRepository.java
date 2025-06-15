package com.example.olaclass.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ScheduledAssignmentRepository {
    private final CollectionReference scheduledRef = FirebaseFirestore.getInstance().collection("scheduledAssignments");

    public Task<DocumentReference> scheduleAssignment(String assignmentId, long publishTimeMillis) {
        Map<String, Object> data = new HashMap<>();
        data.put("assignmentId", assignmentId);
        data.put("publishTime", publishTimeMillis);
        return scheduledRef.add(data);
    }

    public Task<Void> updateSchedule(String scheduleId, long newPublishTime) {
        return scheduledRef.document(scheduleId).update("publishTime", newPublishTime);
    }

    public Task<Void> deleteSchedule(String scheduleId) {
        return scheduledRef.document(scheduleId).delete();
    }
}
