package com.spring.ems.controller;

import com.spring.ems.entity.Notification;
import com.spring.ems.exception.NotificationNotFoundException;
import com.spring.ems.service.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications-api")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    // ✅ USER: Get active notifications for logged-in user
    @GetMapping("/user")
    public ResponseEntity<List<Notification>> getUserNotifications() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Fetching active notifications for user '{}'", email);

        try {
            List<Notification> notifications = notificationService.getNotificationsByUserEmail(email);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching notifications for user '{}': {}", email, e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ USER: Soft-delete their own notification
    @PutMapping("/soft-delete/{id}")
    public ResponseEntity<String> softDeleteNotification(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User '{}' attempting to soft-delete notification ID {}", email, id);

        try {
            notificationService.softDeleteNotification(id, email);
            return new ResponseEntity<>("Notification deleted successfully", HttpStatus.OK);
        } catch (NotificationNotFoundException e) {
            logger.warn("Notification ID {} not found", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            logger.warn("Unauthorized deletion attempt by '{}'", email);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            logger.error("Unexpected error deleting notification ID {}", id, e);
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ ADMIN: View all active notifications
    @GetMapping("/all")
    public ResponseEntity<List<Notification>> getAllActiveNotifications() {
        logger.info("Admin requested all active notifications");

        try {
            List<Notification> notifications = notificationService.getAllNotifications();
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching all active notifications: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ ADMIN: View ALL (active + deleted) notifications for specific user
    @GetMapping("/admin/user/{userId}")
    public ResponseEntity<List<Notification>> getAllNotificationsByUser(@PathVariable Long userId) {
        logger.info("Admin requested all (active + inactive) notifications for user ID {}", userId);

        try {
            List<Notification> notifications = notificationService.getAllNotificationsByUserId(userId);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching notifications for user ID {}: {}", userId, e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ ADMIN: Soft-delete any notification
    @PutMapping("/admin/delete/{id}")
    public ResponseEntity<String> adminDeleteNotification(@PathVariable Long id) {
        logger.info("Admin attempting to soft-delete notification ID {}", id);

        try {
            notificationService.deleteNotification(id);
            return new ResponseEntity<>("Notification soft-deleted by admin", HttpStatus.OK);
        } catch (NotificationNotFoundException e) {
            logger.warn("Admin delete failed - Notification not found: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error during admin deletion for notification ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
