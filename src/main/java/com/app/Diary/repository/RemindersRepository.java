package com.app.Diary.repository;

import com.app.Diary.model.Reminders;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RemindersRepository extends MongoRepository<Reminders, String> {
    List<Reminders> findAllByOrderByCompletedAscFlaggedDescCreatedAtDesc();
    List<Reminders> findByCompletedOrderByFlaggedDescCreatedAtDesc(boolean completed);
}
