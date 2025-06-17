package com.example.olaclass.data.repository;

import com.example.olaclass.data.model.Quiz;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizRepository {
    private final CollectionReference quizzesRef;

    public QuizRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.quizzesRef = db.collection("quizzes");
    }

    // Phương thức để lưu hoặc cập nhật một Quiz
    public Task<Void> saveQuiz(Quiz quiz) {
        if (quiz.getId() == null || quiz.getId().isEmpty()) {
            // Nếu chưa có ID, tạo một document mới và gán ID
            DocumentReference newDocRef = quizzesRef.document();
            quiz.setId(newDocRef.getId());
            return newDocRef.set(quiz);
        } else {
            // Nếu đã có ID, cập nhật document hiện có
            return quizzesRef.document(quiz.getId()).set(quiz);
        }
    }

    // Phương thức để lấy một Quiz theo ID
    public Task<Quiz> getQuiz(String quizId) {
        return quizzesRef.document(quizId).get().continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                return task.getResult().toObject(Quiz.class);
            } else {
                return null;
            }
        });
    }

    // Phương thức để lấy tất cả Quiz theo classroomId
    public Task<List<Quiz>> getQuizzesByClassroomId(String classroomId) {
        return quizzesRef.whereEqualTo("classroomId", classroomId).get()
            .continueWith(task -> {
                List<Quiz> quizList = new ArrayList<>();
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Quiz quiz = document.toObject(Quiz.class);
                        quiz.setId(document.getId());
                        quizList.add(quiz);
                    }
                }
                return quizList;
            });
    }

    // Phương thức để lấy tất cả Quiz do một người tạo cụ thể
    public Task<List<Quiz>> getQuizzesByCreator(String creatorId) {
        return quizzesRef.whereEqualTo("creatorId", creatorId).get()
                .continueWith(task -> {
                    List<Quiz> quizList = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Quiz quiz = document.toObject(Quiz.class);
                            quiz.setId(document.getId());
                            quizList.add(quiz);
                        }
                    }
                    return quizList;
                });
    }

    // Phương thức để lấy tất cả Quiz cho một Classroom cụ thể (Alias for getQuizzesByClassroomId)
    public Task<List<Quiz>> getQuizzesForClassroom(String classroomId) {
        return getQuizzesByClassroomId(classroomId);
    }

    // Phương thức để xóa một Quiz theo ID
    public Task<Void> deleteQuiz(String quizId) {
        return quizzesRef.document(quizId).delete();
    }
} 