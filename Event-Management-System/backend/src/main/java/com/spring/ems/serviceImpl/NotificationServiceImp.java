package com.spring.ems.serviceImpl;

import com.spring.ems.entity.Event;
import com.spring.ems.entity.Notification;
import com.spring.ems.entity.Ticket;
import com.spring.ems.entity.User;
import com.spring.ems.exception.EventNotFoundException;
import com.spring.ems.exception.NotificationNotFoundException;
import com.spring.ems.exception.UserNotFoundException;
import com.spring.ems.repo.EventRepository;
import com.spring.ems.repo.NotificationRepository;
import com.spring.ems.repo.UserRepository;
import com.spring.ems.service.EmailService;
import com.spring.ems.service.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImp implements NotificationService {
	private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImp.class);

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;

	@Override
	public void notifyAllUserAboutNewEvent(Event event) {
		logger.info("Notifying all users about a new event: {}", event.getName());
		List<User> users = userRepository.findAll();
		String message = formatEventDetails(event, "A new event has been created");

		for (User user : users) {
			createNotificationAndSendEmail(user, event, message, "New Event: " + event.getName());
		}

		logger.info("All users have been notified via database and email about the new event: {}", event.getName());
	}

	@Override
	public void notifyAllUsersAboutEventUpdate(Event event) {
		logger.info("Notifying all users about event update: {}", event.getName());
		List<User> users = userRepository.findAll();
		String message = formatEventDetails(event, "An event has been updated");

		for (User user : users) {
			createNotificationAndSendEmail(user, event, message, "Event Update: " + event.getName());
		}

		logger.info("All users have been notified via database and email about the updated event: {}", event.getName());
	}

	// Remanider section
	@Override
	public void sendReminderToTicketHolders(Event event, String message) {
		logger.info("Sending reminder to ticket holders for event: {}", event.getName());
		List<User> ticketHolders = userRepository.findAll(); // Fetching actual ticket holders
		for (User user : ticketHolders) {
			createNotification(user, event, message);
		}
		logger.info("Reminder sent for event: {}", event.getName());
	}

	// Automatic remainder to the Ticket Holders before 60mins
	@Override
	@Scheduled(fixedRate = 60000)
	public void sendAutomaticEventReminders() {
		logger.info("Running scheduled task: Sending event reminders.");
		List<Event> events = eventRepository.findAll();
		LocalDateTime now = LocalDateTime.now();

		for (Event event : events) {
			LocalDateTime eventTime = event.getDate();
			if (eventTime.minusMinutes(60).isBefore(now) && eventTime.minusMinutes(60).isAfter(now.minusMinutes(1))) {
				sendReminderToTicketHolders(event, "Reminder: " + event.getName() + " starts in 1 hour!");
			}
			if (eventTime.minusMinutes(30).isBefore(now) && eventTime.minusMinutes(30).isAfter(now.minusMinutes(1))) {
				sendReminderToTicketHolders(event, "Reminder: " + event.getName() + " starts in 30 minutes!");
			}
		}
		logger.info("Scheduled event reminders sent.");
	}

	// remainder to the user to send the feed back after 10 mins of event start
	@Override
	@Scheduled(fixedRate = 60000)
	public void sendFeedbackReminders() {
		logger.info("Running scheduled task: Sending feedback reminders.");
		List<Event> events = eventRepository.findAll();
		LocalDateTime now = LocalDateTime.now();

		for (Event event : events) {
			if (event.getDate().plusMinutes(10).isBefore(now)
					&& event.getDate().plusMinutes(10).isAfter(now.minusMinutes(1))) {
				sendReminderToTicketHolders(event, "Reminder: Please share your feedback for " + event.getName());
			}
		}
		logger.info("Feedback reminders sent.");
	}

	// Send manual FeedBackEmail to ever User
	@Override
	public void sendManualFeedbackReminder(Long eventId) {
		logger.info("Sending manual feedback reminder for event ID: {}", eventId);
		Event event = eventRepository.findById(eventId).orElseThrow(() -> {
			logger.error("Event not found with ID {}", eventId);
			return new EventNotFoundException("Event not found");
		});

		sendReminderToTicketHolders(event, "Reminder: Please share your feedback for " + event.getName());
	}
//1
	@Override
	public Notification createNotification(User user, Event event, String message) {
		logger.info("Creating notification for user: {} for event: {}", user.getEmail(), event.getName());
		Notification notification = Notification.builder().user(user).event(event).message(message)
				.sentTimestamp(LocalDateTime.now()).isActive(true).build();
		Notification savedNotification = notificationRepository.save(notification);
		logger.info("Notification created successfully for user: {}", user.getEmail());
		return savedNotification;
	}

	@Override
	public List<Notification> getAllNotifications() {
		logger.info("Fetching all active notifications.");
		List<Notification> notifications = notificationRepository.findByIsActiveTrue();
		logger.info("Total active notifications found: {}", notifications.size());
		return notifications;
	}

	@Override
	public List<Notification> getNotificationsByUser(Long userId) {
		logger.info("Fetching notifications for user ID: {}", userId);
		List<Notification> notifications = notificationRepository.findByUserUserIdAndIsActiveTrue(userId);
		logger.info("Total notifications found for user ID {}: {}", userId, notifications.size());
		return notifications;
	}

	@Override
	public void deleteNotification(Long notificationId) {
		logger.info("Attempting to delete notification with ID: {}", notificationId);
		Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> {
			logger.error("Notification not found with ID {}", notificationId);
			return new NotificationNotFoundException("Notification not found");
		});

		notification.setActive(false);
		notificationRepository.save(notification);
		logger.info("Notification with ID {} deleted successfully", notificationId);
	}

	private void createNotificationAndSendEmail(User user, Event event, String message, String subject) {
		logger.info("Creating notification and sending email for user: {} | Event: {}", user.getEmail(),
				event.getName());
		Notification notification = createNotification(user, event, message);
		emailService.sendEmail(user.getEmail(), subject, message);
		logger.info("Email sent successfully to user: {} with subject: {}", user.getEmail(), subject);
	}

	private String formatEventDetails(Event event, String title) {
		return title + ":\n\n" + "📌 Event Name: " + event.getName() + "\n" + "📍 Location: " + event.getLocation()
				+ "\n" + "📅 Date & Time: " + event.getDate() + "\n" + "🎭 Category: " + event.getCategory() + "\n"
				+ "\nCheck the Event Management System for more details.";
	}

	@Override
	public void notifyUserOnTicketBooking(User user, Event event) {
		String subject = "Ticket Booked Successfully for " + event.getName();
		String message = "Hi " + user.getName() + ",\n\n" + "🎫 Your ticket has been successfully booked for:\n\n"
				+ "📌 " + event.getName() + "\n📍 " + event.getLocation() + "\n📅 " + event.getDate() + "\n\n"
				+ "Thanks for using EMS!\n\n-- EMS Team";

		createNotification(user, event, "Your ticket has been booked for " + event.getName());
		emailService.sendEmail(user.getEmail(), subject, message);
	}

	@Override
	public void notifyUserOnTicketCancellation(User user, Event event) {
		String subject = "Ticket Cancelled for " + event.getName();
		String message = "Hi " + user.getName() + ",\n\n" + "❌ Your ticket for the event \"" + event.getName()
				+ "\" has been cancelled.\n\n"
				+ "If this was a mistake, please book again from the EMS portal.\n\n-- EMS Team";

		createNotification(user, event, "Your ticket for " + event.getName() + " has been cancelled.");
		emailService.sendEmail(user.getEmail(), subject, message);
	}

	@Override
	public void notifyUsersAboutEventCancellation(Event event, List<Ticket> bookedTickets) {
		String subject = "Event Cancelled: " + event.getName();

		for (Ticket ticket : bookedTickets) {
			String message = "We regret to inform you that the event '" + event.getName()
					+ "' has been cancelled. Your ticket has been automatically cancelled.";

			// Notify via DB
			createNotification(ticket.getUser(), event, message);

			// Notify via Gmail
			emailService.sendEmail(ticket.getUser().getEmail(), subject, message);
			logger.info("Email sent to user {} regarding event cancellation", ticket.getUser().getEmail());
		}
	}
	
	@Override
    public List<Notification> getNotificationsByUserEmail(String userEmail) {
        logger.info("Fetching notifications by email: {}", userEmail);
        User user = userRepository.findByEmail(userEmail).orElseThrow(() ->
                new UserNotFoundException("User not found"));
        return notificationRepository.findByUserUserIdAndIsActiveTrue(user.getUserId());
    }

    @Override
    public void softDeleteNotification(Long notificationId, String userEmail) {
        logger.info("User {} deleting notification {}", userEmail, notificationId);
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() ->
                new NotificationNotFoundException("Notification not found"));
        if (!notification.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("Unauthorized deletion");
        }
        notification.setActive(false);
        notificationRepository.save(notification);
    }
 @Override
    public List<Notification> getAllNotificationsByUserId(Long userId) {
        logger.info("Admin fetching ALL (active + deleted) notifications for user ID: {}", userId);
        return notificationRepository.findByUserUserId(userId);
    }

}
