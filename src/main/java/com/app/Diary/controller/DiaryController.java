package com.app.Diary.controller;

import com.app.Diary.model.Diary;
import com.app.Diary.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diary")
public class DiaryController {
    @Autowired
    private DiaryService diaryService;

    @GetMapping
    public List<Diary> getDiaryPage() {
        return diaryService.getDiaryPage();
    }

    @PostMapping
    public Diary addDiaryPage(@RequestBody Diary diary) {
        System.out.println("Datos recibidos en el controlador: " + diary);
        Diary savedDiary = diaryService.addDiaryPage(diary);
        System.out.println("Guardado en la base de datos: " + savedDiary);
        return savedDiary;
    }

    @DeleteMapping("/{id}")
    public void deleteDiaryPage(@PathVariable String id) {
        diaryService.deleteDiaryPage(id);
    }

    @PutMapping("/{id}")
    public Diary updateDiaryPage(@PathVariable String id, @RequestBody Diary diary) {
        diary.setId(id);
        return diaryService.updateDiaryPage(diary);
    }
}
