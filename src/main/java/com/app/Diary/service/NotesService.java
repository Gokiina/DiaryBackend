package com.app.Diary.service;

import com.app.Diary.model.Notes;
import com.app.Diary.repository.NotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotesService {
    @Autowired
    private NotesRepository notesRepository;

    public List<Notes> getNotes(){
        return notesRepository.findAll();
    }

    public Notes addNote(Notes notes) {
        if (notes.getTextNote() == null) {
            notes.setTextNote("");
        }

        Notes savedNote = notesRepository.save(notes);
        return savedNote;
    }

    public void deleteNote(String id){
        notesRepository.deleteById(id);
    }
    public Notes updateNote(Notes note) {
        if (!notesRepository.existsById(note.getId())) {
            throw new RuntimeException("Nota no encontrada");
        }

        if (note.getTextNote() == null) {
            note.setTextNote("");
        }

        Notes savedNote = notesRepository.save(note);

        return savedNote;
    }

    public boolean hasEmptyNotes() {
        return notesRepository.findAll().stream()
                .anyMatch(note -> note.getTextNote() == null || note.getTextNote().trim().isEmpty());
    }
}
