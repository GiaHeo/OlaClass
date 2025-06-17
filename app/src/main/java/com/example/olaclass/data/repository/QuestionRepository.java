package com.example.olaclass.data.repository;

import com.example.olaclass.data.model.Question;
import com.example.olaclass.data.model.QuestionSet;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionRepository {
    private final CollectionReference questionsRef; // This was for individual questions, now reconsidering
    private final CollectionReference questionSetsRef; // New collection for QuestionSet

    public QuestionRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // The original questionsRef was probably intended for subcollections under assignments
        // For now, we will focus on questionSetsRef as the main storage for question sets.
        // If 'questions' collection was a top-level collection, it needs clarification.
        this.questionsRef = db.collection("questions"); // Assuming this might be for something else or will be deprecated
        this.questionSetsRef = db.collection("questionSets");
    }

    // Phương thức để lưu hoặc cập nhật một QuestionSet
    public Task<Void> saveQuestionSet(QuestionSet questionSet) {
        if (questionSet.getId() == null || questionSet.getId().isEmpty()) {
            // Nếu chưa có ID, tạo một document mới và gán ID
            DocumentReference newDocRef = questionSetsRef.document();
            questionSet.setId(newDocRef.getId());
            return newDocRef.set(questionSet);
        } else {
            // Nếu đã có ID, cập nhật document hiện có
            return questionSetsRef.document(questionSet.getId()).set(questionSet);
        }
    }

    // Phương thức để lấy một QuestionSet theo ID
    public Task<QuestionSet> getQuestionSet(String questionSetId) {
        return questionSetsRef.document(questionSetId).get().continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                return task.getResult().toObject(QuestionSet.class);
            } else {
                return null;
            }
        });
    }

    // Phương thức để lấy tất cả QuestionSets
    public Task<List<QuestionSet>> getAllQuestionSets() {
        return questionSetsRef.get()
            .continueWith(task -> {
                List<QuestionSet> questionSetList = new ArrayList<>();
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        QuestionSet questionSet = document.toObject(QuestionSet.class);
                        questionSet.setId(document.getId());
                        questionSetList.add(questionSet);
                    }
                }
                return questionSetList;
            });
    }

    // Phương thức để xóa một QuestionSet theo ID
    public Task<Void> deleteQuestionSet(String questionSetId) {
        return questionSetsRef.document(questionSetId).delete();
    }

    // The following methods related to 'questions' collection are likely deprecated
    // and are being removed or commented out as questions are now part of QuestionSet.
    /*
    public Task<Void> addQuestion(String assignmentId, Question question) {
        // This method assumes 'questions' is a subcollection of 'assignmentId'
        // If questions are now part of QuestionSet, this method's usage might change.
        Map<String, Object> data = new HashMap<>();
        data.put("type", question.getType());
        data.put("content", question.getContent());
        data.put("options", question.getOptions());
        // data.put("answer", question.getAnswer()); // Removed as answer is part of Option now
        // data.put("correctAnswers", question.getCorrectAnswers()); // Removed as correctAnswers is part of Option now
        return questionsRef.document(assignmentId).collection("questions").document().set(data);
    }

    public Task<Void> updateQuestion(String assignmentId, String questionId, Question question) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", question.getType());
        data.put("content", question.getContent());
        data.put("options", question.getOptions());
        // data.put("answer", question.getAnswer()); // Removed as answer is part of Option now
        // data.put("correctAnswers", question.getCorrectAnswers()); // Removed as correctAnswers is part of Option now
        return questionsRef.document(assignmentId).collection("questions").document(questionId).set(data);
    }

    public Task<Void> deleteQuestion(String assignmentId, String questionId) {
        return questionsRef.document(assignmentId).collection("questions").document(questionId).delete();
    }

    public Task<List<Question>> getQuestions(String assignmentId) {
        return questionsRef.document(assignmentId).collection("questions").get()
                .continueWith(task -> {
                    List<Question> result = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Question q = doc.toObject(Question.class);
                            q.setId(doc.getId());
                            result.add(q);
                        }
                    }
                    return result;
                });
    }
    */
}
