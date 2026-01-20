package com.example.musictheory.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class User {

    private ObjectId id;
    private String firstName;
    private String lastName;
    @Id
    private String username; // unique id
    private String password;
    @Email
    private String email; //future expansion retrieve forgotten creds
    @Schema(hidden = true)
    private String role;
    @Schema(hidden = true)
    private String proficiency;
    @Schema(hidden = true)
    private List<Integer> score;

    public User(@NonNull String username, @NonNull String password) {
        this.username = username;
        this.password = password;
    }

    public User(String email, @NonNull String username, @NonNull String password, String proficiency) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.proficiency = proficiency;
    }

    public User(String username, String proficiency, List<Integer> score, String firstName) {
        this.username = username;
        this.proficiency = proficiency;
        this.score = score;
        this.firstName = firstName;
    }

    public User(String firstName, String lastName, String username, String password, String email, String role, String proficiency, List<Integer> score) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.proficiency = proficiency;
        this.score = score;
    }

}
