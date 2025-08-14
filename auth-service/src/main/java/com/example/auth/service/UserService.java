package com.example.auth.service;

import com.example.UserEventDto;
import com.example.auth.client.NotificationClient;
import com.example.auth.dto.UserDto;
import com.example.auth.exception.NotFoundException;
import com.example.auth.model.entity.User;
import com.example.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private NotificationClient notificationClient;

    public Integer getCurrentUserId() {
        var authHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest()
                .getHeader("Authorization");

        var jwt = authHeader.substring(7); // убираем "Bearer "
        return jwtService.extractUserId(jwt);
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );
    }

    private User fromDto(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getUsername(),
                null,
                userDto.getEmail(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getRole()
        );
    }

    public List<UserDto> getAllUsers() {
        log.info("getAllUsers called");
        List<UserDto> users = userRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        log.info("getAllUsers returned {} users", users.size());
        return users;
    }

    public Optional<UserDto> getUserById(Integer id){
        log.info("getUserById called with id={}", id);
        checkAccess(id);
        Optional<UserDto> userDto = userRepository.findById(id)
                .map(this::toDto);
        log.info("getUserById result present={}", userDto.isPresent());
        return userDto;
    }

    public Optional<UserDto> getUserByUsername(String username){
        log.info("getUserByUsername called with username={}", username);
        Optional<UserDto> userDto = userRepository.findByUsername(username)
                .map(this::toDto);
        log.info("getUserByUsername result present={}", userDto.isPresent());
        return userDto;
    }

    private void checkAccess(Integer userId) {
        log.info("checkAccess called for userId={}", userId);
        Integer currentUserId = getCurrentUserId();
        boolean isAdmin = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !currentUserId.equals(userId)) {
            log.warn("Access denied for userId={} (currentUserId={}, isAdmin={})", userId, currentUserId, isAdmin);
            throw new SecurityException("You can only modify your own account");
        }
        log.info("Access granted for userId={}", userId);
    }

    @Transactional
    public UserDto updateUser(Integer id, UserDto dto) {
        log.info("updateUser called for id={} with dto={}", id, dto);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for update with id={}", id);
                    return new NotFoundException("User not found");
                });

        checkAccess(id);
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getRole() != null) user.setRole(dto.getRole());

        UserEventDto event = new UserEventDto();
        event.setUsername(user.getUsername());
        event.setEmail(user.getEmail());
        event.setPassword(dto.getPassword());
        event.setAction("UPDATE");

        notificationClient.sendUserEvent(event);
        log.info("updateUser completed for id={}", id);

        return toDto(user);
    }

    @Transactional
    public void deleteUser(Integer id) {
        log.info("deleteUser called for id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for delete with id={}", id);
                    return new NotFoundException("User not found");
                });

        checkAccess(id);

        UserEventDto event = new UserEventDto();
        event.setUsername(user.getUsername());
        event.setEmail(user.getEmail());
        event.setPassword(user.getPassword());
        event.setAction("DELETE");

        notificationClient.sendUserEvent(event);
        userRepository.deleteById(id);
        log.info("deleteUser completed for id={}", id);
    }



}
