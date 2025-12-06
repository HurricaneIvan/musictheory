package com.example.musictheory.dtos;

import lombok.*;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDto {

    private String uid;
    private String question;
    private String image;
    private List<String> options;
    private String answer;
    private String proficiency;

    public QuestionDto(String question, List<String> options, String answer) {
        this.question = question;
        this.options = options;
        this.answer = answer;
    }
}
