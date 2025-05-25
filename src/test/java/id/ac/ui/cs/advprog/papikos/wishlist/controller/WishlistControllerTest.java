package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.service.WishlistService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WishlistService wishlistService;

    @Test
    void testAddToWishlist() throws Exception {
        mockMvc.perform(post("/api/wishlist/add")
                        .param("userId", "1")
                        .param("houseId", "100"))
                .andExpect(status().isOk());

        verify(wishlistService, times(1)).addToWishlist(1L, 100L);
    }

    @Test
    void testRemoveFromWishlist() throws Exception {
        mockMvc.perform(delete("/api/wishlist/remove")
                        .param("userId", "1")
                        .param("houseId", "100"))
                .andExpect(status().isOk());

        verify(wishlistService, times(1)).removeFromWishlist(1L, 100L);
    }

    @Test
    void testGetWishlistByUser() throws Exception {
        when(wishlistService.getWishlistByTenant(1L)).thenReturn(List.of(101L, 102L));

        mockMvc.perform(get("/api/wishlist/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(101))
                .andExpect(jsonPath("$[1]").value(102));
    }
}
