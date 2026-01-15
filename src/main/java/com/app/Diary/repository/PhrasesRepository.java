package com.app.Diary.repository;

import com.app.Diary.model.Phrases;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhrasesRepository extends JpaRepository<Phrases, Long> {
    // Phrases are now global, so we don't need filtering by userEmail
    // But if we ever re-introduce user phrases, we might need a method.
    // For now, standard JpaRepository methods suffice.
}
