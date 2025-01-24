package com.app.Diary.controller;

import com.app.Diary.model.Emotions;
import com.app.Diary.service.EmotionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/emotions")
public class EmotionsController {

    @Autowired
    private EmotionsService emotionsService;

    @GetMapping
    public List<Emotions> getEmotionsPage() {
        return emotionsService.getEmotionsPage();
    }

    @PostMapping
    public ResponseEntity<Emotions> addEmotionsPage(@RequestBody Emotions emotions) {
        try {
            LocalDate date = emotions.getDate();
            Emotions existingEmotion = emotionsService.getEmotionByDate(date);

            if (existingEmotion != null) {
                existingEmotion.setEmotion(emotions.getEmotion());
                Emotions updatedEmotion = emotionsService.updateEmotionsPage(existingEmotion);
                return ResponseEntity.ok(updatedEmotion);
            } else {
                Emotions savedEmotion = emotionsService.addEmotionsPage(emotions);
                return ResponseEntity.ok(savedEmotion);
            }
        } catch (Exception e) {
            System.err.println("Error al guardar la emoción: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteEmotionsPage(@PathVariable String id) {
        emotionsService.deleteEmotionsPage(id);
    }

    @PutMapping("/{id}")
    public Emotions updateEmotionsPage(@PathVariable String id, @RequestBody Emotions emotions) {
        emotions.setId(id);
        return emotionsService.updateEmotionsPage(emotions);
    }

    @GetMapping("/{date}")
    public ResponseEntity<Emotions> getEmotionByDate(@PathVariable String date) {
        try {
            System.out.println("Fecha recibida en el backend: " + date);
            LocalDate parsedDate = LocalDate.parse(date);

            Emotions emotion = emotionsService.getEmotionByDate(parsedDate);
            if (emotion != null) {
                return ResponseEntity.ok(emotion);
            } else {
                System.out.println("No se encontró emoción para la fecha: " + date);
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            System.err.println("Error al procesar la fecha: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
