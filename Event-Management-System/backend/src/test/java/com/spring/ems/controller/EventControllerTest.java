//package com.spring.ems.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.spring.ems.dto.EventDTO;
//import com.spring.ems.entity.Event;
//import com.spring.ems.entity.User;
//import com.spring.ems.service.EventService;
//import com.spring.ems.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(EventController.class)
//public class EventControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private EventService eventService;
//
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private EventController eventController;
//
//    private ObjectMapper objectMapper;
//
//    private Event sampleEvent;
//    private User adminUser;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
//
//        objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//
//        sampleEvent = Event.builder()
//                .eventId(1L)
//                .name("Test Event")
//                .category("Tech")
//                .location("Auditorium")
//                .date(LocalDateTime.of(2025, 5, 10, 18, 0))
//                .isActive(true)
//                .build();
//
//        adminUser = User.builder()
//                .userId(1L)
//                .name("Admin")
//                .email("admin@example.com")
//                .role("ADMIN")
//                .build();
//    }
//
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    void testCreateEvent_AsAdmin() throws Exception {
//        when(userService.getUserByEmail("admin@example.com")).thenReturn(adminUser);
//        when(eventService.createEvent(any(Event.class))).thenReturn(sampleEvent);
//
//        mockMvc.perform(post("/events-api/events")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sampleEvent)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.name").value("Test Event"));
//
//        verify(eventService, times(1)).createEvent(any(Event.class));
//    }
//
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    void testUpdateEvent_AsAdmin() throws Exception {
//        when(userService.getUserByEmail("admin@example.com")).thenReturn(adminUser);
//        when(eventService.updateEvent(eq(1L), any(Event.class))).thenReturn(sampleEvent);
//
//        mockMvc.perform(put("/events-api/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sampleEvent)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Test Event"));
//    }
//
//    @Test
//    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
//    void testDeleteEvent_AsAdmin() throws Exception {
//        when(userService.getUserByEmail("admin@example.com")).thenReturn(adminUser);
//
//        mockMvc.perform(delete("/events-api/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Event deleted successfully"));
//
//        verify(eventService, times(1)).deleteEvent(1L);
//    }
//
//    @Test
//    void testGetAllEvents() throws Exception {
//        when(eventService.getAllEventsAsDTOs()).thenReturn(Collections.singletonList(
//                new EventDTO(1L, "Test Event", "Tech", "Auditorium", LocalDateTime.now())
//        ));
//
//        mockMvc.perform(get("/events-api/view"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(1));
//    }
//
//    @Test
//    void testSearchEvents() throws Exception {
//        when(eventService.searchEventsByName("Tech")).thenReturn(Collections.singletonList(
//                new EventDTO(1L, "Tech Conference", "Tech", "Auditorium", LocalDateTime.now())
//        ));
//
//        mockMvc.perform(get("/events-api/search")
//                        .param("query", "Tech"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].name").value("Tech Conference"));
//    }
//}
