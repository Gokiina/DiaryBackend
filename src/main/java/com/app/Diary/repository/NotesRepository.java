package com.app.Diary.repository;

import com.app.Diary.model.Notes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotesRepository extends JpaRepository<Notes, String> {
    List<Notes> findByUserEmail(String userEmail);
    Optional<Notes> findByIdAndUserEmail(String id, String userEmail);
}
