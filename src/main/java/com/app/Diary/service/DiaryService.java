package com.app.Diary.service;

import com.app.Diary.model.Diary;
import com.app.Diary.repository.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiaryService {
    @Autowired
    private DiaryRepository diaryRepository;

    public List<Diary> getDiaryPage() {
        return diaryRepository.findAll();
    }

    public Diary addDiaryPage(Diary diary) {
        return diaryRepository.save(diary);
    }

    public void deleteDiaryPage(String id) {
        if (diaryRepository.existsById(id)) {
            diaryRepository.deleteById(id);
        } else {
            throw new RuntimeException("La entrada con ID " + id + " no existe.");
        }
    }

    public Diary updateDiaryPage(Diary diary) {
        return diaryRepository.save(diary);
    }
}
