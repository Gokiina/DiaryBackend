package com.app.Diary.service;

import com.app.Diary.model.Reminders;
import com.app.Diary.repository.RemindersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class RemindersService {
    @Autowired
    private RemindersRepository remindersRepository;

    public List<Reminders> getReminders(Boolean completed) {
        if (completed != null) {
            return remindersRepository.findByCompletedOrderByFlaggedDescCreatedAtDesc(completed);
        }
        return remindersRepository.findAllByOrderByCompletedAscFlaggedDescCreatedAtDesc();
    }

    public Reminders addReminder(Reminders reminders) {
        reminders.setCompleted(false);
        reminders.setCreatedAt(new Date());

        if (reminders.getDate() != null && !reminders.getDate().isEmpty()) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = inputFormat.parse(reminders.getDate());
                reminders.setDate(inputFormat.format(date));
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd");
            }
        }

        return remindersRepository.save(reminders);
    }

    public void deleteReminder(String id) {
        if (remindersRepository.existsById(id)) {
            remindersRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Reminder with ID " + id + " does not exist.");
        }
    }

    public Reminders updateReminder(Reminders reminders) {
        Reminders existing = remindersRepository.findById(reminders.getId())
                .orElseThrow(() -> new IllegalArgumentException("Reminder not found"));
        if (reminders.isCompleted() != existing.isCompleted()) {
            existing.setCompleted(reminders.isCompleted());
        }

        existing.setTitle(reminders.getTitle());
        existing.setNotes(reminders.getNotes());
        existing.setUrl(reminders.getUrl());
        existing.setDate(reminders.getDate());
        existing.setTime(reminders.getTime());
        existing.setFlagged(reminders.isFlagged());

        return remindersRepository.save(existing);
    }

    public Reminders toggleComplete(String id) {
        Reminders reminder = remindersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reminder not found"));
        reminder.setCompleted(!reminder.isCompleted());
        return remindersRepository.save(reminder);
    }
}