//package com.spring.ems.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.spring.ems.entity.Event;
//import com.spring.ems.entity.Ticket;
//import com.spring.ems.entity.User;
//import com.spring.ems.service.TicketService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//class TicketControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private TicketService ticketService;
//
//    @InjectMocks
//    private TicketController ticketController;
//
//    private Ticket ticket;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();
//
//        User user = User.builder().userId(1L).email("test@example.com").build();
//        Event event = Event.builder().eventId(1L).name("Tech Talk").build();
//
//        ticket = Ticket.builder()
//                .ticketID(1L)
//                .user(user)
//                .event(event)
//                .bookingDate(LocalDateTime.now())
//                .status("Booked")
//                .isActive(true)
//                .build();
//    }
//
//    @Test
//    void testBookTicket() throws Exception {
//        when(ticketService.bookTicket(1L, 1L)).thenReturn(ticket);
//
//        mockMvc.perform(post("/tickets-api/book")
//                .param("eventId", "1")
//                .param("userId", "1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("Booked"));
//    }
//
//    @Test
//    void testViewTickets() throws Exception {
//        when(ticketService.viewTickets()).thenReturn(List.of(ticket));
//
//        mockMvc.perform(get("/tickets-api/view"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(1));
//    }
//
//    @Test
//    void testCancelTicket() throws Exception {
//        when(ticketService.cancelTicket(1L)).thenReturn("Cancelled Successfully");
//
//        mockMvc.perform(put("/tickets-api/cancel/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Cancelled Successfully"));
//    }
//
//    @Test
//    void testGetAllByEventID() throws Exception {
//        when(ticketService.getAllByEventID(1L)).thenReturn(List.of(ticket));
//
//        mockMvc.perform(get("/tickets-api/event/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(1));
//    }
//
//    @Test
//    void testGetAllByUserID() throws Exception {
//        when(ticketService.getAllByUserID(1L)).thenReturn(List.of(ticket));
//
//        mockMvc.perform(get("/tickets-api/user/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(1));
//    }
//
//    @Test
//    void testViewCancelledTickets() throws Exception {
//        ticket.setStatus("Cancelled");
//        ticket.setActive(false);
//        when(ticketService.viewCancelledTickets()).thenReturn(List.of(ticket));
//
//        mockMvc.perform(get("/tickets-api/view/cancelled"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(1));
//    }
//}
