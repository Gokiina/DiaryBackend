package com.app.Diary.repository;

import com.app.Diary.model.Emotions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmotionsRepository extends JpaRepository<Emotions, String> {
    List<Emotions> findByUserEmail(String userEmail);
    Optional<Emotions> findByIdAndUserEmail(String id, String userEmail);
    Optional<Emotions> findByDateAndUserEmail(LocalDate date, String userEmail);
    Optional<Emotions> findByDate(LocalDate date);
}
