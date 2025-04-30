package com.spring.ems.service;

import com.spring.ems.dto.EventDTO;
import com.spring.ems.entity.Event;

import java.util.List;

public interface EventService {
    Event createEvent(Event event);
    Event updateEvent(Long eventId, Event eventDetails);
    void deleteEvent(Long eventId);
    List<EventDTO> getAllEventsAsDTOs();
    EventDTO getEventDTOById(Long eventId);
    List<EventDTO> getEventsByCategory(String category);
    List<EventDTO> getUpcomingEvents();
    List<EventDTO> getPastEvents();
    List<EventDTO> searchEventsByName(String query);
	List<Event> getAllEventsIncludingInactive();
	EventDTO fetchEventDTOEvenIfInactive(Long eventId);
	
}
