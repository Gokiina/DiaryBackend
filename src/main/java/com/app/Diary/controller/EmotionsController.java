package com.app.Diary.controller;

import com.app.Diary.model.Emotions;
import com.app.Diary.repository.EmotionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/emotions")
public class EmotionsController {

    @Autowired
    private EmotionsRepository emotionsRepository;

    // Método para obtener el email del usuario logueado
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping
    public List<Emotions> getEmotionsPage() {
        String userEmail = getCurrentUserEmail();
        return emotionsRepository.findByUserEmail(userEmail);
    }

    @PostMapping
    public ResponseEntity<Emotions> addOrUpdateEmotion(@RequestBody Emotions emotions) {
        String userEmail = getCurrentUserEmail();
        LocalDate date = emotions.getDate();

        // Busca si ya existe una emoción para este usuario en esta fecha
        Optional<Emotions> existingEmotionOpt = emotionsRepository.findByDateAndUserEmail(date, userEmail);

        if (existingEmotionOpt.isPresent()) {
            // Si existe, la actualiza
            Emotions existingEmotion = existingEmotionOpt.get();
            existingEmotion.setEmotion(emotions.getEmotion());
            return ResponseEntity.ok(emotionsRepository.save(existingEmotion));
        } else {
            // Si no existe, crea una nueva y la vincula al usuario
            emotions.setUserEmail(userEmail);
            return ResponseEntity.ok(emotionsRepository.save(emotions));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmotionsPage(@PathVariable String id) {
        String userEmail = getCurrentUserEmail();
        // Solo se puede borrar si la emoción pertenece al usuario
        if (emotionsRepository.findByIdAndUserEmail(id, userEmail).isPresent()) {
            emotionsRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Emotions> updateEmotionsPage(@PathVariable String id, @RequestBody Emotions emotionsDetails) {
        String userEmail = getCurrentUserEmail();
        Optional<Emotions> emotionOptional = emotionsRepository.findByIdAndUserEmail(id, userEmail);

        if (emotionOptional.isPresent()) {
            Emotions emotion = emotionOptional.get();
            emotion.setEmotion(emotionsDetails.getEmotion());
            emotion.setDate(emotionsDetails.getDate());
            return ResponseEntity.ok(emotionsRepository.save(emotion));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/{date}")
    public ResponseEntity<Emotions> getEmotionByDate(@PathVariable String date) {
        String userEmail = getCurrentUserEmail();
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            // Busca la emoción para la fecha y el usuario específicos
            Optional<Emotions> emotion = emotionsRepository.findByDateAndUserEmail(parsedDate, userEmail);

            return emotion.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.noContent().build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

