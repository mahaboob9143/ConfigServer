package com.spring.ems.controller;

import com.spring.ems.dto.EventDTO;
import com.spring.ems.entity.Event;
import com.spring.ems.entity.User;
import com.spring.ems.exception.UserNotFoundException;
import com.spring.ems.service.EventService;
import com.spring.ems.service.UserService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events-api")
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    // ✅ ADMIN: Create event (Only if role=ADMIN in token)
    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@Valid @RequestBody Event event) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email);

            if (!user.getRole().equalsIgnoreCase("ADMIN")) {
                logger.warn("Unauthorized event creation attempt by user: {}", user.getEmail());
                return new ResponseEntity<>("Only ADMIN can create events", HttpStatus.FORBIDDEN);
            }

            event.setUser(user);  // Attach creator admin to event
            logger.info("Creating event by admin: {}", user.getEmail());
            Event saved = eventService.createEvent(event);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);

        } catch (UserNotFoundException e) {
            logger.error("User not found for event creation: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error during event creation", e);
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ ADMIN: Update event
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @Valid @RequestBody Event updatedEvent) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email);

            if (!user.getRole().equalsIgnoreCase("ADMIN")) {
                logger.warn("Unauthorized event update attempt by user: {}", user.getEmail());
                return new ResponseEntity<>("Only ADMIN can update events", HttpStatus.FORBIDDEN);
            }

            logger.info("Admin updating event with ID: {}", id);
            Event event = eventService.updateEvent(id, updatedEvent);
            return new ResponseEntity<>(event, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error updating event", e);
            return new ResponseEntity<>("Error updating event", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ ADMIN: Delete (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email);

            if (!user.getRole().equalsIgnoreCase("ADMIN")) {
                logger.warn("Unauthorized delete attempt by {}", email);
                return new ResponseEntity<>("Only ADMIN can delete events", HttpStatus.FORBIDDEN);
            }

            eventService.deleteEvent(id);
            logger.info("Event with ID {} soft-deleted by admin", id);
            return new ResponseEntity<>("Event deleted successfully", HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error deleting event", e);
            return new ResponseEntity<>("Error deleting event", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ PUBLIC (USER + ADMIN): View all active events
    @GetMapping("/view")
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        logger.info("Fetching all active events");
        return new ResponseEntity<>(eventService.getAllEventsAsDTOs(), HttpStatus.OK);
    }
    @GetMapping("/internal/{eventId}")
    public ResponseEntity<EventDTO> getEventDTOEvenIfInactive(@PathVariable Long eventId) {
        EventDTO eventDTO = eventService.fetchEventDTOEvenIfInactive(eventId);
        return ResponseEntity.ok(eventDTO);
    }


    // ✅ PUBLIC (USER + ADMIN): View by ID
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        logger.info("Fetching event by ID: {}", id);
        return new ResponseEntity<>(eventService.getEventDTOById(id), HttpStatus.OK);
    }

    // ✅ PUBLIC (USER + ADMIN): Category filter
    @GetMapping("/category/{category}")
    public ResponseEntity<List<EventDTO>> getByCategory(@PathVariable String category) {
        logger.info("Fetching events by category: {}", category);
        return new ResponseEntity<>(eventService.getEventsByCategory(category), HttpStatus.OK);
    }

    // ✅ PUBLIC (USER + ADMIN): Upcoming events
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventDTO>> getUpcomingEvents() {
        logger.info("Fetching upcoming events");
        return new ResponseEntity<>(eventService.getUpcomingEvents(), HttpStatus.OK);
    }

    // ✅ PUBLIC (USER + ADMIN): Past events
    @GetMapping("/past")
    public ResponseEntity<List<EventDTO>> getPastEvents() {
        logger.info("Fetching past events");
        return new ResponseEntity<>(eventService.getPastEvents(), HttpStatus.OK);
    }

    // ✅ PUBLIC (USER + ADMIN): Search events
    @GetMapping("/search")
    public ResponseEntity<List<EventDTO>> searchEvents(@RequestParam String query) {
        logger.info("Searching events with keyword: {}", query);
        return new ResponseEntity<>(eventService.searchEventsByName(query), HttpStatus.OK);
    }
    @GetMapping("/view/all")
    public List<Event> getAllEventsIncludingInactive() {
        return eventService.getAllEventsIncludingInactive();
    }
}
