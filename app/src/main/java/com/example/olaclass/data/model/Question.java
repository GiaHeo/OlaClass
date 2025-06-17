package com.example.olaclass.data.model;

import java.util.List;
import java.util.ArrayList;

public class Question {
    private String id;
    private String type; // "multiple_choice", "essay", "fill_blank"
    private String content;
    private List<Option> options; // for multiple_choice

    public Question() {
        this.options = new ArrayList<>();
    }

    public Question(String id, String type, String content, List<Option> options) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.options = options;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<Option> getOptions() { return options; }
    public void setOptions(List<Option> options) { this.options = options; }

    public static class Option {
        private String content;
        private boolean isCorrect;

        public Option() {}

        public Option(String content, boolean isCorrect) {
            this.content = content;
            this.isCorrect = isCorrect;
        }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public boolean isCorrect() { return isCorrect; }
        public void setCorrect(boolean correct) { isCorrect = correct; }
    }
}
