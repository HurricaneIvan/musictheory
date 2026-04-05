package com.example.musictheory.controllers;

import com.example.musictheory.services.UserService;
import com.example.musictheory.utils.Util;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthUserControllerTest {

    @InjectMocks
    private AuthUserController authUserController;

    @Mock
    private UserService userService;

    @Mock
    private Util util;



}
