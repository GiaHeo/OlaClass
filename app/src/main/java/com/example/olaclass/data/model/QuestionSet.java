package com.example.olaclass.data.model;

import java.util.List;

public class QuestionSet {
    private String id;
    private String title;
    private String creatorId;
    private List<Question> questions;

    public QuestionSet() {
        // Public no-argument constructor needed for Firestore
    }

    public QuestionSet(String id, String title, String creatorId, List<Question> questions) {
        this.id = id;
        this.title = title;
        this.creatorId = creatorId;
        this.questions = questions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
} 