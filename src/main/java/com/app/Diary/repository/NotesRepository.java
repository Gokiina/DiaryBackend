package com.app.Diary.repository;

import com.app.Diary.model.Diary;
import com.app.Diary.model.Notes;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface NotesRepository extends MongoRepository<Notes, String> {
    List<Notes> findByUserEmail(String userEmail);
    Optional<Notes> findByIdAndUserEmail(String id, String userEmail);
}
