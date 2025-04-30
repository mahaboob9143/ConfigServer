package com.spring.ems.service;

import com.spring.ems.entity.User;
import com.spring.ems.exception.UserAlreadyExistsException;
import com.spring.ems.exception.UserNotFoundException;
import com.spring.ems.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface UserService {
    User createUser(User user) throws UserAlreadyExistsException;
    User login(String email, String password) throws UserNotFoundException;
    User updateUser(Long id, User updatedUser) throws UserNotFoundException, UserAlreadyExistsException;
    List<User> getAllUsers();
    User getUserByEmail(String email);
    User getUserById(Long userId) throws UserNotFoundException;
    Long getUserIdByEmail(String email);
	Map<String, Object> getUserAnalytics(Long userId);
	boolean sendOtpToEmail(String email);
	boolean verifyOtp(String email, int otp);
	boolean resetPassword(String email, String newPassword);
}
