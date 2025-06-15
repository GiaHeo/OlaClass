package com.example.olaclass.data.model;

import java.util.List;

public class Question {
    private String id;
    private String type; // "multiple_choice", "essay", "fill_blank"
    private String content;
    private List<String> options; // for multiple_choice
    private String answer; // correct answer (for auto grading)
    private List<String> correctAnswers; // for multi-answer or fill_blank

    public Question() {}

    public Question(String id, String type, String content, List<String> options, String answer, List<String> correctAnswers) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.options = options;
        this.answer = answer;
        this.correctAnswers = correctAnswers;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public List<String> getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(List<String> correctAnswers) { this.correctAnswers = correctAnswers; }
}
