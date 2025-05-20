package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.WishlistItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WishlistServiceImplTest {

    private WishlistItemRepository wishlistItemRepo;
    private NotificationRepository notificationRepo;
    private HouseRepository houseRepo;
    private WishlistServiceImpl wishlistService;

    @BeforeEach
    void setUp() {
        wishlistItemRepo = mock(WishlistItemRepository.class);
        notificationRepo = mock(NotificationRepository.class);
        houseRepo = mock(HouseRepository.class);
        wishlistService = new WishlistServiceImpl(wishlistItemRepo, notificationRepo, houseRepo);
    }

    @Test
    void testAddToWishlist() {
        House house = mock(House.class);
        when(houseRepo.findById(1L)).thenReturn(java.util.Optional.of(house));
        when(house.getOwner()).thenReturn(null); // No notification sent
        when(wishlistItemRepo.findByTenantIdAndHouseId("tenant123", 1L)).thenReturn(null);

        wishlistService.addToWishlist("tenant123", 1L);

        ArgumentCaptor<WishlistItem> captor = ArgumentCaptor.forClass(WishlistItem.class);
        verify(wishlistItemRepo).save(captor.capture());

        WishlistItem savedItem = captor.getValue();
        assertEquals("tenant123", savedItem.getTenantId());
        assertEquals(1L, savedItem.getHouseId());
    }

    @Test
    void testPreventDuplicateWishlistEntry() {
        WishlistItem existing = WishlistItem.builder()
                .tenantId("tenant123")
                .houseId(1L)
                .build();

        House house = mock(House.class);
        when(houseRepo.findById(1L)).thenReturn(java.util.Optional.of(house));
        when(wishlistItemRepo.findByTenantIdAndHouseId("tenant123", 1L)).thenReturn(existing);

        wishlistService.addToWishlist("tenant123", 1L);

        verify(wishlistItemRepo, never()).save(any());
    }

    @Test
    void testRemoveFromWishlist() {
        WishlistItem mockItem = WishlistItem.builder()
                .tenantId("tenant123")
                .houseId(1L)
                .build();

        when(wishlistItemRepo.findByTenantIdAndHouseId("tenant123", 1L)).thenReturn(mockItem);

        wishlistService.removeFromWishlist("tenant123", 1L);

        verify(wishlistItemRepo).delete(mockItem);
    }

    @Test
    void testRemoveFromWishlistItemNotFound() {
        when(wishlistItemRepo.findByTenantIdAndHouseId("tenant123", 999L)).thenReturn(null);

        assertDoesNotThrow(() -> wishlistService.removeFromWishlist("tenant123", 999L));
        verify(wishlistItemRepo, never()).delete(any());
    }

    @Test
    void testGetWishlistByTenant() {
        WishlistItem item1 = WishlistItem.builder().tenantId("tenant123").houseId(1L).build();
        WishlistItem item2 = WishlistItem.builder().tenantId("tenant123").houseId(2L).build();

        when(wishlistItemRepo.findByTenantId("tenant123")).thenReturn(List.of(item1, item2));

        List<Long> wishlist = wishlistService.getWishlistByTenant("tenant123");

        assertEquals(List.of(1L, 2L), wishlist);
    }

    @Test
    void testGetWishlistByTenantEmpty() {
        when(wishlistItemRepo.findByTenantId("tenant123")).thenReturn(Collections.emptyList());

        List<Long> wishlist = wishlistService.getWishlistByTenant("tenant123");

        assertTrue(wishlist.isEmpty());
    }

    @Test
    void testGetWishlistForUnknownTenant() {
        when(wishlistItemRepo.findByTenantId("unknownTenant")).thenReturn(Collections.emptyList());

        List<Long> wishlist = wishlistService.getWishlistByTenant("unknownTenant");

        assertNotNull(wishlist);
        assertTrue(wishlist.isEmpty());
    }

    @Test
    void testNotifyAvailabilityCreatesNotifications() {
        WishlistItem item = WishlistItem.builder().tenantId("tenant123").houseId(1L).build();
        when(wishlistItemRepo.findByHouseId(1L)).thenReturn(List.of(item));
        House house = mock(House.class);
        when(houseRepo.findById(1L)).thenReturn(java.util.Optional.of(house));
        when(house.getOwner()).thenReturn(null); // No notification sent

        wishlistService.notifyAvailability(1L);
    }

    @Test
    void testNotifyAvailabilityWhenNoMatchingItems() {
        when(wishlistItemRepo.findByHouseId(999L)).thenReturn(Collections.emptyList());
        when(houseRepo.findById(999L)).thenReturn(java.util.Optional.empty());

        wishlistService.notifyAvailability(999L);
    }

    @Test
    void testGetNotificationsByTenant() {
        when(notificationRepo.findByTenantId("tenant123")).thenReturn(Collections.emptyList());
        List<String> notifications = wishlistService.getNotificationsByTenant("tenant123");
        assertNotNull(notifications);
        assertTrue(notifications.isEmpty());
    }

    @Test
    void testGetNotificationsByTenantEmpty() {
        when(notificationRepo.findByTenantId("tenant123")).thenReturn(Collections.emptyList());
        List<String> notifications = wishlistService.getNotificationsByTenant("tenant123");
        assertNotNull(notifications);
        assertTrue(notifications.isEmpty());
    }

    @Test
    void testAddMultipleHouseIds() {
        House house1 = mock(House.class);
        House house2 = mock(House.class);
        when(houseRepo.findById(1L)).thenReturn(java.util.Optional.of(house1));
        when(houseRepo.findById(2L)).thenReturn(java.util.Optional.of(house2));
        when(wishlistItemRepo.findByTenantIdAndHouseId("tenant123", 1L)).thenReturn(null);
        when(wishlistItemRepo.findByTenantIdAndHouseId("tenant123", 2L)).thenReturn(null);
        when(house1.getOwner()).thenReturn(null);
        when(house2.getOwner()).thenReturn(null);

        wishlistService.addToWishlist("tenant123", 1L);
        wishlistService.addToWishlist("tenant123", 2L);

        verify(wishlistItemRepo, times(2)).save(any());
    }
}
