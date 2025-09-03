package com.app.Diary.repository;

import com.app.Diary.model.Diary;
import com.app.Diary.model.Emotions;
import com.app.Diary.model.Phrases;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PhrasesRepository extends MongoRepository<Phrases, String> {
    List<Phrases> findByIsFavoriteTrue();
    List<Phrases> findByUserEmail(String userEmail);
    Optional<Phrases> findByIdAndUserEmail(String id, String userEmail);
    List<Phrases> findByUserEmailIsNull();
}