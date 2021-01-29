package com.example.todo_app.model;

import com.orm.SugarRecord;

public class Task extends SugarRecord {
    String title;
    String description;
    Importance importance;
    Status status;

    public Task() {
    }

    public Task(String title, String description, String importance, String status) {
        this.title = title;
        this.description = description;
        this.importance = Importance.valueOf(importance);
        this.status = Status.valueOf(status);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Importance getImportance() {
        return importance;
    }

    public void setImportance(Importance importance) {
        this.importance = importance;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
