package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.service.WishlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
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
                .thenReturn(List.of("Room type Kamar AC is now available!"));

        mockMvc.perform(get("http://localhost:8080/api/notifications/tenant123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0]").value("Room type Kamar AC is now available!"));
    }

    @Test
    void testGetNotificationsByTenantEmpty() throws Exception {
        when(wishlistService.getNotificationsByTenant("tenant123"))
                .thenReturn(List.of());

        mockMvc.perform(get("http://localhost:8080/api/notifications/tenant123"))
                .andExpect(status().isNoContent());
    }
}
