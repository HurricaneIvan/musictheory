package com.example.musictheory.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "questions")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class Question {

    private ObjectId objectId;
    @Id
    private String uid;
    @NonNull @NotBlank(message = "Question is required")
    @Pattern(regexp = "^[a-zA-Z0-9:.,?]+$")
    @Schema(example = "Who killed Roger Rabbit?")
    private String question;
    private String image;
    @NonNull @NotEmpty(message = "Multiple choice options are required")
    private List<String> options;
    @NonNull @NotBlank(message = "Answer is required")
    private String answer;
    private String proficiency;

    public Question(String uid, String question, String image, List<String> options, String answer, String proficiency) {
        this.uid = uid;
        this.question = question;
        this.image = image;
        this.options = options;
        this.answer = answer;
        this.proficiency = proficiency;
    }

    // expand to include a difficulty tracker. Change proficiency/difficulty dependent on pass/fail rate

}