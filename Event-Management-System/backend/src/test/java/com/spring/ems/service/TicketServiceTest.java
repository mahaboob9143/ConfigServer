//package com.spring.ems.service;
//
//import com.spring.ems.entity.Event;
//import com.spring.ems.entity.Ticket;
//import com.spring.ems.entity.User;
//import com.spring.ems.exception.EventNotFoundException;
//import com.spring.ems.exception.TicketNotFoundException;
//import com.spring.ems.exception.UserNotFoundException;
//import com.spring.ems.repo.EventRepository;
//import com.spring.ems.repo.TicketRepository;
//import com.spring.ems.repo.UserRepository;
//import com.spring.ems.serviceImpl.TicketServiceImp;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//class TicketServiceTest {
//
//    @Mock
//    private TicketRepository ticketRepository;
//
//    @Mock
//    private EventRepository eventRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private NotificationService notificationService;
//
//    @InjectMocks
//    private TicketServiceImp ticketService;
//
//    private Event event;
//    private User user;
//    private Ticket ticket;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//
//        user = User.builder().userId(1L).email("user@example.com").build();
//        event = Event.builder().eventId(1L).name("Spring Fest").isActive(true).build();
//
//        ticket = Ticket.builder()
//                .ticketID(1L)
//                .event(event)
//                .user(user)
//                .bookingDate(LocalDateTime.now())
//                .status("Booked")
//                .isActive(true)
//                .build();
//    }
//
//    @Test
//    void testBookTicket_Success() {
//        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
//
//        Ticket result = ticketService.bookTicket(1L, 1L);
//        assertNotNull(result);
//        assertEquals("Booked", result.getStatus());
//        verify(notificationService).notifyUserOnTicketBooking(any(User.class), any(Event.class));
//    }
////    @Test
////    void testBookTicket_Success() {
////        // Arrange
////        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
////        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
////        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
////
////        // Act
////        Ticket result = ticketService.bookTicket(1L, 1L);
////
////        // Assert
////        assertNotNull(result);
////        assertEquals("Booked", result.getStatus());
////        verify(notificationService).notifyUserOnTicketBooking(any(User.class), any(Event.class));
////    }
//
//    @Test
//    void testBookTicket_EventNotFound() {
//        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
//        assertThrows(EventNotFoundException.class, () -> ticketService.bookTicket(1L, 1L));
//    }
//
//    @Test
//    void testBookTicket_UserNotFound() {
//        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
//        when(userRepository.findById(1L)).thenReturn(Optional.empty());
//        assertThrows(UserNotFoundException.class, () -> ticketService.bookTicket(1L, 1L));
//    }
//
//    @Test
//    void testCancelTicket_Success() {
//        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
//        String response = ticketService.cancelTicket(1L);
//        assertEquals("Cancelled Successfully", response);
//        assertFalse(ticket.isActive());
//        assertEquals("Cancelled", ticket.getStatus());
//    }
//
//    @Test
//    void testCancelTicket_TicketNotFound() {
//        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());
//        assertThrows(TicketNotFoundException.class, () -> ticketService.cancelTicket(1L));
//    }
//
//    @Test
//    void testGetAllByEventID() {
//        when(ticketRepository.findByEventEventIdAndIsActiveTrue(1L)).thenReturn(List.of(ticket));
//        assertEquals(1, ticketService.getAllByEventID(1L).size());
//    }
//
//    @Test
//    void testGetAllByUserID() {
//        when(ticketRepository.findByUserUserIdAndIsActiveTrue(1L)).thenReturn(List.of(ticket));
//        assertEquals(1, ticketService.getAllByUserID(1L).size());
//    }
//
//    @Test
//    void testViewTickets() {
//        when(ticketRepository.findByStatusAndIsActiveTrue("Booked")).thenReturn(List.of(ticket));
//        assertEquals(1, ticketService.viewTickets().size());
//    }
//
//    @Test
//    void testViewCancelledTickets() {
//        ticket.setStatus("Cancelled");
//        ticket.setActive(false);
//        when(ticketRepository.findByStatusAndIsActiveFalse("Cancelled")).thenReturn(List.of(ticket));
//        assertEquals(1, ticketService.viewCancelledTickets().size());
//    }
//
//    @Test
//    void testCountTicketsByEventId() {
//        when(ticketRepository.countByEventEventIdAndIsActiveTrue(1L)).thenReturn(5L);
//        assertEquals(5L, ticketService.countTicketsByEventId(1L));
//    }
//}
