package com.app.Diary.controller;

import com.app.Diary.model.Reminders;
import com.app.Diary.service.RemindersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class RemindersController {
    private static final Logger logger = LoggerFactory.getLogger(RemindersController.class);
    @Autowired
    private RemindersService remindersService;

    @GetMapping
    public List<Reminders> getReminders(@RequestParam(required = false) Boolean completed) {
        return remindersService.getReminders(completed);
    }

    @PostMapping
    public Reminders addReminder(@RequestBody Reminders reminders) {
        logger.info("Recibida petición para crear recordatorio: {}", reminders);
        try {
            Reminders saved = remindersService.addReminder(reminders);
            logger.info("Recordatorio guardado exitosamente: {}", saved);
            return saved;
        } catch (Exception e) {
            logger.error("Error al guardar recordatorio: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public Reminders updateReminder(@PathVariable String id, @RequestBody Reminders reminders) {
        reminders.setId(id);
        return remindersService.updateReminder(reminders);
    }

    @PatchMapping("/{id}/complete")
    public Reminders toggleComplete(@PathVariable String id) {
        return remindersService.toggleComplete(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable String id) {
        remindersService.deleteReminder(id);
        return ResponseEntity.noContent().build();
    }
}
