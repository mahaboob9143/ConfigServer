package com.spring.ems.serviceImpl;

import com.spring.ems.entity.User;
import com.spring.ems.exception.UserAlreadyExistsException;
import com.spring.ems.exception.UserNotFoundException;
import com.spring.ems.repo.FeedbackRepository;
import com.spring.ems.repo.TicketRepository;
import com.spring.ems.repo.UserRepository;
import com.spring.ems.service.EmailService;
import com.spring.ems.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;


@Service
public class UserServiceImp implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
	private Map<String, Integer> otpStorage = new HashMap<>();
	private Random random = new Random();

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired FeedbackRepository feedbackRepository;

	@Override
	public User createUser(User user) {
		logger.info("Attempting to create user: {}", user.getEmail());

		if (userRepository.existsByEmail(user.getEmail())) {
			logger.warn("User creation failed: Email already registered - {}", user.getEmail());
			throw new UserAlreadyExistsException("Email is already registered");
		}
		if (userRepository.existsByContactNumber(user.getContactNumber())) {
			logger.warn("User creation failed: Contact number already registered - {}", user.getContactNumber());
			throw new UserAlreadyExistsException("Contact number is already registered");
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User savedUser = userRepository.save(user);

		logger.debug("User details after creation: {}", savedUser);
		logger.info("User created successfully with ID: {}", savedUser.getUserId());
		return savedUser;
	}
	@Override
	public Long getUserIdByEmail(String email) {
	    User user = userRepository.findByEmail(email)
	        .orElseThrow(() -> new UserNotFoundException("User not found"));
	    return user.getUserId();
	}

	@Override
	public User login(String email, String password) {
		logger.info("User login attempt: {}", email);

		User user = userRepository.findByEmail(email).orElseThrow(() -> {
			logger.error("Login failed: Email not found - {}", email);
			return new UserNotFoundException("Email not found");
		});

		return user;
	}

	@Override
	public User updateUser(Long id, User updatedUser) {
		logger.info("Attempting to update user with ID: {}", id);

		User existingUser = userRepository.findById(id).orElseThrow(() -> {
			logger.error("User update failed: User not found with ID - {}", id);
			return new UserNotFoundException("User not found");
		});

		if (!existingUser.getEmail().equals(updatedUser.getEmail()) &&
				userRepository.existsByEmail(updatedUser.getEmail())) {
			logger.warn("User update failed: Email already registered - {}", updatedUser.getEmail());
			throw new UserAlreadyExistsException("Email is already registered");
		}

		if (!existingUser.getContactNumber().equals(updatedUser.getContactNumber()) &&
				userRepository.existsByContactNumber(updatedUser.getContactNumber())) {
			logger.warn("User update failed: Contact number already registered - {}", updatedUser.getContactNumber());
			throw new UserAlreadyExistsException("Contact number is already registered");
		}

		existingUser.setName(updatedUser.getName());
		existingUser.setEmail(updatedUser.getEmail());
		existingUser.setContactNumber(updatedUser.getContactNumber());
		existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

		User updated = userRepository.save(existingUser);
		logger.info("User updated successfully: {}", updated.getEmail());
		return updated;
	}

	@Override
	public List<User> getAllUsers() {
		logger.info("Fetching all users from database.");
		List<User> users = userRepository.findAll();
		logger.debug("Total users fetched: {}", users.size());
		return users;
	}
	
	public User getUserByEmail(String email) {
	    return userRepository.findByEmail(email)
	            .orElseThrow(() -> new UserNotFoundException("User not found by email"));
	}

	@Override
	public User getUserById(Long userId) {
		logger.info("Fetching user by ID: {}", userId);

		return userRepository.findById(userId).orElseThrow(() -> {
			logger.error("User not found with ID: {}", userId);
			return new UserNotFoundException("User not found");
		});
	}
	
	@Override
	public Map<String, Object> getUserAnalytics(Long userId) {
	    User user = userRepository.findById(userId)
	        .orElseThrow(() -> new UserNotFoundException("User not found"));

	    long totalTickets = ticketRepository.countByUserUserId(userId);
	    long cancelledTickets = ticketRepository.countByUserUserIdAndIsActiveFalse(userId);
	    long eventsParticipated = ticketRepository.countDistinctEventByUserUserId(userId);
	    long feedbacksSubmitted = feedbackRepository.countByUserUserId(userId);

	    Map<String, Object> map = new HashMap<>();
	    map.put("userId", userId);
	    map.put("totalTickets", totalTickets);
	    map.put("cancelledTickets", cancelledTickets);
	    map.put("eventsParticipated", eventsParticipated);
	    map.put("feedbacksSubmitted", feedbacksSubmitted);

	    return map;
	}
	

	

	@Override
	public boolean sendOtpToEmail(String email) {
	    Optional<User> userOpt = userRepository.findByEmail(email);
	    if (userOpt.isEmpty()) return false;

	    int otp = 100000 + random.nextInt(800000);
	    otpStorage.put(email, otp);

	    String subject = "🔐 Your OTP for Password Reset";
	    String message = "Your OTP is: " + otp + "\nPlease do not share this with anyone.";
	    logger.info("Generated OTP for {} is {}", email, otp);

	    
	    return emailService.sendEmailWithStatus(email, subject, message);
	}

	@Override
	public boolean verifyOtp(String email, int otp) {
	    Integer storedOtp = otpStorage.get(email);
	    logger.info("Stored OTP for {} is {} and user entered {}", email, storedOtp, otp);
	    return storedOtp != null && storedOtp.equals(otp);
	}

	@Override
	public boolean resetPassword(String email, String newPassword) {
	    User user = userRepository.findByEmail(email)
	        .orElseThrow(() -> new UserNotFoundException("User not found"));

	    user.setPassword(passwordEncoder.encode(newPassword));
	    userRepository.save(user);
	    otpStorage.remove(email); // Remove OTP after success
	    return true;
	}
}
