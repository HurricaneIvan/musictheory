package com.example.musictheory.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String firstname;
    private String lastname;
    @Size(min=5, max=20, message = "Username should be between 5-20 characters")
    private String username;
    @Size(min=5, max=20, message = "Password should be between 5-20 characters")
    private String password;
    @Email
    private String email;

    public UserDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
