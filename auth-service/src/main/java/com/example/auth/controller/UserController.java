package com.example.auth.controller;

import com.example.auth.dto.UserDto;
import com.example.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Request: getAllUsers()");
        List<UserDto> users = userService.getAllUsers();
        log.info("Response: getAllUsers() -> {} users found", users.size());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public UserDto updateById(@PathVariable Integer id,
                              @Valid @RequestBody UserDto dto) {
        log.info("Request: updateById(id={}, dto={})", id, dto);
        UserDto updated = userService.updateUser(id, dto);
        log.info("Response: updateById -> {}", updated);
        return updated;
    }

    @DeleteMapping("/delete/{id}")
    public void deleteById(@PathVariable Integer id) {
        log.info("Request: deleteById(id={})", id);
        userService.deleteUser(id);
        log.info("Response: deleteById -> user deleted successfully");
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<Optional<UserDto>> getUserById(@PathVariable Integer id) {
        log.info("Request: getUserById(id={})", id);
        Optional<UserDto> user = userService.getUserById(id);
        log.info("Response: getUserById -> {}", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


}
