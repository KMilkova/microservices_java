package com.example.auth.controller;

import com.example.auth.dto.AuthorizationDto;
import com.example.auth.dto.JwtAuthenticationResponseDto;
import com.example.auth.dto.RegistrationDto;
import com.example.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

@Slf4j
@RestController
@RequestMapping(value = "/api/auth", produces = "application/json")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private final AuthenticationService authenticationService;


    @Operation(summary = "Registration")
    @PostMapping("/sign-up")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Integer signUp(@RequestBody RegistrationDto request) {
        log.info("Received sign-up request for username={}", request.getUsername());
        Integer userId = authenticationService.signUp(request);
        log.info("User registered successfully with userId={}", userId);
        return userId;
    }

    @Operation(summary = "Authorization")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponseDto signIn(@RequestBody AuthorizationDto request) {
        log.info("Received sign-in request for username={}", request.getUsername());
        JwtAuthenticationResponseDto response = authenticationService.signIn(request);
        log.info("User {} signed in successfully, isAdmin={}", request.getUsername(), response.isAdmin());
        return response;
    }
}
