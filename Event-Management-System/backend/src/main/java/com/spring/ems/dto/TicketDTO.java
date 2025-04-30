package com.spring.ems.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDTO {
    private long ticketID;
    private LocalDateTime bookingDate;
    private String status;
    private boolean active;
    private Long eventId;
    
}