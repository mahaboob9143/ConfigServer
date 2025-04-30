package com.spring.ems.controller;

import com.spring.ems.entity.Ticket;
import com.spring.ems.exception.TicketNotFoundException;
import com.spring.ems.service.TicketService;
import com.spring.ems.dto.TicketDTO;
import com.spring.ems.dto.TicketDetailDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets-api")
public class TicketController {

    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    private TicketService ticketService;
    
   
    
    // ✅ USER: Book Ticket
    @PostMapping("/book")
    public ResponseEntity<?> bookTicket(@RequestParam Long eventId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User '{}' requested to book a ticket for event ID {}", email, eventId);

        try {
            TicketDTO ticketDTO = ticketService.bookTicket(eventId, email); // updated
            logger.info("Ticket successfully booked for user '{}' at event ID {}", email, eventId);
            return new ResponseEntity<>(ticketDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error booking ticket for user '{}' and event ID {}: {}", email, eventId, e.getMessage());
            return new ResponseEntity<>("Ticket booking failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // ✅ USER: Cancel Ticket (only if owned)
    @PutMapping("/cancel/{ticketId}")
    public ResponseEntity<?> cancelTicket(@PathVariable Long ticketId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User '{}' attempting to cancel ticket ID {}", email, ticketId);

        try {
            String result = ticketService.cancelTicket(ticketId, email);
            logger.info("Ticket ID {} cancelled by user '{}'", ticketId, email);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (TicketNotFoundException e) {
            logger.warn("Ticket cancellation failed - not found: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            logger.warn("Unauthorized cancellation attempt by user '{}' for ticket ID {}", email, ticketId);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            logger.error("Unexpected error cancelling ticket ID {}: {}", ticketId, e.getMessage());
            return new ResponseEntity<>("Error cancelling ticket", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ USER: View active tickets
    @GetMapping("/user")
    public ResponseEntity<List<TicketDTO>> viewUserTickets() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User '{}' requested active ticket list", email);

        try {
            List<TicketDTO> tickets = ticketService.getTicketsForUser(email);
            return new ResponseEntity<>(tickets, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching tickets for user '{}': {}", email, e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ USER: View cancelled tickets
    @GetMapping("/user/cancelled")
    public ResponseEntity<List<TicketDTO>> viewUserCancelledTickets() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User '{}' requested cancelled ticket list", email);

        try {
            List<TicketDTO> tickets = ticketService.getCancelledDTOsByUserEmail(email);
            return new ResponseEntity<>(tickets, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching cancelled tickets for user '{}': {}", email, e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ ADMIN: View all active booked tickets
    @GetMapping("/view")
    public ResponseEntity<List<Ticket>> viewAllBookedTickets() {
        logger.info("Admin requested all booked tickets");
        return new ResponseEntity<>(ticketService.viewTickets(), HttpStatus.OK);
    }

    // ✅ ADMIN: View all cancelled tickets
    @GetMapping("/view/cancelled")
    public ResponseEntity<List<Ticket>> viewAllCancelledTickets() {
        logger.info("Admin requested all cancelled tickets");
        return new ResponseEntity<>(ticketService.viewCancelledTickets(), HttpStatus.OK);
    }

    // ✅ ADMIN: View tickets for a specific event
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Ticket>> getTicketsByEvent(@PathVariable Long eventId) {
        logger.info("Admin requested tickets for event ID {}", eventId);
        return new ResponseEntity<>(ticketService.getAllByEventID(eventId), HttpStatus.OK);
    }

    // ✅ ADMIN: Get ticket count for a specific event
    @GetMapping("/event/{eventId}/count")
    public ResponseEntity<Long> getTicketCountByEvent(@PathVariable Long eventId) {
        logger.info("Admin requested ticket count for event ID {}", eventId);
        return new ResponseEntity<>(ticketService.countTicketsByEventId(eventId), HttpStatus.OK);
    }
    @GetMapping("/{ticketId}/details")
    public ResponseEntity<TicketDetailDTO> getTicketDetails(@PathVariable Long ticketId) {
        TicketDetailDTO ticketDetails = ticketService.getTicketDetails(ticketId);
        return ResponseEntity.ok(ticketDetails);
    }
}
