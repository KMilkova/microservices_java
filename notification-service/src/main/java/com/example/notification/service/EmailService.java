package com.example.notification.service;

import com.example.UserEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    public void sendToAdmins(UserEventDto event) {
        String subject = "User " + event.getAction() + " " + event.getUsername();
        String text = "Username - " + event.getUsername() +
                ", password - " + event.getPassword() +
                ", email - " + event.getEmail() +
                " был " + event.getAction();

        log.info("Sending message: {}\n{}", subject, text);
    }
}
