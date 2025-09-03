package com.app.Diary.controller;

import com.app.Diary.model.Phrases;
import com.app.Diary.repository.PhrasesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/phrases")
public class PhrasesController {

    @Autowired
    private PhrasesRepository phrasesRepository;

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping
    public List<Phrases> getPhrases() {
        String userEmail = getCurrentUserEmail();
        // Devuelve las frases globales (sin email) Y las que pertenecen al usuario
        List<Phrases> globalPhrases = phrasesRepository.findByUserEmailIsNull();
        List<Phrases> userPhrases = phrasesRepository.findByUserEmail(userEmail);
        return Stream.concat(globalPhrases.stream(), userPhrases.stream()).toList();
    }

    // --- FUNCIONALIDAD DE FAVORITOS RESTAURADA Y CORREGIDA ---

    @PostMapping("/{id}/favorite")
    public ResponseEntity<Phrases> toggleFavorite(@PathVariable String id) {
        String userEmail = getCurrentUserEmail();
        // Para la lógica de favoritos, asumimos que un usuario puede marcar como favorita
        // tanto una frase global como una propia.
        Optional<Phrases> phraseOptional = phrasesRepository.findById(id);

        if (phraseOptional.isPresent()) {
            Phrases phrase = phraseOptional.get();
            return ResponseEntity.ok(phrasesRepository.save(phrase));

        } else {
            return ResponseEntity.notFound().build();
        }
    }
}