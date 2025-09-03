package com.app.Diary.repository;

import com.app.Diary.model.Diary;
import com.app.Diary.model.Reminders;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RemindersRepository extends MongoRepository<Reminders, String> {
    List<Reminders> findAllByOrderByCompletedAscFlaggedDescCreatedAtDesc();
    List<Reminders> findByCompletedOrderByFlaggedDescCreatedAtDesc(boolean completed);
    List<Reminders> findByUserEmail(String userEmail);
    Optional<Reminders> findByIdAndUserEmail(String id, String userEmail);
}
