package com.spring.ems.serviceImpl;

import com.spring.ems.dto.TicketDTO;
import com.spring.ems.dto.TicketDetailDTO;
import com.spring.ems.entity.Event;
import com.spring.ems.entity.Ticket;
import com.spring.ems.entity.User;
import com.spring.ems.exception.EventNotFoundException;
import com.spring.ems.exception.TicketNotFoundException;
import com.spring.ems.exception.UserNotFoundException;
import com.spring.ems.repo.EventRepository;
import com.spring.ems.repo.TicketRepository;
import com.spring.ems.repo.UserRepository;
import com.spring.ems.service.NotificationService;
import com.spring.ems.service.TicketService;
import com.spring.ems.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketServiceImp implements TicketService {

	private static final Logger logger = LoggerFactory.getLogger(TicketServiceImp.class);

	@Autowired
	private TicketRepository ticketRepository;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;

	@Autowired
	private NotificationService notificationService;

	public TicketServiceImp(TicketRepository ticketRepository,
	                        EventRepository eventRepository,
	                        UserRepository userRepository,
	                        NotificationService notificationService) {
		this.ticketRepository = ticketRepository;
		this.eventRepository = eventRepository;
		this.userRepository = userRepository;
		this.notificationService = notificationService;
	}

	@Override
	@Transactional
	public TicketDTO bookTicket(Long eventId, String userEmail) {
		logger.info("Booking ticket for eventId: {} by user: {}", eventId, userEmail);

		Event event = eventRepository.findById(eventId)
				.filter(Event::isActive)
				.orElseThrow(() -> new EventNotFoundException("Event not found or inactive"));

		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UserNotFoundException("User not found"));

		Ticket ticket = Ticket.builder()
				.event(event)
				.user(user)
				.bookingDate(LocalDateTime.now())
				.status("Booked")
				.isActive(true)
				.build();

		Ticket savedTicket = ticketRepository.save(ticket);
		notificationService.notifyUserOnTicketBooking(user, event);
		logger.info("Ticket booked successfully for user: {}", userEmail);

		return mapToDTO(savedTicket);
	}
	
	private TicketDTO mapToDTO(Ticket ticket) {
	    return TicketDTO.builder()
	            .ticketID(ticket.getTicketID())
	            .bookingDate(ticket.getBookingDate())
	            .status(ticket.getStatus())
	            .active(ticket.isActive())
	            .eventId(ticket.getEvent().getEventId())
	            .build();
	}

	@Override
	@Transactional
	public String cancelTicket(Long ticketId, String userEmail) {
		Ticket ticket = ticketRepository.findById(ticketId)
				.orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

		if (!ticket.getUser().getEmail().equals(userEmail)) {
			logger.warn("Unauthorized ticket cancellation by {}", userEmail);
			throw new SecurityException("You are not authorized to cancel this ticket");
		}

		ticket.setActive(false);
		ticket.setStatus("Cancelled");
		ticketRepository.save(ticket);

		notificationService.notifyUserOnTicketCancellation(ticket.getUser(), ticket.getEvent());
		logger.info("Ticket cancelled successfully for user: {}", userEmail);
		return "Cancelled Successfully";
	}

	@Override
	public List<TicketDTO> getTicketsForUser(String email) {
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new UserNotFoundException("User not found"));

	    List<Ticket> tickets = ticketRepository.findByUserUserIdAndIsActiveTrue(user.getUserId());

	    return tickets.stream().map(ticket -> TicketDTO.builder()
	            .ticketID(ticket.getTicketID())
	            .bookingDate(ticket.getBookingDate())
	            .status(ticket.getStatus())
	            .active(ticket.isActive())
	            .eventId(ticket.getEvent().getEventId()) // ✅ Make sure this is mapped
	            .build()
	    ).collect(Collectors.toList());
	}

	@Override
	public List<TicketDTO> getCancelledDTOsByUserEmail(String email) {
	    Long userId = userService.getUserIdByEmail(email); 
	    List<Ticket> cancelledTickets = ticketRepository.findByUserUserIdAndStatus(userId, "CANCELLED");

	    return cancelledTickets.stream()
	        .map(ticket -> TicketDTO.builder()
	            .ticketID(ticket.getTicketID())
	            .bookingDate(ticket.getBookingDate())
	            .status(ticket.getStatus())
	            .active(ticket.isActive())
	            .eventId(ticket.getEvent().getEventId())
	            .build()
	        ).collect(Collectors.toList());
	}


	@Override
	public List<Ticket> viewTickets() {
		logger.info("Fetching all booked tickets (Admin)");
		return ticketRepository.findByStatusAndIsActiveTrue("Booked");
	}

	@Override
	public List<Ticket> viewCancelledTickets() {
		logger.info("Fetching all cancelled tickets (Admin)");
		return ticketRepository.findByStatusAndIsActiveFalse("Cancelled");
	}

	@Override
	public List<Ticket> getAllByEventID(Long eventId) {
		logger.info("Fetching tickets by eventId: {}", eventId);
		return ticketRepository.findByEventEventIdAndIsActiveTrue(eventId);
	}

	@Override
	public long countTicketsByEventId(Long eventId) {
		logger.info("Counting tickets by eventId: {}", eventId);
		return ticketRepository.countByEventEventIdAndIsActiveTrue(eventId);
	}

	@Override
	public TicketDetailDTO getTicketDetails(Long ticketId) {
	    Ticket ticket = ticketRepository.findById(ticketId)
	            .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

	    return TicketDetailDTO.builder()
	            .ticketId(ticket.getTicketID())
	            .eventName(ticket.getEvent().getName())
	            .userName(ticket.getUser().getName())
	            .bookingDate(ticket.getBookingDate())
	            .status(ticket.getStatus())
	            .build();
	}
}
