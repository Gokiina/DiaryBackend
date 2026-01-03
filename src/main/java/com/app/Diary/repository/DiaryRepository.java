package com.app.Diary.repository;

import com.app.Diary.model.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, String> {
    // We map userEmail to User.email in the entity, so we can query by the userEmail property directly
    // because we have a field "userEmail" mapped to the column or via the relationship if we name it properly.
    // In Diary entity, we have `private String userEmail;` mapped as insertable=false, updatable=false.
    // So we can query by it.
    List<Diary> findByUserEmail(String userEmail);
    Optional<Diary> findByIdAndUserEmail(String id, String userEmail);
}
