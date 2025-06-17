package com.example.olaclass.utils;

import com.example.olaclass.data.model.Question;
import java.util.List;

public class AutoGrader {
    public static int grade(List<Question> questions, List<String> userAnswers) {
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String userAnswer = userAnswers.get(i); // Get the user's answer for the current question

            if (q.getType().equals("multiple_choice") || q.getType().equals("fill_blank")) {
                // For multiple choice and fill_blank, check if the user's answer matches any correct option
                for (Question.Option option : q.getOptions()) {
                    if (option.isCorrect() && option.getContent().equalsIgnoreCase(userAnswer)) {
                        score++;
                        break; // Found a correct answer, move to the next question
                    }
                }
            }
            // Essay/tự luận không tự động chấm điểm, nên không cần xử lý ở đây
        }
        return score;
    }
}
