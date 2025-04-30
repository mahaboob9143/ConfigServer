package com.spring.ems.service;

import com.spring.ems.dto.FeedbackDTO;
import com.spring.ems.dto.FeedbackRequest;
import com.spring.ems.entity.Feedback;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FeedbackService {
    Feedback submitFeedback(Long eventId, Long userId, String message, int rating);
    List<FeedbackDTO> getFeedbackByEvent(Long eventId);
    List<FeedbackDTO> getFeedbackByUser(Long userId);
    String deleteFeedback(Long feedbackId);
    Optional<Double> getAverageRatingForEvent(Long eventId);

    List<FeedbackDTO> getSortedFeedback(Long eventId, String sortBy);
    Map<String, Object> getFeedbackAnalytics(Long eventId);
	void submitFeedback(@Valid FeedbackRequest request, String email);
	
}
