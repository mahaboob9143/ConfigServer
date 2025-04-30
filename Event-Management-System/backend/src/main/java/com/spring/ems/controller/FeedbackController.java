package com.spring.ems.controller;

import com.spring.ems.dto.FeedbackRequest;
import com.spring.ems.dto.FeedbackDTO;
import com.spring.ems.entity.Feedback;
import com.spring.ems.exception.DuplicateFeedbackException;
import com.spring.ems.service.FeedbackService;
import com.spring.ems.service.NotificationService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/feedback-api")
public class FeedbackController {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

	@Autowired
	private FeedbackService feedbackService;

	@Autowired
	private NotificationService notificationService;

	// ✅ Submit Feedback (User)
	@PostMapping("/submit")
	public ResponseEntity<?> submitFeedback(@RequestBody @Valid FeedbackRequest request) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		logger.info("Received feedback submission from user '{}'", email);

		try {
			feedbackService.submitFeedback(request, email);
			return ResponseEntity.ok("Feedback submitted successfully.");
		} catch (DuplicateFeedbackException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("You have already submitted feedback for this event.");
		} catch (Exception e) {
			logger.error("Error while submitting feedback: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Feedback submission failed.");
		}
	}

	// ✅ Get all feedback for event
	@GetMapping("/event/{eventId}")
	public ResponseEntity<List<FeedbackDTO>> getFeedbackByEvent(@PathVariable Long eventId) {
		logger.info("Fetching feedback for event ID {}", eventId);
		return ResponseEntity.ok(feedbackService.getFeedbackByEvent(eventId));
	}

	// ✅ Sort feedback (latest/highest/lowest)
	@GetMapping("/event/{eventId}/sort")
	public ResponseEntity<List<FeedbackDTO>> getSortedFeedback(@PathVariable Long eventId, @RequestParam String by) {
		logger.info("Sorting feedback for event ID {} by {}", eventId, by);
		return ResponseEntity.ok(feedbackService.getSortedFeedback(eventId, by));
	}

	// ✅ Get average rating
	@GetMapping("/event/{eventId}/rating")
	public ResponseEntity<Optional<Double>> getAverageRatingForEvent(@PathVariable Long eventId) {
		logger.info("Calculating average rating for event ID {}", eventId);
		return ResponseEntity.ok(feedbackService.getAverageRatingForEvent(eventId));
	}

	// ✅ Get full feedback analytics (admin)
	@GetMapping("/event/{eventId}/analytics")
	public ResponseEntity<Map<String, Object>> getFeedbackAnalytics(@PathVariable Long eventId) {
		logger.info("Fetching analytics for feedback of event ID {}", eventId);
		return ResponseEntity.ok(feedbackService.getFeedbackAnalytics(eventId));
	}

	// ✅ Get feedback by user
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<FeedbackDTO>> getFeedbackByUser(@PathVariable Long userId) {
		logger.info("Fetching feedback submitted by user ID {}", userId);
		return ResponseEntity.ok(feedbackService.getFeedbackByUser(userId));
	}

	// ✅ Soft-delete feedback
	@DeleteMapping("/{feedbackId}")
	public ResponseEntity<String> deleteFeedback(@PathVariable Long feedbackId) {
		logger.info("Deleting feedback with ID {}", feedbackId);
		return ResponseEntity.ok(feedbackService.deleteFeedback(feedbackId));
	}

	// ✅ Trigger manual reminder for feedback
	@GetMapping("/event/{eventId}/reminder")
	public ResponseEntity<String> sendManualReminder(@PathVariable Long eventId) {
		logger.info("Sending manual feedback reminder for event ID {}", eventId);
		notificationService.sendManualFeedbackReminder(eventId);
		return ResponseEntity.ok("Manual reminder sent.");
	}
}
