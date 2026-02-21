package com.example.musictheory.controllers;

import com.example.musictheory.dtos.UserDto;
import com.example.musictheory.services.UserService;
import com.example.musictheory.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
@RestController
@RequestMapping("/api/auth")
public class AuthUserController {
    private static final Logger logger = LoggerFactory.getLogger(AuthUserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    Util util;

    @PatchMapping(value = "/update")
    public ResponseEntity<?> update(@RequestBody UserDto userDto){
        try {
            if(util.isNotNullOrEmpty(userDto.getUsername())){
                return new ResponseEntity<>(userService.updateUser(userDto), HttpStatus.OK);

            } else {
                return ResponseEntity.badRequest().body("Invalid Username");
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping(value = "/updateRole")
    public ResponseEntity<?> updateRole(@RequestParam String username, @RequestParam String role){
        try {
            if(util.isNotNullOrEmpty(username)){
                return new ResponseEntity<>(userService.updateUserRole(username, role), HttpStatus.OK);

            } else {
                return ResponseEntity.badRequest().body("Invalid Username");
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/delete")
    public ResponseEntity<?> delete(@RequestBody String username){
        try{
            if(util.isNotNullOrEmpty(username)){
                userService.deleteUser(username);
                return new ResponseEntity<>("User " + username + " Deleted", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }


}
