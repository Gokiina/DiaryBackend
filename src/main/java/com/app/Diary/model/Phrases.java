package com.app.Diary.model;

import jakarta.persistence.*;

@Entity
@Table(name = "phrases")
public class Phrases {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String phrase;

    // Removed userEmail as phrases are global

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
