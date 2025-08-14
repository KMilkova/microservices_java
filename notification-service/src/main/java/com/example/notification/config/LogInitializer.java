package com.example.notification.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class LogInitializer {
    @PostConstruct
    public void init() {
        MDC.put("service", "auth-service");
    }
}
