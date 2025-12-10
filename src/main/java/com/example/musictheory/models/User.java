package com.example.musictheory.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document(collection = "user")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private ObjectId objectId;
    @Id @Email @NotBlank(message = "Email is required")
    private String email; //future expansion retrieve forgotten creds
    @NonNull @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$")
    @Schema(example = "Defa_lt-1")
    private String user; // unique id
    @NonNull @NotBlank(message = "Password is required")
    private String pw;
    private String firstName;
    private String lastName;
    private String proficiency;
    @DocumentReference
    private List<Score> score;

}
