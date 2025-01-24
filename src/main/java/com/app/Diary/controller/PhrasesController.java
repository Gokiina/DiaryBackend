package com.app.Diary.controller;

import com.app.Diary.model.Phrases;
import com.app.Diary.repository.PhrasesRepository;
import com.app.Diary.service.PhrasesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/phrases")
public class PhrasesController {
    @Autowired
    private PhrasesService phrasesService;

    @Autowired
    private PhrasesRepository phrasesRepository;

    @GetMapping
    public List<Phrases> getPhrases() {
        return phrasesService.getPhrases();
    }

    @PostMapping
    public Phrases addPhrase(@RequestBody Phrases phrases) {
        return phrasesService.addPhrase(phrases);
    }

    @PutMapping
    public Phrases updatePhrase(@RequestBody Phrases phrases) {
        return phrasesService.updatePhrase(phrases);
    }

    @DeleteMapping("/{id}")
    public void deletePhrase(@PathVariable String id) {
        phrasesService.deletePhrase(id);
    }

    @GetMapping("/favorites")
    public List<Phrases> getFavoritePhrases() {
        return phrasesService.getFavoritePhrases();
    }

    @PostMapping("/{id}/favorite")
    public Phrases addFavoritePhrase(@PathVariable String id) {
        return phrasesRepository.findById(id)
                .map(phrase -> {
                    phrase.setFavorite(true);
                    return phrasesRepository.save(phrase);
                })
                .orElseThrow(() -> new RuntimeException("Frase no encontrada con id: " + id));
    }

    @DeleteMapping("/{id}/favorite")
    public Phrases removeFavoritePhrase(@PathVariable String id) {
        return phrasesRepository.findById(id)
                .map(phrase -> {
                    phrase.setFavorite(false);
                    return phrasesRepository.save(phrase);
                })
                .orElseThrow(() -> new RuntimeException("Frase no encontrada con id: " + id));
    }

}

