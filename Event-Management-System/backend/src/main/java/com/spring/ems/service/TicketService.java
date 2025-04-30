package com.spring.ems.service;

import com.spring.ems.dto.TicketDTO;
import com.spring.ems.dto.TicketDetailDTO;
import com.spring.ems.entity.Ticket;
import java.util.List;

public interface TicketService {
    TicketDTO bookTicket(Long eventId, String userEmail);
    String cancelTicket(Long ticketId, String userEmail);
              // Active tickets for user
    //List<Ticket> getCancelledByUserEmail(String userEmail);     // Cancelled tickets for user
    List<Ticket> viewTickets();                                 // Admin - All active tickets
    List<Ticket> viewCancelledTickets();                        // Admin - All cancelled tickets
    List<Ticket> getAllByEventID(Long eventId);                 // Admin - By event
    long countTicketsByEventId(Long eventId);                   // Admin - Count by event
	List<TicketDTO> getTicketsForUser(String email);
	List<TicketDTO> getCancelledDTOsByUserEmail(String email);
	TicketDetailDTO getTicketDetails(Long ticketId);
}
