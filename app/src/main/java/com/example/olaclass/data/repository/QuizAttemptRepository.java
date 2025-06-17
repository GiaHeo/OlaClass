package com.example.olaclass.data.repository;

import com.example.olaclass.data.model.QuizAttempt;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.UUID;

public class QuizAttemptRepository {
    private final CollectionReference quizAttemptsRef;

    public QuizAttemptRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        quizAttemptsRef = db.collection("quizAttempts");
    }

    public Task<Void> saveQuizAttempt(QuizAttempt quizAttempt) {
        if (quizAttempt.getId() == null || quizAttempt.getId().isEmpty()) {
            String id = UUID.randomUUID().toString();
            quizAttempt.setId(id);
        }
        return quizAttemptsRef.document(quizAttempt.getId()).set(quizAttempt);
    }

    public Task<QuerySnapshot> getQuizAttemptsForStudentAndQuiz(String studentId, String quizId) {
        return quizAttemptsRef.whereEqualTo("studentId", studentId)
                .whereEqualTo("quizId", quizId)
                .get();
    }

    public Task<QuerySnapshot> getQuizAttemptsForStudentAndClassroom(String studentId, String classroomId) {
        return quizAttemptsRef.whereEqualTo("studentId", studentId)
                .whereEqualTo("classroomId", classroomId)
                .get();
    }
} 