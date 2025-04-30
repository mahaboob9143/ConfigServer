package com.spring.ems.controller;

import com.spring.ems.config.JwtUtility;
import com.spring.ems.dto.LoginRequest;
import com.spring.ems.dto.LoginResponse;
import com.spring.ems.entity.User;
import com.spring.ems.exception.UserAlreadyExistsException;
import com.spring.ems.exception.UserNotFoundException;
import com.spring.ems.service.UserService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user-api")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private AuthenticationManager authenticationManager;

    // ✅ Register new user
    @PostMapping("/user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            logger.info("Registering user: {}", user.getEmail());
            User createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED); // 201
        } catch (UserAlreadyExistsException e) {
            logger.warn("Registration failed: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409
        } catch (Exception e) {
            logger.error("Unexpected error during registration", e);
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<Long> getUserIdByEmail(@PathVariable String email) {
        Long userId = userService.getUserIdByEmail(email);
        return ResponseEntity.ok(userId);
    }

    // ✅ Login & generate JWT with email + role
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for email: {}", loginRequest.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword())
            );
            if (authentication.isAuthenticated()) {
                User user = userService.getUserByEmail(loginRequest.getEmail());
                String token = jwtUtility.generateToken(user.getEmail(), user.getRole());
                logger.info("Login successful for {}", user.getEmail());
                return ResponseEntity.ok(new LoginResponse(token));
            } else {
                logger.warn("Login failed: Unauthorized for {}", loginRequest.getEmail());
                return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
            }
        } catch (BadCredentialsException e) {
            logger.warn("Login failed due to bad credentials: {}", e.getMessage());
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Login failed due to server error", e);
            return new ResponseEntity<>("Server error during login", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ Update own profile
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        try {
            String requesterEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userService.getUserByEmail(requesterEmail);

            if (!currentUser.getUserId().equals(id)) {
                logger.warn("Unauthorized update attempt by {}", currentUser.getEmail());
                return new ResponseEntity<>("You are not authorized to update this user", HttpStatus.FORBIDDEN);
            }

            logger.info("Updating user profile: {}", currentUser.getEmail());
            User updated = userService.updateUser(id, user);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (UserAlreadyExistsException | UserNotFoundException e) {
            logger.warn("Update failed: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("Unexpected error while updating user", e);
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ Admin: View all users
    @GetMapping("/view")
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Admin requested all users");
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    // ✅ Admin: Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            logger.info("Admin fetching user by ID: {}", id);
            User user = userService.getUserById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.warn("User not found: {}", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Unexpected error while fetching user", e);
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{userId}/analytics")
    public ResponseEntity<Map<String, Object>> getUserAnalytics(@PathVariable Long userId) {
        logger.info("Fetching analytics for user ID {}", userId);
        Map<String, Object> analytics = userService.getUserAnalytics(userId);
        return ResponseEntity.ok(analytics);
    }
    
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtpToEmail(@RequestParam String email) {
        boolean result = userService.sendOtpToEmail(email);
        if (result) {
            return ResponseEntity.ok("OTP sent to your registered email.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam int otp) {
        boolean result = userService.verifyOtp(email, otp);
        return result ? ResponseEntity.ok("OTP verified successfully.")
                      : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        boolean result = userService.resetPassword(email, newPassword);
        return result ? ResponseEntity.ok("Password reset successful.")
                      : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password reset failed.");
    }
}
