package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

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

    private List<String> dummyNotifications;

    @BeforeEach
    void setUp() {
        dummyNotifications = List.of("House 1 available!", "House 2 is ready!");
    }

    @Test
    void testGetTenantNotifications_NotEmpty() throws Exception {
        when(notificationService.getNotificationsByTenant(1L)).thenReturn(dummyNotifications);

        mockMvc.perform(get("/api/notifications/tenant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("House 1 available!"));
    }

    @Test
    void testGetTenantNotifications_Empty() throws Exception {
        when(notificationService.getNotificationsByTenant(2L)).thenReturn(List.of());

        mockMvc.perform(get("/api/notifications/tenant/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetOwnerNotifications_NotEmpty() throws Exception {
        when(notificationService.getNotificationsByOwner(10L)).thenReturn(dummyNotifications);

        mockMvc.perform(get("/api/notifications/owner/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1]").value("House 2 is ready!"));
    }

    @Test
    void testGetOwnerNotifications_Empty() throws Exception {
        when(notificationService.getNotificationsByOwner(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/notifications/owner/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testTriggerHouseNotification() throws Exception {
        doReturn(CompletableFuture.completedFuture(null)).when(notificationService).notifyAvailability(5L);

        mockMvc.perform(post("/api/notifications/house/5/trigger"))
                .andExpect(status().isNoContent());

        verify(notificationService, times(1)).notifyAvailability(5L);
    }
}
