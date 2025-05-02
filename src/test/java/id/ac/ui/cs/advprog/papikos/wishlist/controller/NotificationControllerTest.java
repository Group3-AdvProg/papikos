package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.service.WishlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WishlistService wishlistService;

    @Test
    void testGetNotificationsByTenant() throws Exception {
        when(wishlistService.getNotificationsByTenant("tenant123"))
                .thenReturn(List.<Notification>of());

        mockMvc.perform(get("/api/notifications/tenant123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0]").value("Room type Kamar AC is now available!"));
    }

    // Test when no notifications are available for the tenant
    @Test
    void testGetNotificationsByTenant_NoNotifications() throws Exception {
        when(wishlistService.getNotificationsByTenant("tenant123"))
                .thenReturn(List.of());  // Empty list

        mockMvc.perform(get("/api/notifications/tenant123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));  // Expect empty array
    }

    // Test for invalid tenant ID (e.g., tenant does not exist)
    @Test
    void testGetNotificationsByTenant_InvalidTenant() throws Exception {
        when(wishlistService.getNotificationsByTenant("invalidTenant"))
                .thenThrow(new IllegalArgumentException("Tenant not found"));

        mockMvc.perform(get("/api/notifications/invalidTenant"))
                .andExpect(status().isNotFound())  // Expect 404 Not Found
                .andExpect(jsonPath("$.message").value("Tenant not found"));
    }

    // Test when the service throws an exception (simulate server error)
    @Test
    void testGetNotificationsByTenant_ServiceException() throws Exception {
        when(wishlistService.getNotificationsByTenant("tenant123"))
                .thenThrow(new RuntimeException("Internal Server Error"));

        mockMvc.perform(get("/api/notifications/tenant123"))
                .andExpect(status().isInternalServerError())  // Expect 500 Internal Server Error
                .andExpect(jsonPath("$.message").value("Internal Server Error"));
    }
}
