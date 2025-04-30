package com.spring.ems.service;

import com.spring.ems.dto.EventDTO;
import com.spring.ems.entity.Event;
import com.spring.ems.entity.User;
import com.spring.ems.exception.EventNotFoundException;
import com.spring.ems.exception.UserNotFoundException;
import com.spring.ems.repo.EventRepository;
import com.spring.ems.repo.UserRepository;
import com.spring.ems.serviceImpl.EventServiceImp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private EventServiceImp eventService;

    private Event testEvent;
    private User testUser;

    @BeforeEach
    void init() {
        testUser = User.builder()
                .userId(1L)
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .contactNumber("1234567890")
                .role("USER")
                .build();

        testEvent = Event.builder()
                .eventId(1L)
                .name("Tech Conference")
                .category("Technology")
                .location("New York")
                .date(LocalDateTime.now().plusDays(1))
                .user(testUser)
                .isActive(true)
                .build();
    }

    @Test
    void testCreateEvent_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        Event created = eventService.createEvent(testEvent);

        assertNotNull(created);
        assertEquals("Tech Conference", created.getName());
        verify(notificationService).notifyAllUserAboutNewEvent(testEvent);
    }

    @Test
    void testCreateEvent_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> eventService.createEvent(testEvent));
    }

    @Test
    void testGetAllEventsAsDTOs() {
        when(eventRepository.findByIsActiveTrue()).thenReturn(List.of(testEvent));
        List<EventDTO> result = eventService.getAllEventsAsDTOs();

        assertEquals(1, result.size());
        assertEquals("Tech Conference", result.get(0).getName());
    }

    @Test
    void testGetEventDTOById_Success() {
        when(eventRepository.findByEventIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testEvent));
        EventDTO dto = eventService.getEventDTOById(1L);

        assertNotNull(dto);
        assertEquals("Tech Conference", dto.getName());
    }

    @Test
    void testGetEventDTOById_NotFound() {
        when(eventRepository.findByEventIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class, () -> eventService.getEventDTOById(1L));
    }
}
