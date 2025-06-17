package com.example.olaclass.data.model;

import java.util.List;

public class QuizAttempt {
    private String id;
    private String studentId;
    private String quizId;
    private String classroomId;
    private Long submissionTime;
    private Integer score;
    private List<Answer> answers;

    public QuizAttempt() {
        // Public no-argument constructor needed for Firestore
    }

    public QuizAttempt(String id, String studentId, String quizId, String classroomId, Long submissionTime, Integer score, List<Answer> answers) {
        this.id = id;
        this.studentId = studentId;
        this.quizId = quizId;
        this.classroomId = classroomId;
        this.submissionTime = submissionTime;
        this.score = score;
        this.answers = answers;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public Long getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(Long submissionTime) {
        this.submissionTime = submissionTime;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public static class Answer {
        private String questionContent;
        private String selectedOptionContent;
        private boolean isCorrect;

        public Answer() {
            // Public no-argument constructor needed for Firestore
        }

        public Answer(String questionContent, String selectedOptionContent, boolean isCorrect) {
            this.questionContent = questionContent;
            this.selectedOptionContent = selectedOptionContent;
            this.isCorrect = isCorrect;
        }

        // Getters and Setters
        public String getQuestionContent() {
            return questionContent;
        }

        public void setQuestionContent(String questionContent) {
            this.questionContent = questionContent;
        }

        public String getSelectedOptionContent() {
            return selectedOptionContent;
        }

        public void setSelectedOptionContent(String selectedOptionContent) {
            this.selectedOptionContent = selectedOptionContent;
        }

        public boolean isCorrect() {
            return isCorrect;
        }

        public void setCorrect(boolean correct) {
            isCorrect = correct;
        }
    }
} 