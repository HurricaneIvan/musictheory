package com.example.musictheory.utils;

import com.example.musictheory.dtos.LoginDto;
import com.example.musictheory.dtos.QuestionDto;
import com.example.musictheory.dtos.UserDto;
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

    public Boolean isNotNullOrEmpty(String elem) {
        if (elem != null && !elem.isBlank()){
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public boolean isValidUid(String input) {
        String regex = "^[a-zA-Z0-9]{5}$";
        return Pattern.matches(regex, input);
    }

    public LoginDto sanitizeLogin(LoginDto user) throws IOException {
        LoginDto sanitized = new LoginDto();
        if(isNotNullOrEmpty(user.getUsername()) && isNotNullOrEmpty(user.getPassword())){
            sanitized.setUsername(sanitizer(user.getUsername()));
            sanitized.setPassword(sanitizer(user.getPassword()));
        }
        else {
            throw new IOException("Invalid Input");
        }
        return sanitized;
    }

    public String sanitizer(String elem) {
        return JsonSanitizer.sanitize(elem).replaceAll("^\"|\"$", "");
    }

    public User validateAndSanitizeUser(UserDto user) throws IOException {
        User sanitized = new User();
        logger.info("validate :: {}", user);
        if(user != null && !user.toString().isBlank()){
            String regex = "^[a-zA-Z0-9-_]+$";
            if(isNotNullOrEmpty(user.getFirstname())){
                sanitized.setFirstName(sanitizer(user.getFirstname()));
            } else {
                throw new IOException("Invalid Parameter: First Name is Missing");
            }
            if(isNotNullOrEmpty(user.getLastname())){
                sanitized.setLastName(sanitizer(user.getLastname()));
            } else {
                throw new IOException("Invalid Parameter: Last Name is Missing");
            }
            if(isNotNullOrEmpty(user.getUsername()) && user.getUsername().matches(regex)){
                sanitized.setUsername(user.getUsername());
            } else {
                throw new IOException("Invalid Parameter: UserName is Missing");
            }
            if(isNotNullOrEmpty(user.getPassword()) && user.getPassword().matches(regex)){
                sanitized.setPassword(sanitizer(user.getPassword()));
            } else {
                throw new IOException("Invalid Parameter: Password is Missing");
            }
            if(isNotNullOrEmpty(user.getEmail())){
                sanitized.setEmail(user.getEmail());
            } else {
                throw new IOException("Invalid Parameter: Email is Missing");
            }

            return sanitized;
        } else {
            throw new IOException("Invalid User input");
        }
    }

    public Question questionValidator(QuestionDto question) throws IOException {
        Question validated = new Question();
        if(question != null && !question.toString().isBlank()){
            boolean hasSpecialChar = question.getQuestion().matches(".*[?.,].*");
            boolean hasAlphanumeric = question.getQuestion().matches(".*\\p{Alnum}.*");
            if(isNotNullOrEmpty(question.getQuestion()) && hasAlphanumeric && hasSpecialChar){
                validated.setQuestion(question.getQuestion());
            } else {
                throw new IOException("Invalid Parameter: Question is Missing");
            }
            if(isNotNullOrEmpty(question.getImage())){
                validated.setImage(question.getImage());
            }
            if(!question.getOptions().isEmpty()){
                validated.setOptions(question.getOptions());
            } else {
                throw new IOException("Invalid Parameter: Options is Missing");
            }
            if(isNotNullOrEmpty(question.getAnswer())){
                validated.setAnswer(question.getAnswer());
            } else {
                throw new IOException("Invalid Parameter: Answer is Missing");
            }
            if(!question.getProficiency().isBlank()){
                validated.setProficiency(question.getProficiency());
            }
        }
        return validated;
    }
}
