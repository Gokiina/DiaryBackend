package com.app.Diary.controller;

import com.app.Diary.model.User;
import com.app.Diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // --- ENDPOINTS PARA FAVORITOS ---

    // Devuelve la lista de IDs de las frases favoritas del usuario
    @GetMapping("/favorites")
    public ResponseEntity<List<String>> getFavoritePhraseIds() {
        String userEmail = getCurrentUserEmail();
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        return userOptional.map(user -> ResponseEntity.ok(user.getFavoritePhraseIds()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Añade o quita una frase de la lista de favoritos del usuario
    @PostMapping("/favorites/{phraseId}")
    public ResponseEntity<List<String>> toggleFavoritePhrase(@PathVariable String phraseId) {
        String userEmail = getCurrentUserEmail();
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<String> favoriteIds = user.getFavoritePhraseIds();

            if (favoriteIds.contains(phraseId)) {
                // Si ya es favorita, la quita
                favoriteIds.remove(phraseId);
            } else {
                // Si no es favorita, la añade
                favoriteIds.add(phraseId);
            }

            userRepository.save(user);
            return ResponseEntity.ok(favoriteIds);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
