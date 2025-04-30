package com.spring.ems.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDetailDTO {
    private Long ticketId;
    private String eventName;
    private String userName;
    private LocalDateTime bookingDate;
    private String status;
}