package com.example.notification.controller;

import com.example.UserEventDto;
import com.example.notification.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/user-event")
    public ResponseEntity<String> handleUserEvent(@RequestBody UserEventDto event) {
        log.info("Received user event: {}", event);
        emailService.sendToAdmins(event);
        return ResponseEntity.ok("Notification sent");
    }
}
