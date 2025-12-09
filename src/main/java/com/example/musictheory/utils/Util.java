package com.example.musictheory.utils;

import com.example.musictheory.dtos.QuestionDto;
import com.example.musictheory.models.Question;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SecureRandom;

@Component
public class Util {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ID_LENGTH = 5;
    private static final SecureRandom secureRandom = new SecureRandom();
    private final String  regrex = "^[a-zA-Z0-9:.,?]+$";

    public String generateUid(){
        StringBuilder sb = new StringBuilder(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            sb.append(ALPHABET.charAt(secureRandom.nextInt(ALPHABET.length())));
        }
       return sb.toString();
    }

    public Boolean isNotNullOrEmpty(String elem) throws IOException {
        if (elem != null && !elem.isBlank()){
            return Boolean.TRUE;
        } else {
            throw new IOException("Invalid Parameter: Parameter is Missing");
        }
    }

//    public String validateUid(String uid) throws IOException {
//        if(StringUtils.isAlphanumeric(uid)) {
//            return uid;
//        } else {
//            throw new IOException("Invalid Uid Provided");
//        }
//    }

    public Question questionValidator(QuestionDto question) throws IOException {
        Question validated = new Question();
        if(question != null && !question.toString().isBlank()){
            if(isNotNullOrEmpty(question.getQuestion()) && question.getQuestion().matches(regrex)){
                validated.setQuestion(question.getQuestion());
            }
            if(question.getImage().isBlank()){
                validated.setImage(question.getImage());
            }
            if(!question.getOptions().isEmpty()){
                validated.setOptions(question.getOptions());
            }
            if(isNotNullOrEmpty(question.getAnswer())){
                validated.setAnswer(question.getAnswer());
            }
            if(question.getProficiency().isBlank()){
                validated.setProficiency(question.getProficiency());
            }
        }
        return validated;
    }
}
