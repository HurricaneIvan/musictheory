package com.example.musictheory.models;

import jakarta.validation.constraints.Email;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;
import java.util.Objects;

@Document(collection = "user")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id @Email
    private String email; //future expansion retrieve forgotten creds
    @NonNull
    private String user; // unique id
    @NonNull
    private String pw;
    private String firstName;
    private String lastName;
    private String proficiency;
    @DocumentReference
    private List<Score> score;

}
