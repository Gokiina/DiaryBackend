package com.app.Diary.service;

import com.app.Diary.model.Emotions;
import com.app.Diary.repository.EmotionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmotionsService {

    @Autowired
    private EmotionsRepository emotionsRepository;

    public List<Emotions> getEmotionsPage() {
        return emotionsRepository.findAll();
    }

    public Emotions addEmotionsPage(Emotions emotions) {
        try {
            Optional<Emotions> existingEmotion = Optional.ofNullable(emotionsRepository.findByDate(emotions.getDate()));
            if (existingEmotion.isPresent()) {
                Emotions emotionToUpdate = existingEmotion.get();
                emotionToUpdate.setEmotion(emotions.getEmotion());
                return emotionsRepository.save(emotionToUpdate);
            } else {
                return emotionsRepository.save(emotions);
            }
        } catch (Exception e) {
            System.err.println("Error al guardar la emoción: " + e.getMessage());
            throw new RuntimeException("Error al guardar la emoción: " + e.getMessage());
        }
    }

    public void deleteEmotionsPage(String id) {
        emotionsRepository.deleteById(id);
    }

    public Emotions updateEmotionsPage(Emotions emotions) {
        return emotionsRepository.save(emotions);
    }

    public Emotions getEmotionByDate(LocalDate date) {
        return emotionsRepository.findByDate(date);
    }

}
