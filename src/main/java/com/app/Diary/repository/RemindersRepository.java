package com.app.Diary.repository;

import com.app.Diary.model.Reminders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RemindersRepository extends JpaRepository<Reminders, String> {
    // Note: JPA property expressions might need adjustment if sorting by fields that are not direct properties
    // But these look standard.
    List<Reminders> findAllByOrderByCompletedAscFlaggedDescCreatedAtDesc();
    List<Reminders> findByCompletedOrderByFlaggedDescCreatedAtDesc(boolean completed);
    List<Reminders> findByUserEmail(String userEmail);
    Optional<Reminders> findByIdAndUserEmail(String id, String userEmail);
}
