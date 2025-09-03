package com.app.Diary.model;

import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Diary")
public class Diary {
    @Id
    private String id;

    private String userEmail;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String date;

    private String content;

    public Diary() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
