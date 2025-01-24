package com.app.Diary.repository;

import com.app.Diary.model.Emotions;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EmotionsRepository extends MongoRepository<Emotions, String> {
    Emotions findByDate(LocalDate date);
}


