package com.spring.ems.service;

import com.spring.ems.repo.FeedbackRepository;
import com.spring.ems.entity.Event;
import com.spring.ems.entity.Feedback;
import com.spring.ems.entity.User;
import com.spring.ems.repo.EventRepository;
import com.spring.ems.repo.UserRepository;
import com.spring.ems.serviceImpl.FeedbackServiceImp;
import com.spring.ems.repo.TicketRepository; // ✅ Add TicketRepository

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository; 

    @InjectMocks
    private FeedbackServiceImp feedbackService;

    private Event testEvent;
    private User testUser;
    private Feedback testFeedback;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User(1L, "John Doe", "john@example.com", "password123", "9876543210", "USER");

        testEvent = new Event(1L, "Tech Conference", "Technology", "New York",
                LocalDateTime.of(2025, 5, 20, 10, 0), testUser, true);

        testFeedback = new Feedback(1L, testEvent, testUser, "Great event!", 5, LocalDateTime.now(), true);
    }

    @Test
    void testSubmitFeedback_Success() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(testEvent));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(ticketRepository.existsByUserUserIdAndEventEventIdAndIsActiveTrue(anyLong(), anyLong()))
                .thenReturn(true); 
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(invocation -> {
            Feedback savedFeedback = invocation.getArgument(0);
            savedFeedback.setFeedbackId(1L); 
            return savedFeedback;
        });

        Feedback feedback = feedbackService.submitFeedback(1L, 1L, "Amazing event!", 5);

        assertNotNull(feedback);
        assertEquals("Amazing event!", feedback.getMessage());
        assertNotNull(feedback.getFeedbackId());
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }
}
