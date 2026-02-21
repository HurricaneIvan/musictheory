package com.example.musictheory.controllers;

import com.example.musictheory.config.SecurityConfig;
import com.example.musictheory.dtos.LoginDto;
import com.example.musictheory.dtos.UserDto;
import com.example.musictheory.models.User;
import com.example.musictheory.services.UserService;
import com.example.musictheory.utils.JWTUtil;
import com.example.musictheory.utils.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Import({ SecurityConfig.class })
public class LoginControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private LoginController loginController;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private Util util;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testLogin_sunny_day() throws Exception {
        LoginDto validUserObj = new LoginDto("testUser3",  "testUser0", "string@gajdjfs.com");
        User user = new User("", "", "testUser3", new BCryptPasswordEncoder().encode("testUser0"), "", "USER", "BEGINNER", Collections.singletonList(0));

        when(util.sanitizeLogin(Mockito.any())).thenReturn(validUserObj);
        when(userService.findUserByUsername(Mockito.any())).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserObj)))
                .andExpect(status().isOk()).andReturn();
    }
    @Test
    public void testLogin_invalid() throws Exception {
        LoginDto validUserObj = new LoginDto("testUser3",  "testUser3", "string@gajdjfs.com");

        when(util.sanitizeLogin(Mockito.any())).thenReturn(validUserObj);
        when(userService.findUserByUsername(Mockito.any())).thenThrow(new FileNotFoundException(""));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserObj)))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void testRegister_sunny_day() throws Exception {
        UserDto validUserObj = new UserDto("first","last", "testUser3",  "testUser3", "string@gajdjfs.com");
        User validated = mock(User.class);
        when(util.validateAndSanitizeUser(Mockito.any())).thenReturn(validated);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/public/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validUserObj)))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    public void testRegister_bad_request() throws Exception {
        UserDto validUserObj = new UserDto("first","last", "testUser3#",  "testUser3", "string@gajdjfs.com");
        when(util.validateAndSanitizeUser(Mockito.any())).thenThrow(new IOException(""));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/public/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserObj)))
                .andExpect(status().isBadRequest()).andReturn();
    }

}
