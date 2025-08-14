package com.example;

import lombok.Data;

@Data
public class UserEventDto {
    // геттеры и сеттеры
    private String username;
    private String email;
    private String password;
    private String action; // CREATE, UPDATE, DELETE

}