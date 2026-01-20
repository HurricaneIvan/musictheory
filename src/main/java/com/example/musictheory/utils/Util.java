package com.example.musictheory.utils;

import com.example.musictheory.dtos.LoginDto;
import com.example.musictheory.dtos.QuestionDto;
import com.example.musictheory.dtos.UserDto;
import com.example.musictheory.models.Proficiency;
import com.example.musictheory.models.Question;
import com.example.musictheory.models.User;
import com.google.json.JsonSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.regex.Pattern;

@Component
public class Util {
    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ID_LENGTH = 5;
    private static final SecureRandom secureRandom = new SecureRandom();

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

    public boolean isValidUid(String input) {
        String regex = "^[a-zA-Z0-9]{5}$";
        return Pattern.matches(regex, input);
    }

    public LoginDto sanitizeLogin(LoginDto user) throws IOException {
        LoginDto sanitized = new LoginDto();
        if(isNotNullOrEmpty(user.getUsername())){
            sanitized.setUsername(JsonSanitizer.sanitize(user.getUsername()).replaceAll("^\"|\"$", ""));
        }
        if(isNotNullOrEmpty(user.getPassword())){
            sanitized.setPassword(JsonSanitizer.sanitize(user.getPassword()).replaceAll("^\"|\"$", ""));
        }
        return sanitized;
    }

    public User validateAndSanitizeUser(UserDto user) throws IOException {
        User sanitized = new User();
        logger.info("validate :: "+ user);
        if(user != null && !user.toString().isBlank()){
            String regex = "^[a-zA-Z0-9-_]+$";
            if(isNotNullOrEmpty(user.getFirstname())){
                //JsonSanitizer adds second "" to valid input, replaceAll to negate
                String cleanJson = JsonSanitizer.sanitize(user.getFirstname()).replaceAll("^\"|\"$", "");
                sanitized.setFirstName(cleanJson);
            }
            if(isNotNullOrEmpty(user.getLastname())){
                String cleanJson = JsonSanitizer.sanitize(user.getLastname()).replaceAll("^\"|\"$", "");
                sanitized.setLastName(cleanJson);
            }
            if(isNotNullOrEmpty(user.getUsername()) && user.getUsername().matches(regex)){
                sanitized.setUsername(user.getUsername());
            }
            if(isNotNullOrEmpty(user.getPassword()) && user.getPassword().matches(regex)){
                String cleanJson = JsonSanitizer.sanitize(user.getPassword()).replaceAll("^\"|\"$", "");
                sanitized.setPassword(cleanJson);
            }
            if(isNotNullOrEmpty(user.getEmail())){
                sanitized.setEmail(user.getEmail());
            }

            return sanitized;
        } else {
            throw new IOException("Invalid User input");
        }
    }

    public Question questionValidator(QuestionDto question) throws IOException {
        Question validated = new Question();
        if(question != null && !question.toString().isBlank()){
            String regex = "^[a-zA-Z0-9:.,?]+$";
            if(isNotNullOrEmpty(question.getQuestion()) && question.getQuestion().matches(regex)){
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
