package com.example.musictheory.controllers;


import com.example.musictheory.dtos.LoginDto;
import com.example.musictheory.dtos.UserDto;
import com.example.musictheory.models.User;
import com.example.musictheory.services.UserService;
import com.example.musictheory.utils.JWTUtil;
import com.example.musictheory.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/public")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);


    @Autowired
    private UserService userService;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    Util util;

    /*
    * Endpoints:
    * 1) POST login(username, pass) returns logged in
    * 2) POST register(username, pass, firstname, lastname, email?)
    * 3) POST logout()
    * */

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginDto user){

        try {
            LoginDto sanitized = util.sanitizeLogin(user);
            User findUser = userService.findUserByUsername(sanitized.getUsername());
            if(new BCryptPasswordEncoder().matches(sanitized.getPassword(), findUser.getPassword())){
                String token = jwtUtil.generateToken(findUser);
                String cookie = String.valueOf(jwtUtil.generateJwtCookie(findUser));
                logger.info("login cookie :: " + cookie);
                return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token)
                    .body(userService.findUserByUsername(findUser.getUsername()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials");
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials");
        }
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestBody UserDto user){

        try {
            User sanitized = util.validateAndSanitizeUser(user);
            logger.info("Sanitized User :: "+ sanitized);
            return new ResponseEntity<>(userService.createNewUser(sanitized),HttpStatus.OK);

        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<?> logout() {
//        String user = jwtUtil.extractUsernameFromToken(cookie);
        ResponseCookie cookie = jwtUtil.getCleanJwtCookie();
        logger.info(" cookie :: {}" + cookie.toString());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("You have been signed out");
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping(value = "/update")
    public ResponseEntity<?> update(@RequestBody UserDto userDto){
        try {
            logger.info("user " +userDto.getUsername());
            if(util.isNotNullOrEmpty(userDto.getUsername())){
                return new ResponseEntity<>(userService.updateUser(userDto),HttpStatus.OK);

            } else {
                return ResponseEntity.badRequest().body("Invalid Username");
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
//        return ResponseEntity.ok().body("user updated");
    }
}
