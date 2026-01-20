package com.example.musictheory.services;

import com.example.musictheory.exception.UserAlreadyExistsException;
import com.example.musictheory.models.AppUserRole;
import com.example.musictheory.models.Proficiency;
import com.example.musictheory.models.User;
import com.example.musictheory.repositories.UserRepository;
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

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User findUserByUsername(String username) throws FileNotFoundException {
        Optional<User> response = userRepository.findUserByUsername(username);
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
