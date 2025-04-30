package com.spring.ems.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDTO {
    private Long feedbackId;
    private String userName;
    private String eventName;
    private String message;
    private int rating;
    private LocalDateTime feedbackDate;
}
