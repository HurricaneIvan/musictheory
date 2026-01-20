package com.example.musictheory.services;

import com.example.musictheory.dtos.UserDto;
import com.example.musictheory.exception.UserAlreadyExistsException;
import com.example.musictheory.models.AppUserRole;
import com.example.musictheory.models.Proficiency;
import com.example.musictheory.models.User;
import com.example.musictheory.repositories.UserRepository;
import com.example.musictheory.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.*;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    Util util;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User findUserByUsername(String username) throws FileNotFoundException {
        Optional<User> response = userRepository.findUserByUsername(username);
        logger.info("User " + response);
        if (response.isEmpty()){
            logger.info("User not Found");
            throw new FileNotFoundException("User not Found. Try Again");
        }

        return response.get();
    }

    public User createNewUser(User user) {
        Optional<User> response = userRepository.findUserByUsername(user.getUsername());
        if (response.isPresent()){
            logger.info("Username already exists");
            throw new UserAlreadyExistsException("Username already exists. Retry with new username");
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setProficiency(Proficiency.BEGINNER.getProficiency());
            List<Integer> score = new ArrayList<>();
            score.add(0);
            user.setScore(score);
            user.setRole(AppUserRole.USER.name());

            return userRepository.save(user);
        }
    }

    public User updateUser(UserDto user) throws FileNotFoundException {
        Optional<User> response = userRepository.findUserByUsername(user.getUsername());
        logger.info("Response " + response);
        if(response.isEmpty()){
            logger.info("Username does not exist");
            throw new FileNotFoundException("Username does not exist. Retry with existing username");
        } else {
            if(util.isNotNullOrEmpty(user.getFirstname())){
                response.get().setFirstName(util.sanitizer(user.getFirstname()));
            }
            if(util.isNotNullOrEmpty(user.getLastname())){
                response.get().setLastName(util.sanitizer(user.getLastname()));
            }
            if(util.isNotNullOrEmpty(user.getPassword())){
                response.get().setPassword(util.sanitizer(user.getPassword()));
            }
            if(util.isNotNullOrEmpty(user.getEmail())){
                response.get().setEmail(util.sanitizer(user.getEmail()));
            }
        }
        logger.info("updated response :: " + response.get() );
        return userRepository.save(response.get());
    }

//    @Secured("ADMIN") // only allows admin to call this service.
//    public void deleteUser(String username){};
//
////    Example:
///     @Secured("ROLE_ADMIN")
////    public void deleteUser(String userId) {
////        // ... only users with ROLE_ADMIN can call this ...
////    }
////
////    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
////    public User getUserDetails(String username) {
////        // ... users with either role can call this ...
////    }


}
