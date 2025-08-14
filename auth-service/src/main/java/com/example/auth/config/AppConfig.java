package com.example.auth.config;

import com.example.auth.client.NotificationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public NotificationClient notificationClient(RestTemplate restTemplate,
                                                 @Value("${notification.url}") String notificationUrl) {
        return new NotificationClient(restTemplate, notificationUrl);
    }
}