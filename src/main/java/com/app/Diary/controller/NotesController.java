package com.app.Diary.controller;

import com.app.Diary.model.Notes;
import com.app.Diary.repository.NotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
public class NotesController {
    @Autowired
    private NotesRepository notesRepository;

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping
    public List<Notes> getNotes(){
        String userEmail = getCurrentUserEmail();
        return notesRepository.findByUserEmail(userEmail);
    }

    @PostMapping
    public Notes addNotes(@RequestBody Notes notes) {
        String userEmail = getCurrentUserEmail();
        notes.setUserEmail(userEmail);
        return notesRepository.save(notes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable String id){
        String userEmail = getCurrentUserEmail();
        if (notesRepository.findByIdAndUserEmail(id, userEmail).isPresent()) {
            notesRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notes> updateNote(@PathVariable String id, @RequestBody Notes noteDetails) {
        String userEmail = getCurrentUserEmail();
        Optional<Notes> noteOptional = notesRepository.findByIdAndUserEmail(id, userEmail);

        if (noteOptional.isPresent()) {
            Notes note = noteOptional.get();
            note.setTextNote(noteDetails.getTextNote());
            return ResponseEntity.ok(notesRepository.save(note));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}