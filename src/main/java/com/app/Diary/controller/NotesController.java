package com.app.Diary.controller;

import com.app.Diary.model.Notes;
import com.app.Diary.service.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NotesController {
    @Autowired
    private NotesService notesService;

    @GetMapping
    public List<Notes> getNotes(){
        return notesService.getNotes();
    }

    @PostMapping
    public Notes addNotes(@RequestBody Notes notes) {
        return notesService.addNote(notes);
    }

    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable String id){
        notesService.deleteNote(id);
    }

    @PutMapping("/{id}")
    public Notes updateNote(@PathVariable String id, @RequestBody Notes note) {
        note.setId(id);
        return notesService.updateNote(note);
    }
}
