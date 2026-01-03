package com.app.Diary.controller;

import com.app.Diary.model.Phrases;
import com.app.Diary.model.User;
import com.app.Diary.repository.PhrasesRepository;
import com.app.Diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhrasesRepository phrasesRepository;

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // --- ENDPOINTS PARA FAVORITOS ---

    // Devuelve la lista de IDs de las frases favoritas del usuario
    @GetMapping("/favorites")
    public ResponseEntity<List<Long>> getFavoritePhraseIds() {
        String userEmail = getCurrentUserEmail();
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isPresent()) {
            Set<Phrases> favorites = userOptional.get().getFavoritePhrases();
            List<Long> favoriteIds = favorites.stream()
                                            .map(Phrases::getId)
                                            .collect(Collectors.toList());
            return ResponseEntity.ok(favoriteIds);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Añade o quita una frase de la lista de favoritos del usuario
    @PostMapping("/favorites/{phraseId}")
    public ResponseEntity<List<Long>> toggleFavoritePhrase(@PathVariable Long phraseId) {
        String userEmail = getCurrentUserEmail();
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Phrases> favorites = user.getFavoritePhrases();

            // Check if phrase is already in favorites
            // We use standard Set operations. To remove, we need to find the object with the same ID or rely on equals/hashCode.
            // Since we haven't overridden equals/hashCode in Phrases, we should find by ID.

            Optional<Phrases> existingFavorite = favorites.stream()
                .filter(p -> p.getId().equals(phraseId))
                .findFirst();

            if (existingFavorite.isPresent()) {
                favorites.remove(existingFavorite.get());
            } else {
                // Fetch the phrase to add it
                Optional<Phrases> phraseToAdd = phrasesRepository.findById(phraseId);
                if (phraseToAdd.isPresent()) {
                    favorites.add(phraseToAdd.get());
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
            }

            userRepository.save(user);

            List<Long> currentFavoriteIds = user.getFavoritePhrases().stream()
                                                .map(Phrases::getId)
                                                .collect(Collectors.toList());
            return ResponseEntity.ok(currentFavoriteIds);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
