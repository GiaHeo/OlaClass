package com.example.olaclass.data.model;

import com.google.firebase.Timestamp;

public class Announcement {
    private String id;
    private String title;
    private String content;
    private String classroomId;
    private String createdBy;
    private Timestamp createdAt;

    // Required empty constructor for Firestore
    public Announcement() {
    }

    public Announcement(String title, String content, String classroomId, String createdBy, Timestamp createdAt) {
        this.title = title;
        this.content = content;
        this.classroomId = classroomId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    // Getters and setters
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
