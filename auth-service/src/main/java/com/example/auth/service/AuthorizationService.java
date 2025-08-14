package com.example.auth.service;

import com.example.UserEventDto;
import com.example.auth.client.NotificationClient;
import com.example.auth.model.entity.User;
import com.example.auth.model.enums.Role;
import com.example.auth.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthorizationService {
    private final UserRepository repository;
    @Lazy
    private NotificationClient notificationClient;

    public User save(User user) {
        log.info("Saving user: {}", user.getUsername());
        return repository.save(user);
    }

    public Integer create(User user){
        log.info("Creating user: {}", user.getUsername());
        if (repository.existsByUsername(user.getUsername())){
            log.warn("User already exists: {}", user.getUsername());
            throw new RuntimeException("A user with this username already exists");
        }
        User savedUser = save(user);

        UserEventDto event = new UserEventDto();
        event.setUsername(savedUser.getUsername());
        event.setEmail(savedUser.getEmail());
        event.setPassword(savedUser.getPassword());
        event.setAction("CREATE");

        notificationClient.sendUserEvent(event);
        log.info("User creation event sent: {}", savedUser.getUsername());


        return savedUser.getId();
    }

    public User getByUsername(String username){
        log.info("getByUsername called with username={}", username);
        return repository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username={}", username);
                    return new UsernameNotFoundException("User not found");
                });
    }

    public UserDetailsService userDetailsService() {
        log.info("userDetailsService called");
        return this::getByUsername;
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("getCurrentUser called for username={}", username);
        return getByUsername(username);
    }

    public boolean isAdmin(UserDetails userDetails){
        var isAdmin= false;
        if (userDetails instanceof User customUserDetails) {
            isAdmin = customUserDetails.getRole().equals(Role.ROLE_ADMIN);
        }
        log.info("isAdmin called for user={} result={}", userDetails.getUsername(), isAdmin);
        return isAdmin;
    }


    public void isExistsByUsername(String username) {
        log.info("isExistsByUsername called with username={}", username);
        if (repository.existsByUsername(username)) {
            log.warn("User already exists with username={}", username);
            throw new EntityNotFoundException(String.format("Exists Employee %s", username));
        }
    }
}
