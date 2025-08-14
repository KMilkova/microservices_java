package com.example.auth.service;

import com.example.auth.dto.AuthorizationDto;
import com.example.auth.dto.JwtAuthenticationResponseDto;
import com.example.auth.dto.RegistrationDto;
import com.example.auth.model.entity.User;
import com.example.auth.model.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthorizationService service;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public Integer signUp(RegistrationDto dto) {
        log.info("signUp called with username={}", dto.getUsername());
        service.isExistsByUsername(dto.getUsername());

        var authorization = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        Integer userId = service.create(authorization);
        log.info("signUp completed for username={} with userId={}", dto.getUsername(), userId);
        return userId;
    }

    public JwtAuthenticationResponseDto signIn(AuthorizationDto request) {
        log.info("signIn called for username={}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var user = service.userDetailsService().loadUserByUsername(request.getUsername());
        var jwt = jwtService.generateToken(user);
        var isAdmin = service.isAdmin(user);

        log.info("signIn successful for username={}, isAdmin={}", request.getUsername(), isAdmin);
        return new JwtAuthenticationResponseDto(jwt, isAdmin);
    }
}
