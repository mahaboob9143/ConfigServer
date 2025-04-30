package com.spring.ems.serviceImpl;

import com.spring.ems.dto.EventDTO;
import com.spring.ems.entity.Event;
import com.spring.ems.entity.Ticket;
import com.spring.ems.entity.User;
import com.spring.ems.exception.EventNotFoundException;
import com.spring.ems.exception.UserNotFoundException;
import com.spring.ems.repo.EventRepository;
import com.spring.ems.repo.TicketRepository;
import com.spring.ems.repo.UserRepository;
import com.spring.ems.service.EmailService;
import com.spring.ems.service.EventService;
import com.spring.ems.service.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImp implements EventService {
	private static final Logger logger = LoggerFactory.getLogger(EventServiceImp.class);

	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private EmailService emailService;

	@Override
	public Event createEvent(Event event) {
		if (event.getDate().isBefore(LocalDateTime.now())) {
			logger.warn("Attempted to create event with past date: {}", event.getDate());
			throw new IllegalArgumentException("Event date must not be in the past");
		}

		User user = userRepository.findById(event.getUser().getUserId()).orElseThrow(() -> {
			logger.error("User not found with ID: {}", event.getUser().getUserId());
			return new UserNotFoundException("User not found");
		});

		event.setUser(user);
		event.setActive(true);

		Event savedEvent = eventRepository.save(event);
		notificationService.notifyAllUserAboutNewEvent(savedEvent);
		logger.info("Event created successfully: {}", savedEvent.getName());
		return savedEvent;
	}

	@Override
	public Event updateEvent(Long eventId, Event eventDetails) {
		if (eventDetails.getDate().isBefore(LocalDateTime.now())) {
			logger.warn("Attempted to update event to a past date: {}", eventDetails.getDate());
			throw new IllegalArgumentException("Event date must not be in the past");
		}

		Event existingEvent = eventRepository.findById(eventId).orElseThrow(() -> {
			logger.error("Event not found with ID: {}", eventId);
			return new EventNotFoundException("Event not found");
		});

		existingEvent.setName(eventDetails.getName());
		existingEvent.setCategory(eventDetails.getCategory());
		existingEvent.setLocation(eventDetails.getLocation());
		existingEvent.setDate(eventDetails.getDate());

		Event updated = eventRepository.save(existingEvent);
		notificationService.notifyAllUsersAboutEventUpdate(updated);
		logger.info("Event updated: {}", updated.getName());
		return updated;
	}
	@Override
	public void deleteEvent(Long eventId) {
	    Event event = eventRepository.findById(eventId)
	            .orElseThrow(() -> new EventNotFoundException("Event not found"));

	    event.setActive(false);
	    eventRepository.save(event);
	    logger.info("Event soft-deleted: {}", eventId);

	    List<Ticket> bookedTickets = ticketRepository.findByEventEventIdAndIsActiveTrue(eventId);

	    for (Ticket ticket : bookedTickets) {
	        ticket.setActive(false);
	        ticket.setStatus("Cancelled");
	        ticketRepository.save(ticket);
	    }

	    notificationService.notifyUsersAboutEventCancellation(event, bookedTickets);
	    logger.info("All tickets cancelled and users notified via DB and email for event: {}", event.getName());
	}

	

	@Override
	public List<EventDTO> getAllEventsAsDTOs() {
		return eventRepository.findByIsActiveTrue().stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	@Override
	public EventDTO getEventDTOById(Long eventId) {
		Event event = eventRepository.findByEventIdAndIsActiveTrue(eventId)
				.orElseThrow(() -> new EventNotFoundException("Event not found"));
		return convertToDTO(event);
	}

	@Override
	public List<EventDTO> getEventsByCategory(String category) {
		return eventRepository.findByCategoryAndIsActiveTrue(category).stream().map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<EventDTO> getUpcomingEvents() {
		return eventRepository.findByDateAfterAndIsActiveTrue(LocalDateTime.now()).stream().map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<EventDTO> getPastEvents() {
		return eventRepository.findByDateBeforeAndIsActiveTrue(LocalDateTime.now()).stream().map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<EventDTO> searchEventsByName(String query) {
		return eventRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(query).stream().map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	private EventDTO convertToDTO(Event event) {
		return EventDTO.builder().eventId(event.getEventId()).name(event.getName()).category(event.getCategory())
				.location(event.getLocation()).date(event.getDate()).build();
	}

	@Override
	public List<Event> getAllEventsIncludingInactive() {
		// TODO Auto-generated method stub
		return eventRepository.findAll();
	}
	@Override
	public EventDTO fetchEventDTOEvenIfInactive(Long eventId) {
	    Event event = eventRepository.findById(eventId)
	            .orElseThrow(() -> new EventNotFoundException("Event not found (even inactive)"));

	    return convertToDTO(event); // ✅ convert before returning
	}


}
