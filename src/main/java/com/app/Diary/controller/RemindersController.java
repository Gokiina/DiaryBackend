package com.app.Diary.controller;

import com.app.Diary.model.Reminders;
import com.app.Diary.repository.RemindersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reminders")
public class RemindersController {

    @Autowired
    private RemindersRepository remindersRepository;

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping
    public List<Reminders> getReminders() {
        String userEmail = getCurrentUserEmail();
        return remindersRepository.findByUserEmail(userEmail);
    }

    @PostMapping
    public Reminders addReminder(@RequestBody Reminders reminder) {
        String userEmail = getCurrentUserEmail();
        reminder.setUserEmail(userEmail);
        return remindersRepository.save(reminder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reminders> updateReminder(@PathVariable String id, @RequestBody Reminders reminderDetails) {
        String userEmail = getCurrentUserEmail();
        Optional<Reminders> reminderOptional = remindersRepository.findByIdAndUserEmail(id, userEmail);

        if (reminderOptional.isPresent()) {
            Reminders reminder = reminderOptional.get();
            reminder.setTitle(reminderDetails.getTitle());
            reminder.setNotes(reminderDetails.getNotes());
            reminder.setUrl(reminderDetails.getUrl());
            reminder.setDate(reminderDetails.getDate());
            reminder.setTime(reminderDetails.getTime());
            reminder.setFlagged(reminderDetails.isFlagged());
            return ResponseEntity.ok(remindersRepository.save(reminder));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Reminders> toggleComplete(@PathVariable String id) {
        String userEmail = getCurrentUserEmail();
        Optional<Reminders> reminderOptional = remindersRepository.findByIdAndUserEmail(id, userEmail);

        if(reminderOptional.isPresent()) {
            Reminders reminder = reminderOptional.get();
            reminder.setCompleted(!reminder.isCompleted());
            return ResponseEntity.ok(remindersRepository.save(reminder));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable String id) {
        String userEmail = getCurrentUserEmail();
        if (remindersRepository.findByIdAndUserEmail(id, userEmail).isPresent()) {
            remindersRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}