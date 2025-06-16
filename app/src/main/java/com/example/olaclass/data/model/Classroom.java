package com.example.olaclass.data.model;

public class Classroom {
    private String id;
    private String name;
    private String description;
    private String teacherId;
    private String subject;
    private String inviteCode;

    public Classroom() {}

    public Classroom(String id, String name, String description, String teacherId, String subject) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.teacherId = teacherId;
        this.subject = subject;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }

    @Override
    public String toString() {
        return "Classroom{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", teacherId='" + teacherId + '\'' +
                ", subject='" + subject + '\'' +
                ", inviteCode='" + inviteCode + '\'' +
                '}';
    }
}
