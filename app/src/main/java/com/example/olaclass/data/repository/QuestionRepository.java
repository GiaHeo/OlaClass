package com.example.olaclass.data.repository;

import com.example.olaclass.data.model.Question;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionRepository {
    private final CollectionReference questionsRef = FirebaseFirestore.getInstance().collection("questions");

    public Task<Void> addQuestion(String assignmentId, Question question) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", question.getType());
        data.put("content", question.getContent());
        data.put("options", question.getOptions());
        data.put("answer", question.getAnswer());
        data.put("correctAnswers", question.getCorrectAnswers());
        return questionsRef.document(assignmentId).collection("questions").document().set(data);
    }

    public Task<Void> updateQuestion(String assignmentId, String questionId, Question question) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", question.getType());
        data.put("content", question.getContent());
        data.put("options", question.getOptions());
        data.put("answer", question.getAnswer());
        data.put("correctAnswers", question.getCorrectAnswers());
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
}
