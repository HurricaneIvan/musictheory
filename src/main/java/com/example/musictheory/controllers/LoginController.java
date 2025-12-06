package com.example.musictheory.controllers;


import com.example.musictheory.services.UserService;

public class LoginController {

    private UserService userService;

    public LoginController(UserService userService) {
        super();
        this.userService = userService;
    }

    /*
    * Endpoints:
    * 1) POST login(username, pass) returns logged in
    * 2) POST register(username, pass, firstname, lastname, email?)
    * */
}
