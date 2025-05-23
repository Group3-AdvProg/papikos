package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.auth.filter.JwtFilter;
import id.ac.ui.cs.advprog.papikos.auth.util.JwtUtil;
import id.ac.ui.cs.advprog.papikos.wishlist.DTO.WishlistRequest;
import id.ac.ui.cs.advprog.papikos.wishlist.service.WishlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WishlistService wishlistService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddToWishlist() throws Exception {
        mockMvc.perform(post("/api/wishlist/add")
                        .param("tenantId", "123")
                        .param("houseId", "101"))
                .andExpect(status().isOk());

        verify(wishlistService).addToWishlist(123L, 101L);
    }

    @Test
    void testRemoveFromWishlist() throws Exception {
        mockMvc.perform(delete("/api/wishlist/remove")
                        .param("tenantId", "123")
                        .param("houseId", "101"))
                .andExpect(status().isOk());

        verify(wishlistService).removeFromWishlist(123L, 101L);
    }

    @Test
    void testGetWishlistByTenant() throws Exception {
        when(wishlistService.getWishlistByTenant(123L)).thenReturn(List.of(101L, 102L));

        mockMvc.perform(get("/api/wishlist/tenant/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value(101))
                .andExpect(jsonPath("$[1]").value(102));
    }

    @Test
    void testGetNotificationsByTenant() throws Exception {
        when(wishlistService.getNotificationsByTenant(123L))
                .thenReturn(List.of("Room with ID 101 is now available!"));

        mockMvc.perform(get("/api/wishlist/notifications/tenant/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0]").value("Room with ID 101 is now available!"));
    }

    @Test
    void testGetNotificationsByOwner() throws Exception {
        when(wishlistService.getNotificationsByOwner(200L))
                .thenReturn(List.of("Tenant added your house ID 101 to wishlist."));

        mockMvc.perform(get("/api/wishlist/notifications/owner/200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0]").value("Tenant added your house ID 101 to wishlist."));
    }

    @Test
    void testNotifyAvailability() throws Exception {
        mockMvc.perform(post("/api/wishlist/notify")
                        .param("houseId", "101"))
                .andExpect(status().isOk());

        verify(wishlistService).notifyAvailability(101L);
    }
}
