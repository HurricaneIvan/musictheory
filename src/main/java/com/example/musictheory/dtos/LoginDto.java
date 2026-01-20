package com.example.musictheory.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class LoginDto {

    @NonNull
    @Size(min=5, max=20, message = "Username should be between 5-20 characters")
    private String username;
    @NonNull
    @Size(min=5, max=20, message = "Password should be between 5-20 characters")
    private String password;
    @Email
    private String email;

}
