//package com.spring.ems.controller;
//
//import com.spring.ems.entity.Event;
//import com.spring.ems.entity.Feedback;
//import com.spring.ems.entity.User;
//import com.spring.ems.service.FeedbackService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//class FeedbackControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private FeedbackService feedbackService;
//
//    @InjectMocks
//    private FeedbackController feedbackController;
//
//    private Feedback testFeedback;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(feedbackController).build();
//
//        testFeedback = new Feedback();
//        testFeedback.setMessage("Great event!");
//        testFeedback.setRating(5);
//    }
//
//    @Test
//    void testSubmitFeedbackWithRequestBody() throws Exception {
//        testFeedback.setFeedbackDate(LocalDateTime.now());
//        testFeedback.setEvent(new Event());
//        testFeedback.setUser(new User());
//        testFeedback.setActive(true);
//
//        when(feedbackService.submitFeedback(anyLong(), anyLong(), anyString(), anyInt()))
//                .thenReturn(testFeedback);
//
//        String json = """
//            {
//                "eventId": 1,
//                "userId": 1,
//                "message": "Great event!",
//                "rating": 5
//            }
//        """;
//
//        mockMvc.perform(post("/feedback-api/submit")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Great event!"));
//    }
//
//
//    @Test
//    void testGetFeedbackByEvent() throws Exception {
//        when(feedbackService.getFeedbackByEvent(1L)).thenReturn(List.of(testFeedback));
//
//        mockMvc.perform(get("/feedback-api/event/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(1));
//    }
//
//    @Test
//    void testGetAverageRatingForEvent() throws Exception {
//        when(feedbackService.getAverageRatingForEvent(1L)).thenReturn(Optional.of(4.5));
//
//        mockMvc.perform(get("/feedback-api/event/1/rating"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("4.5"));
//    }
//
//    @Test
//    void testDeleteFeedback() throws Exception {
//        when(feedbackService.deleteFeedback(anyLong())).thenReturn("Feedback deleted successfully");
//
//        mockMvc.perform(delete("/feedback-api/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Feedback deleted successfully"));
//    }
//}
