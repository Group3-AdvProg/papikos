package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.WishlistItemRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WishlistServiceImplTest {

    private WishlistItemRepository wishlistItemRepo;
    private NotificationRepository notificationRepo;
    private WishlistServiceImpl wishlistService;

    @BeforeEach
    void setUp() {
        wishlistItemRepo = mock(WishlistItemRepository.class);
        notificationRepo = mock(NotificationRepository.class);
        wishlistService = new WishlistServiceImpl(wishlistItemRepo, notificationRepo);
    }

    @Test
    void testAddToWishlist() {
        wishlistService.addToWishlist("tenant123", "Kamar AC");

        ArgumentCaptor<WishlistItem> captor = ArgumentCaptor.forClass(WishlistItem.class);
        verify(wishlistItemRepo).save(captor.capture());

        WishlistItem savedItem = captor.getValue();
        assertEquals("tenant123", savedItem.getTenantId());
        assertEquals("Kamar AC", savedItem.getRoomType());
    }

    @Test
    void testRemoveFromWishlist() {
        WishlistItem mockItem = WishlistItem.builder().tenantId("tenant123").roomType("Kamar AC").build();
        when(wishlistItemRepo.findByTenantIdAndRoomType("tenant123", "Kamar AC")).thenReturn(mockItem);

        wishlistService.removeFromWishlist("tenant123", "Kamar AC");

        verify(wishlistItemRepo).delete(mockItem);
    }

    @Test
    void testGetWishlistByTenant() {
        WishlistItem item1 = WishlistItem.builder().tenantId("tenant123").roomType("A").build();
        WishlistItem item2 = WishlistItem.builder().tenantId("tenant123").roomType("B").build();

        when(wishlistItemRepo.findByTenantId("tenant123")).thenReturn(List.of(item1, item2));

        List<String> wishlist = wishlistService.getWishlistByTenant("tenant123");

        assertEquals(List.of("A", "B"), wishlist);
    }

    @Test
    void testNotifyAvailability() {
        WishlistItem item = WishlistItem.builder().tenantId("tenant123").roomType("Kamar AC").build();
        when(wishlistItemRepo.findByRoomType("Kamar AC")).thenReturn(List.of(item));

        wishlistService.notifyAvailability("Kamar AC");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepo).save(captor.capture());

        Notification notif = captor.getValue();
        assertEquals("tenant123", notif.getTenantId());
        assertTrue(notif.getMessage().contains("Kamar AC"));
    }
}
