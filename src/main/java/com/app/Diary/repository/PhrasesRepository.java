package com.app.Diary.repository;

import com.app.Diary.model.Phrases;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PhrasesRepository extends MongoRepository<Phrases, String> {
    List<Phrases> findByIsFavoriteTrue();
}