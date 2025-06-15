package com.example.olaclass.utils;

import com.example.olaclass.data.model.Question;
import java.util.List;

public class AutoGrader {
    public static int grade(List<Question> questions, List<String> userAnswers) {
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            if (q.getType().equals("multiple_choice") && q.getAnswer() != null) {
                if (q.getAnswer().equalsIgnoreCase(userAnswers.get(i))) {
                    score++;
                }
            } else if (q.getType().equals("fill_blank") && q.getCorrectAnswers() != null) {
                if (q.getCorrectAnswers().contains(userAnswers.get(i))) {
                    score++;
                }
            }
            // Essay/tự luận không tự động chấm điểm
        }
        return score;
    }
}
