package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NotificationRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {
        NotificationRestController.class,
        NotificationRestControllerTest.TestConfig.class
})
class NotificationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public id.ac.ui.cs.advprog.papikos.auth.util.JwtUtil jwtUtil() {
            return Mockito.mock(id.ac.ui.cs.advprog.papikos.auth.util.JwtUtil.class);
        }
    }

    private final Long tenantId = 1L;
    private final Long ownerId = 2L;
    private final Long houseId = 3L;

    @Test
    void testGetNotificationsByTenant() throws Exception {
        List<String> notifications = List.of("notif1", "notif2");
        when(notificationService.getNotificationsByTenant(tenantId)).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/tenant/{tenantId}", tenantId))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"notif1\",\"notif2\"]"));

        when(notificationService.getNotificationsByTenant(tenantId)).thenReturn(List.of());
        mockMvc.perform(get("/api/notifications/tenant/{tenantId}", tenantId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetNotificationsByOwner() throws Exception {
        List<String> notifications = List.of("ownerNotif");
        when(notificationService.getNotificationsByOwner(ownerId)).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/owner/{ownerId}", ownerId))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"ownerNotif\"]"));

        when(notificationService.getNotificationsByOwner(ownerId)).thenReturn(List.of());
        mockMvc.perform(get("/api/notifications/owner/{ownerId}", ownerId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testTriggerNotification() throws Exception {
        doNothing().when(notificationService).notifyAvailability(houseId);

        mockMvc.perform(post("/api/notifications/house/{houseId}/trigger", houseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(notificationService, times(1)).notifyAvailability(houseId);
    }
}
