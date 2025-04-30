package com.spring.ems.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {
    private Long eventId;
    private String name;
    private String category;
    private String location;
    private LocalDateTime date;
}
