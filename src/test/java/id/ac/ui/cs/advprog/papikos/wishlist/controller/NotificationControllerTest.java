package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    private List<Notification> dummyNotifications;

    @BeforeEach
    void setUp() {
        dummyNotifications = List.of(
                Notification.builder().message("House 1 available!").createdAt(LocalDateTime.now()).build(),
                Notification.builder().message("House 2 is ready!").createdAt(LocalDateTime.now()).build()
        );
    }

    @Test
    void testGetNotificationsByReceiver_NotEmpty() throws Exception {
        when(notificationService.getNotificationsByReceiver(1L)).thenReturn(dummyNotifications);

        mockMvc.perform(get("/api/notifications/receiver/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("House 1 available!"));
    }

    @Test
    void testGetNotificationsByReceiver_Empty() throws Exception {
        when(notificationService.getNotificationsByReceiver(2L)).thenReturn(List.of());

        mockMvc.perform(get("/api/notifications/receiver/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetNotificationsBySender_NotEmpty() throws Exception {
        when(notificationService.getNotificationsBySender(10L)).thenReturn(dummyNotifications);

        mockMvc.perform(get("/api/notifications/sender/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].message").value("House 2 is ready!"));
    }

    @Test
    void testGetNotificationsBySender_Empty() throws Exception {
        when(notificationService.getNotificationsBySender(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/notifications/sender/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testTriggerHouseNotification() throws Exception {
        doReturn(CompletableFuture.completedFuture(null)).when(notificationService).notifyAvailability(5L);

        mockMvc.perform(post("/api/notifications/house/5/trigger"))
                .andExpect(status().isNoContent());

        verify(notificationService, times(1)).notifyAvailability(5L);
    }

    @Test
    void testSendGlobalNotification() throws Exception {
        doNothing().when(notificationService).sendToAllUsers(1L, "Hello everyone!");

        mockMvc.perform(post("/api/notifications/broadcast")
                        .param("senderId", "1")
                        .param("message", "Hello everyone!"))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).sendToAllUsers(1L, "Hello everyone!");
    }
}
