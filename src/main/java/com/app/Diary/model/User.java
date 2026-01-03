package com.app.Diary.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String id; // Keeping String to match existing data (likely Mongo ObjectIds)

    @Column(unique = true, nullable = false)
    private String email;

    private String name;

    private String password;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private boolean isPremium = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_favorite_phrases",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "phrase_id")
    )
    private Set<Phrases> favoritePhrases = new HashSet<>();

    // --- Getters y Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public Set<Phrases> getFavoritePhrases() {
        return favoritePhrases;
    }

    public void setFavoritePhrases(Set<Phrases> favoritePhrases) {
        this.favoritePhrases = favoritePhrases;
    }
}
