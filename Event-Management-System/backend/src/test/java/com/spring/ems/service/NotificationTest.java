package com.spring.ems.service;

import com.spring.ems.entity.Event;
import com.spring.ems.entity.Notification;
import com.spring.ems.entity.User;
import com.spring.ems.exception.NotificationNotFoundException;
import com.spring.ems.repo.EventRepository;
import com.spring.ems.repo.NotificationRepository;
import com.spring.ems.repo.UserRepository;
import com.spring.ems.serviceImpl.NotificationServiceImp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImp notificationService;

    private Event testEvent;
    private User testUser;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User(1L, "John Doe", "john@example.com", "password123", "9876543210", "USER");

        testEvent = new Event(1L, "Tech Conference", "Technology", "New York",
                LocalDateTime.of(2025, 5, 20, 10, 0), testUser, true);

        testNotification = new Notification(1L, testUser, testEvent, "Test Notification", LocalDateTime.now(), true);
    }

    @Test
    void testCreateNotification_Success() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Notification createdNotification = notificationService.createNotification(testUser, testEvent, "Welcome!");

        assertNotNull(createdNotification);
        assertEquals("Welcome!", createdNotification.getMessage());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testGetAllNotifications() {
        when(notificationRepository.findByIsActiveTrue()).thenReturn(List.of(testNotification));

        List<Notification> notifications = notificationService.getAllNotifications();

        assertFalse(notifications.isEmpty());
        assertEquals(1, notifications.size());
    }

    @Test
    void testGetNotificationsByUser() {
        when(notificationRepository.findByUserUserIdAndIsActiveTrue(1L)).thenReturn(List.of(testNotification));

        List<Notification> notifications = notificationService.getNotificationsByUser(1L);

        assertFalse(notifications.isEmpty());
        assertEquals(1, notifications.size());
    }

    @Test
    void testDeleteNotification_Success() {
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        notificationService.deleteNotification(1L);

        assertFalse(testNotification.isActive());
        verify(notificationRepository, times(1)).save(testNotification);
    }

    @Test
    void testDeleteNotification_NotFound() {
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () -> notificationService.deleteNotification(1L));
    }
}
