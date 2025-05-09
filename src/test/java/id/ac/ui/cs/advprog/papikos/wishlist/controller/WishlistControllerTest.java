package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.DTO.WishlistRequest;
import id.ac.ui.cs.advprog.papikos.wishlist.service.WishlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class)
public class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WishlistService wishlistService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testAddToWishlist() throws Exception {
        WishlistRequest request = new WishlistRequest();
        request.setTenantId("tenant123");
        request.setHouseId(101L);

        mockMvc.perform(post("/api/wishlist/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(wishlistService).addToWishlist("tenant123", 101L);
    }

    @Test
    void testGetWishlistByTenant() throws Exception {
        when(wishlistService.getWishlistByTenant("tenant123"))
                .thenReturn(List.of(101L, 102L));

        mockMvc.perform(get("/api/wishlist/tenant/tenant123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0]").value(101))
                .andExpect(jsonPath("$[1]").value(102));
    }

    @Test
    void testGetNotificationsByTenant() throws Exception {
        when(wishlistService.getNotificationsByTenant("tenant123"))
                .thenReturn(List.of("Room with ID 101 is now available!"));

        mockMvc.perform(get("/api/wishlist/notifications/tenant123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0]").value("Room with ID 101 is now available!"));
    }
}
