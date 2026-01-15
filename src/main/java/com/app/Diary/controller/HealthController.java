package com.app.Diary.controller;

import com.app.Diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");

        try {
            long userCount = userRepository.count();
            status.put("database", "CONNECTED");
            status.put("userCount", userCount);
        } catch (Exception e) {
            status.put("database", "ERROR");
            status.put("error", e.getMessage());
        }

        return ResponseEntity.ok(status);
    }
}
