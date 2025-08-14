package com.example.auth.client;



import com.example.UserEventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationClient {

    private final RestTemplate restTemplate;
    private final String notificationUrl;

    public NotificationClient(RestTemplate restTemplate,
                              @Value("${notification.url}") String notificationUrl) {
        this.restTemplate = restTemplate;
        this.notificationUrl = notificationUrl;
    }

    public void sendUserEvent(UserEventDto dto) {
        try {
            restTemplate.postForEntity(notificationUrl, dto, String.class);
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }
}
