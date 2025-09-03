package com.app.Diary.controller;

import com.app.Diary.model.Diary;
import com.app.Diary.repository.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/diary")
public class DiaryController {

    @Autowired
    private DiaryRepository diaryRepository;

    // Método para obtener el email del usuario logueado
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping
    public List<Diary> getDiaryPages() {
        String userEmail = getCurrentUserEmail();
        return diaryRepository.findByUserEmail(userEmail);
    }

    @PostMapping
    public Diary addDiaryPage(@RequestBody Diary diary) {
        String userEmail = getCurrentUserEmail();
        diary.setUserEmail(userEmail); // Se asigna el usuario a la nueva entrada
        return diaryRepository.save(diary);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiaryPage(@PathVariable String id) {
        String userEmail = getCurrentUserEmail();
        // Solo se puede borrar si la entrada pertenece al usuario
        if (diaryRepository.findByIdAndUserEmail(id, userEmail).isPresent()) {
            diaryRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Diary> updateDiaryPage(@PathVariable String id, @RequestBody Diary diaryDetails) {
        String userEmail = getCurrentUserEmail();
        Optional<Diary> diaryOptional = diaryRepository.findByIdAndUserEmail(id, userEmail);

        if (diaryOptional.isPresent()) {
            Diary diary = diaryOptional.get();
            diary.setContent(diaryDetails.getContent());
            diary.setDate(diaryDetails.getDate());
            return ResponseEntity.ok(diaryRepository.save(diary));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}