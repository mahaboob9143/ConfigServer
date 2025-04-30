package com.spring.ems.service;

import com.spring.ems.entity.Event;
import com.spring.ems.entity.Notification;
import com.spring.ems.entity.User;
import com.spring.ems.exception.EventNotFoundException;
import com.spring.ems.exception.NotificationNotFoundException;
import com.spring.ems.exception.UserNotFoundException;
import com.spring.ems.repo.EventRepository;
import com.spring.ems.repo.NotificationRepository;
import com.spring.ems.repo.UserRepository;
import com.spring.ems.entity.Ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationService {

	void notifyAllUserAboutNewEvent(Event event);

	void notifyAllUsersAboutEventUpdate(Event event);

	void sendReminderToTicketHolders(Event event, String message);

	void sendAutomaticEventReminders();

	void sendFeedbackReminders();

	void sendManualFeedbackReminder(Long eventId) throws EventNotFoundException;

	Notification createNotification(User user, Event event, String message);

	List<Notification> getAllNotifications();

	List<Notification> getNotificationsByUser(Long userId);

	void deleteNotification(Long notificationId) throws NotificationNotFoundException;

	void notifyUserOnTicketCancellation(User user, Event event);

	void notifyUserOnTicketBooking(User user, Event event);

	void notifyUsersAboutEventCancellation(Event event, List<Ticket> bookedTickets);

	List<Notification> getNotificationsByUserEmail(String userEmail); // For logged-in USER

	void softDeleteNotification(Long notificationId, String userEmail); // By USER

	List<Notification> getAllNotificationsByUserId(Long userId); // ADMIN - all

}
