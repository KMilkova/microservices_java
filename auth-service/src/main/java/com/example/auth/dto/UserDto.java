package com.example.auth.dto;

import com.example.auth.model.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;

    public UserDto(Integer id, String username, String password, String email, String firstName, String lastName, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
}


