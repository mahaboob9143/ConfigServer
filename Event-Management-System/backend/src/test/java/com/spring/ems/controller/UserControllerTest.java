package com.spring.ems.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.ems.config.JwtUtility;
import com.spring.ems.dto.LoginRequest;
import com.spring.ems.dto.LoginResponse;
import com.spring.ems.entity.User;
import com.spring.ems.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(UserControllerTest.class);

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtility jwtUtility;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        testUser = new User(1L, "John Doe", "john@example.com", "password123", "9876543210", "USER");
        logger.info("Setup complete with test user: {}", testUser.getEmail());
    }

    @Test
    void testRegisterUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        logger.info("Testing user registration...");

        mockMvc.perform(post("/user-api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"));

        logger.info("User registration test passed");
    }

    


    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(testUser));

        logger.info("Testing fetch all users...");

        mockMvc.perform(get("/user-api/view"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        logger.info("Fetch all users test passed");
    }

    @Test
    void testGetUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);

        logger.info("Testing fetch user by ID...");

        mockMvc.perform(get("/user-api/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));

        logger.info("Fetch user by ID test passed");
    }
}
