package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.WishlistItemRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
    void testRemoveFromWishlistItemNotFound() {
        when(wishlistItemRepo.findByTenantIdAndRoomType("tenant123", "Nonexistent Room")).thenReturn(null);

        assertDoesNotThrow(() -> wishlistService.removeFromWishlist("tenant123", "Nonexistent Room"));
        verify(wishlistItemRepo, never()).delete(any());
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
    void testGetWishlistByTenantEmpty() {
        when(wishlistItemRepo.findByTenantId("tenant123")).thenReturn(Collections.emptyList());

        List<String> wishlist = wishlistService.getWishlistByTenant("tenant123");

        assertTrue(wishlist.isEmpty());
    }

    @Test
    void testNotifyAvailabilityCreatesNotifications() {
        WishlistItem item = WishlistItem.builder().tenantId("tenant123").roomType("Kamar AC").build();
        when(wishlistItemRepo.findByRoomType("Kamar AC")).thenReturn(List.of(item));

        wishlistService.notifyAvailability("Kamar AC");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepo).save(captor.capture());

        Notification notif = captor.getValue();
        assertEquals("tenant123", notif.getTenantId());
        assertTrue(notif.getMessage().contains("Kamar AC"));
        assertNotNull(notif.getCreatedAt());
        assertTrue(notif.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testNotifyAvailabilityWhenNoMatchingItems() {
        when(wishlistItemRepo.findByRoomType("Nonexistent Room")).thenReturn(Collections.emptyList());

        wishlistService.notifyAvailability("Nonexistent Room");

        verify(notificationRepo, never()).save(any());
    }

    @Test
    void testGetNotificationsByTenant() {
        Notification notif1 = Notification.builder()
                .tenantId("tenant123")
                .message("Room A available!")
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        Notification notif2 = Notification.builder()
                .tenantId("tenant123")
                .message("Room B available!")
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        when(notificationRepo.findByTenantId("tenant123")).thenReturn(List.of(notif1, notif2));

        List<Notification> notifications = wishlistService.getNotificationsByTenant("tenant123");

        assertEquals(2, notifications.size());
        assertTrue(notifications.get(0).getMessage().contains("Room"));
    }

    @Test
    void testGetNotificationsByTenantEmpty() {
        when(notificationRepo.findByTenantId("tenant123")).thenReturn(Collections.emptyList());

        List<Notification> notifications = wishlistService.getNotificationsByTenant("tenant123");

        assertTrue(notifications.isEmpty());
    }

    @Test
    void testAddAndGetWishlist() {
        wishlistService.addToWishlist("tenant123", "Kamar AC");
        List<String> wishlist = wishlistService.getWishlistByTenant("tenant123");

        assertEquals(1, wishlist.size());
        assertEquals("Kamar AC", wishlist.get(0));
    }

    @Test
    void testAddMultipleRoomTypes() {
        wishlistService.addToWishlist("tenant123", "Kamar AC");
        wishlistService.addToWishlist("tenant123", "Kamar Deluxe");

        List<String> wishlist = wishlistService.getWishlistByTenant("tenant123");
        assertEquals(2, wishlist.size());
        assertTrue(wishlist.contains("Kamar Deluxe"));
    }

    @Test
    void testNotifyAvailabilitySendsNotifications() {
        wishlistService.addToWishlist("tenant123", "Kamar AC");
        wishlistService.notifyAvailability("Kamar AC");

        List<Notification> notifications = wishlistService.getNotificationsByTenant("tenant123");
        assertEquals(1, notifications.size());
        assertEquals("Room type Kamar AC is now available!", notifications.get(0).getMessage());
    }

    @Test
    void testNoNotificationForUnlistedRoomType() {
        wishlistService.addToWishlist("tenant123", "Kamar AC");
        wishlistService.notifyAvailability("Kamar Deluxe");

        List<Notification> notifications = wishlistService.getNotificationsByTenant("tenant123");
        assertTrue(notifications.isEmpty());
    }

    @Test
    void testGetWishlistForUnknownTenant() {
        List<String> wishlist = wishlistService.getWishlistByTenant("unknownTenant");
        assertNotNull(wishlist);
        assertTrue(wishlist.isEmpty());
    }

    @Test
    void testPreventDuplicateWishlistEntry() {
        WishlistItem existing = WishlistItem.builder()
                .tenantId("tenant123")
                .roomType("Kamar AC")
                .build();

        when(wishlistItemRepo.findByTenantIdAndRoomType("tenant123", "Kamar AC")).thenReturn(existing);

        wishlistService.addToWishlist("tenant123", "Kamar AC");

        verify(wishlistItemRepo, never()).save(any());
    }

}
