package com.app.Diary.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "notes")
public class Notes {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userEmail", referencedColumnName = "email")
    @JsonIgnore
    private User user;

    @Column(name = "userEmail", insertable = false, updatable = false)
    private String userEmail;

    @Column(columnDefinition = "TEXT")
    private String textNote;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTextNote() {
        return textNote;
    }

    public void setTextNote(String textNote) {
        this.textNote = textNote;
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

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
