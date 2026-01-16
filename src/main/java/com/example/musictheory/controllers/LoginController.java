package com.example.musictheory.controllers;


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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/public")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);


    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

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
    public ResponseEntity<User> login(UserDto user){

        try {
            User sanitized = util.validateAndSanitizeUser(user);
            UserDetails userDetail = userService.loadUserByUsername(sanitized.getUsername());
            ResponseCookie jwtCookie = jwtUtil.generateJwtCookie(userDetail.getUsername());

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(userService.findUserByUsername(userDetail.getUsername()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/register")
    public ResponseEntity<User> register(UserDto user){

        try {
            User sanitized = util.validateAndSanitizeUser(user);
            return new ResponseEntity<>(userService.createNewUser(sanitized),HttpStatus.OK);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = jwtUtil.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("You have been signed out");
    }
}
