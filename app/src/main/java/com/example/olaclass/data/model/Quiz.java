package com.example.olaclass.data.model;

import java.util.List;
import java.util.ArrayList;

public class Quiz {
    private String id;
    private String title;
    private String creatorId;
    private String creatorName;
    private String classroomId;
    private QuestionSet questionSet; // To store the full QuestionSet object
    private long startTime;
    private long endTime;
    private long duration; // in minutes
    private long createdAt;

    public Quiz() {
        // Public no-argument constructor needed for Firestore
    }

    public Quiz(String id, String title, String creatorId, String creatorName, String classroomId, QuestionSet questionSet, long startTime, long endTime, long duration, long createdAt) {
        this.id = id;
        this.title = title;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.classroomId = classroomId;
        this.questionSet = questionSet;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public String getClassroomId() { return classroomId; }
    public void setClassroomId(String classroomId) { this.classroomId = classroomId; }

    public QuestionSet getQuestionSet() { return questionSet; }
    public void setQuestionSet(QuestionSet questionSet) { this.questionSet = questionSet; }

    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }

    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
} 