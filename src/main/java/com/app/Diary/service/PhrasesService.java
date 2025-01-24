package com.app.Diary.service;

import com.app.Diary.model.Phrases;
import com.app.Diary.repository.PhrasesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhrasesService {
    @Autowired
    private PhrasesRepository phrasesRepository;

    public List<Phrases> getPhrases() {
        return phrasesRepository.findAll();
    }

    public Phrases addPhrase(Phrases phrases) {
        return phrasesRepository.save(phrases);
    }

    public Phrases updatePhrase(Phrases phrases) {
        return phrasesRepository.save(phrases);
    }

    public void deletePhrase(String id) {
        phrasesRepository.deleteById(id);
    }

    public List<Phrases> getFavoritePhrases() {
        return phrasesRepository.findByIsFavoriteTrue();
    }

    public Phrases addFavoritePhrase(String id) {
        Phrases phrase = phrasesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Frase no encontrada"));
        phrase.setFavorite(true);
        return phrasesRepository.save(phrase);
    }

    public Phrases removeFavoritePhrase(String id) {
        Phrases phrase = phrasesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Frase no encontrada"));
        phrase.setFavorite(false);
        return phrasesRepository.save(phrase);
    }
}
