package com.app.Diary.repository;

import com.app.Diary.model.Diary;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends MongoRepository<Diary, String> {
    List<Diary> findByUserEmail(String userEmail);
    Optional<Diary> findByIdAndUserEmail(String id, String userEmail);
}

