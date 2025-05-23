package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import id.ac.ui.cs.advprog.papikos.wishlist.observer.WishlistNotifier;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.WishlistItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WishlistServiceImplTest {

    @MockBean
    private WishlistItemRepository wishlistItemRepo;

    @MockBean
    private NotificationRepository notificationRepo;

    @MockBean
    private HouseRepository houseRepo;

    @MockBean
    private WishlistNotifier notifier;

    private WishlistServiceImpl wishlistService;

    private final Long tenantId = 1L;
    private final Long houseId = 100L;
    private final Long ownerId = 200L;

    private House sampleHouse;

    @BeforeEach
    void setUp() {
        wishlistService = new WishlistServiceImpl(wishlistItemRepo, notificationRepo, houseRepo, notifier);

        sampleHouse = new House();
        sampleHouse.setId(houseId);
        User owner = new User();
        owner.setId(ownerId);
        sampleHouse.setOwner(owner);
    }

    @Test
    void testAddToWishlistSuccess() {
        when(houseRepo.findById(houseId)).thenReturn(Optional.of(sampleHouse));
        when(wishlistItemRepo.findByTenantIdAndHouseId(tenantId, houseId)).thenReturn(null);

        wishlistService.addToWishlist(tenantId, houseId);

        verify(wishlistItemRepo, times(1)).save(any(WishlistItem.class));
        verify(notifier, times(1)).notifyObservers(houseId, ownerId);
    }

    @Test
    void testAddToWishlistWhenAlreadyExists() {
        when(houseRepo.findById(houseId)).thenReturn(Optional.of(sampleHouse));
        when(wishlistItemRepo.findByTenantIdAndHouseId(tenantId, houseId)).thenReturn(new WishlistItem());

        wishlistService.addToWishlist(tenantId, houseId);

        verify(wishlistItemRepo, never()).save(any());
        verify(notifier, never()).notifyObservers(any(), any());
    }

    @Test
    void testRemoveFromWishlist() {
        WishlistItem item = WishlistItem.builder().id(1L).tenantId(tenantId).houseId(houseId).build();
        when(wishlistItemRepo.findByTenantIdAndHouseId(tenantId, houseId)).thenReturn(item);

        wishlistService.removeFromWishlist(tenantId, houseId);

        verify(wishlistItemRepo, times(1)).delete(item);
    }

    @Test
    void testRemoveFromWishlistItemNotFound() {
        when(wishlistItemRepo.findByTenantIdAndHouseId(tenantId, houseId)).thenReturn(null);

        assertDoesNotThrow(() -> wishlistService.removeFromWishlist(tenantId, houseId));
        verify(wishlistItemRepo, never()).delete(any());
    }

    @Test
    void testGetWishlistByTenant() {
        WishlistItem item1 = WishlistItem.builder().tenantId(tenantId).houseId(1L).build();
        WishlistItem item2 = WishlistItem.builder().tenantId(tenantId).houseId(2L).build();
        when(wishlistItemRepo.findByTenantId(tenantId)).thenReturn(List.of(item1, item2));

        List<Long> result = wishlistService.getWishlistByTenant(tenantId);
        assertEquals(List.of(1L, 2L), result);
    }

    @Test
    void testGetNotificationsByTenant() {
        Notification n1 = Notification.builder().tenantId(tenantId).message("Hello").build();
        Notification n2 = Notification.builder().tenantId(tenantId).message("World").build();
        when(notificationRepo.findByTenantId(tenantId)).thenReturn(List.of(n1, n2));

        List<String> messages = wishlistService.getNotificationsByTenant(tenantId);
        assertEquals(List.of("Hello", "World"), messages);
    }

    @Test
    void testGetNotificationsByOwner() {
        Notification n1 = Notification.builder().ownerId(ownerId).message("Admin update 1").build();
        when(notificationRepo.findByOwnerId(ownerId)).thenReturn(List.of(n1));

        List<String> messages = wishlistService.getNotificationsByOwner(ownerId);
        assertEquals(List.of("Admin update 1"), messages);
    }

    @Test
    void testNotifyAvailabilityValidHouse() {
        when(houseRepo.findById(houseId)).thenReturn(Optional.of(sampleHouse));

        wishlistService.notifyAvailability(houseId);

        verify(notifier, times(1)).notifyObservers(houseId, ownerId);
    }

    @Test
    void testNotifyAvailabilityHouseNotFound() {
        when(houseRepo.findById(999L)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> wishlistService.notifyAvailability(999L));
        verify(notifier, never()).notifyObservers(any(), any());
    }
}
