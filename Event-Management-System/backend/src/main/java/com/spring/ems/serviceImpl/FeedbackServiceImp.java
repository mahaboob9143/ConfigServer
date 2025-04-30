package com.spring.ems.serviceImpl;

import com.spring.ems.dto.FeedbackDTO;
import com.spring.ems.dto.FeedbackRequest;
import com.spring.ems.entity.*;
import com.spring.ems.exception.*;
import com.spring.ems.repo.*;
import com.spring.ems.service.FeedbackService;
import com.spring.ems.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImp implements FeedbackService {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackServiceImp.class);

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private NotificationService notificationService;
    
    @Override
    @Transactional
    public void submitFeedback(FeedbackRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        boolean alreadyGiven = feedbackRepository
                .existsByUserUserIdAndEventEventIdAndIsActiveTrue(user.getUserId(), event.getEventId());

        if (alreadyGiven) {
            throw new DuplicateFeedbackException("Feedback already submitted for this event.");
        }

        Feedback feedback = Feedback.builder()
                .user(user)
                .event(event)
                .message(request.getMessage())
                .rating(request.getRating())
                .feedbackDate(LocalDateTime.now())
                .isActive(true)
                .build();

        feedbackRepository.save(feedback);

        // Notify others if needed
        
    }

    @Override
    @Transactional
    public Feedback submitFeedback(Long eventId, Long userId, String message, int rating) {
        logger.info("Submitting feedback for event ID {} by user ID {}", eventId, userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!ticketRepository.existsByUserUserIdAndEventEventIdAndIsActiveTrue(userId, eventId)) {
            logger.warn("Feedback submission blocked - user did not attend event.");
            throw new RuntimeException("User has not booked a ticket for this event.");
        }

        Feedback feedback = Feedback.builder()
                .event(event)
                .user(user)
                .message(message)
                .rating(rating)
                .feedbackDate(LocalDateTime.now())
                .isActive(true)
                .build();

        Feedback saved = feedbackRepository.save(feedback);
        logger.info("Feedback submitted successfully");

        List<Ticket> ticketHolders = ticketRepository.findByEventEventIdAndIsActiveTrue(eventId);
        for (Ticket ticket : ticketHolders) {
            notificationService.createNotification(ticket.getUser(), event,
                    "New feedback added for " + event.getName() + ": " + message);
        }

        return saved;
    }

    @Override
    public List<FeedbackDTO> getFeedbackByEvent(Long eventId) {
        logger.info("Fetching all feedback for event ID {}", eventId);
        return feedbackRepository.findByEventEventIdAndIsActiveTrue(eventId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<FeedbackDTO> getFeedbackByUser(Long userId) {
        logger.info("Fetching all feedback submitted by user ID {}", userId);
        return feedbackRepository.findByUserUserIdAndIsActiveTrue(userId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<FeedbackDTO> getSortedFeedback(Long eventId, String sortBy) {
        List<Feedback> feedbackList;

        switch (sortBy.toLowerCase()) {
            case "latest" -> feedbackList = feedbackRepository.findByEventEventIdAndIsActiveTrueOrderByFeedbackDateDesc(eventId);
            case "highest" -> feedbackList = feedbackRepository.findByEventEventIdAndIsActiveTrueOrderByRatingDesc(eventId);
            case "lowest" -> feedbackList = feedbackRepository.findByEventEventIdAndIsActiveTrueOrderByRatingAsc(eventId);
            default -> throw new IllegalArgumentException("Invalid sort type. Use 'latest', 'highest', or 'lowest'.");
        }

        logger.info("Returning sorted feedback list for event {} sorted by {}", eventId, sortBy);
        return feedbackList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getFeedbackAnalytics(Long eventId) {
        long count = feedbackRepository.countByEventId(eventId);
        double average = feedbackRepository.findAverageRatingByEventId(eventId).orElse(0.0);

        Map<String, Object> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("totalFeedback", count);
        map.put("averageRating", average);
        logger.info("Feedback analytics fetched for event {}: total={}, avg={}", eventId, count, average);
        return map;
    }

    @Override
    @Transactional
    public String deleteFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new FeedbackNotFoundException("Feedback not found"));
        feedback.setActive(false);
        feedbackRepository.save(feedback);
        logger.info("Feedback with ID {} soft deleted", feedbackId);
        return "Feedback deleted successfully";
    }

    @Override
    public Optional<Double> getAverageRatingForEvent(Long eventId) {
        logger.info("Calculating average rating for event ID {}", eventId);
        return feedbackRepository.findAverageRatingByEventId(eventId);
    }

    private FeedbackDTO convertToDTO(Feedback f) {
        return FeedbackDTO.builder()
                .feedbackId(f.getFeedbackId())
                .userName(f.getUser().getName())
                .eventName(f.getEvent().getName())
                .message(f.getMessage())
                .rating(f.getRating())
                .feedbackDate(f.getFeedbackDate())
                .build();
    }
}
