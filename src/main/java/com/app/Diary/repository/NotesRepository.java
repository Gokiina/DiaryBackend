package com.app.Diary.repository;

import com.app.Diary.model.Notes;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

public interface NotesRepository extends MongoRepository<Notes, String> {
}
