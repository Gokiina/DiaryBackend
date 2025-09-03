package com.app.Diary.repository;

import com.app.Diary.model.Diary;
import com.app.Diary.model.Emotions;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmotionsRepository extends MongoRepository<Emotions, String> {
    Emotions findByDate(LocalDate date);
    List<Emotions> findByUserEmail(String userEmail);
    Optional<Emotions> findByIdAndUserEmail(String id, String userEmail);
    Optional<Emotions> findByDateAndUserEmail(LocalDate date, String userEmail);
}


