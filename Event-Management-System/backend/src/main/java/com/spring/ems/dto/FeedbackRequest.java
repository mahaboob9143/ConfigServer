package com.spring.ems.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class FeedbackRequest {
    @NotNull(message = "Event ID is required")
    private Long eventId;

//    @NotNull(message = "User ID is required")
//    private Long userId;

    @NotBlank(message = "Message cannot be empty")
    @Size(min = 5, max = 500, message = "Message must be between 5 and 500 characters")
    private String message;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private int rating;
}
