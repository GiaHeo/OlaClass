package com.example.olaclass.data.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

public class Student {
    private String userId;
    @PropertyName("displayName")
    private String name; // Tên học sinh, có thể lấy từ collection users
    private Timestamp joinedAt;

    public Student() {
        // Public no-argument constructor needed for Firestore
    }

    public Student(String userId, String name, Timestamp joinedAt) {
        this.userId = userId;
        this.name = name;
        this.joinedAt = joinedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }
} 