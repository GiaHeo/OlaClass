package com.example.olaclass.data.model;

public class QuizScoreDisplayItem {
    private String studentId;
    private String studentName;
    private double score;
    private int correctAnswers;
    private int totalQuestions;
    private long timeTakenMillis;
    private long startTimeMillis;

    public QuizScoreDisplayItem(String studentId, String studentName, double score, int correctAnswers, int totalQuestions, long timeTakenMillis, long startTimeMillis) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.score = score;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.timeTakenMillis = timeTakenMillis;
        this.startTimeMillis = startTimeMillis;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public double getScore() {
        return score;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public long getTimeTakenMillis() {
        return timeTakenMillis;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }
} 