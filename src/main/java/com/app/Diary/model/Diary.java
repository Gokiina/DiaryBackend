package com.app.Diary.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "\"Diary\"")
public class Diary {
    @Id
    @Column(name = "_id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userEmail", referencedColumnName = "email")
    @JsonIgnore // Prevent infinite recursion or leaking user details if not needed
    private User user;

    @Column(name = "userEmail", insertable = false, updatable = false)
    private String userEmail;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String date;

    @Column(columnDefinition = "TEXT")
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserEmail() {
        return userEmail;
    }

    // Allow setting email manually if needed for transient objects,
    // but primarily use setUser
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
