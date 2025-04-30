package com.spring.ems.controller;

import com.spring.ems.entity.Notification;
import com.spring.ems.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private Notification testNotification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();

        testNotification = new Notification();
        testNotification.setMessage("Test Notification");
    }

    @Test
    void testGetAllNotifications() throws Exception {
        when(notificationService.getAllNotifications()).thenReturn(List.of(testNotification));

        mockMvc.perform(get("/notifications-api/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testGetNotificationsByUser() throws Exception {
        when(notificationService.getNotificationsByUser(1L)).thenReturn(List.of(testNotification));

        mockMvc.perform(get("/notifications-api/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testDeleteNotification() throws Exception {
        doNothing().when(notificationService).deleteNotification(anyLong());

        mockMvc.perform(delete("/notifications-api/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification deleted successfully!"));
    }
}
