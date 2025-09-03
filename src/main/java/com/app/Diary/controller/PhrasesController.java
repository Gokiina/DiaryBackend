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

            // La lógica de a quién pertenece una frase favorita se maneja en una lista en el frontend
            // o se podría crear un modelo de datos más complejo (ej. una tabla UserFavorites).
            // Por ahora, el endpoint simplemente actúa como un interruptor que el frontend puede usar.
            // Para este ejemplo, vamos a simular que el campo 'favorite' es específico del usuario,
            // aunque en un modelo real esto sería más complejo.

            // NOTA: Esta implementación de favoritos es una simplificación. En una app real,
            // el estado "favorito" debería guardarse por usuario, no en la frase misma.
            // Pero para que tu endpoint funcione, lo implementamos así.
            phrase.setFavorite(!phrase.isFavorite());
            return ResponseEntity.ok(phrasesRepository.save(phrase));

        } else {
            return ResponseEntity.notFound().build();
        }
    }
}