package com.app.Diary.repository;

import com.app.Diary.model.Diary;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DiaryRepository extends MongoRepository<Diary, String> {
}

