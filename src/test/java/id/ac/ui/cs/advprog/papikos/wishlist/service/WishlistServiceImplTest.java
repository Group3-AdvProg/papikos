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
        wishlistService.setNotifier(notifier); // you need to allow injecting the mock

    }

    @Test
    void testAddToWishlist() {
        House house = mock(House.class);
        when(houseRepo.findById(1L)).thenReturn(java.util.Optional.of(house));
        when(house.getOwner()).thenReturn(null); // No notification sent
        when(wishlistItemRepo.findByTenantIdAndHouseId(123L, 1L)).thenReturn(null);

        wishlistService.addToWishlist(123L, 1L);

        ArgumentCaptor<WishlistItem> captor = ArgumentCaptor.forClass(WishlistItem.class);
        verify(wishlistItemRepo).save(captor.capture());

        WishlistItem savedItem = captor.getValue();
        assertEquals(123L, savedItem.getTenantId());
        assertEquals(1L, savedItem.getHouseId());
    }

    @Test
    void testPreventDuplicateWishlistEntry() {
        WishlistItem existing = WishlistItem.builder()
                .tenantId(123L)
                .houseId(1L)
                .build();

        House house = mock(House.class);
        when(houseRepo.findById(1L)).thenReturn(java.util.Optional.of(house));
        when(wishlistItemRepo.findByTenantIdAndHouseId(123L, 1L)).thenReturn(existing);

        wishlistService.addToWishlist(123L, 1L);

        verify(wishlistItemRepo, never()).save(any());
    }

    @Test
    void testRemoveFromWishlist() {
        WishlistItem mockItem = WishlistItem.builder()
                .tenantId(123L)
                .houseId(1L)
                .build();

        when(wishlistItemRepo.findByTenantIdAndHouseId(123L, 1L)).thenReturn(mockItem);

        wishlistService.removeFromWishlist(123L, 1L);

        verify(wishlistItemRepo).delete(mockItem);
    }

    @Test
    void testRemoveFromWishlistItemNotFound() {
        when(wishlistItemRepo.findByTenantIdAndHouseId(123L, 999L)).thenReturn(null);

        assertDoesNotThrow(() -> wishlistService.removeFromWishlist(123L, 999L));
        verify(wishlistItemRepo, never()).delete(any());
    }

    @Test
    void testGetWishlistByTenant() {
        WishlistItem item1 = WishlistItem.builder().tenantId(123L).houseId(1L).build();
        WishlistItem item2 = WishlistItem.builder().tenantId(123L).houseId(2L).build();

        when(wishlistItemRepo.findByTenantId(123L)).thenReturn(List.of(item1, item2));

        List<Long> wishlist = wishlistService.getWishlistByTenant(123L);

        assertEquals(List.of(1L, 2L), wishlist);
    }

    @Test
    void testGetWishlistByTenantEmpty() {
        when(wishlistItemRepo.findByTenantId(123L)).thenReturn(Collections.emptyList());

        List<Long> wishlist = wishlistService.getWishlistByTenant(123L);

        assertTrue(wishlist.isEmpty());
    }

    @Test
    void testGetWishlistForUnknownTenant() {
        when(wishlistItemRepo.findByTenantId(999L)).thenReturn(Collections.emptyList());

        List<Long> wishlist = wishlistService.getWishlistByTenant(999L);

        assertNotNull(wishlist);
        assertTrue(wishlist.isEmpty());
    }

    @Test
    void testNotifyAvailabilityCreatesNotifications() {
        WishlistItem item = WishlistItem.builder().tenantId(123L).houseId(1L).build();
        when(wishlistItemRepo.findByHouseId(1L)).thenReturn(List.of(item));
        House house = mock(House.class);
        when(houseRepo.findById(1L)).thenReturn(Optional.of(house));
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
        when(notificationRepo.findByTenantId(123L)).thenReturn(Collections.emptyList());
        List<String> notifications = wishlistService.getNotificationsByTenant(123L);
        assertNotNull(notifications);
        assertTrue(notifications.isEmpty());
    }

    @Test
    void testGetNotificationsByTenantEmpty() {
        when(notificationRepo.findByTenantId(123L)).thenReturn(Collections.emptyList());
        List<String> notifications = wishlistService.getNotificationsByTenant(123L);
        assertNotNull(notifications);
        assertTrue(notifications.isEmpty());
    }

    @Test
    void testAddMultipleHouseIds() {
        House house1 = mock(House.class);
        House house2 = mock(House.class);
        when(houseRepo.findById(1L)).thenReturn(java.util.Optional.of(house1));
        when(houseRepo.findById(2L)).thenReturn(java.util.Optional.of(house2));
        when(wishlistItemRepo.findByTenantIdAndHouseId(123L, 1L)).thenReturn(null);
        when(wishlistItemRepo.findByTenantIdAndHouseId(123L, 2L)).thenReturn(null);
        when(house1.getOwner()).thenReturn(null);
        when(house2.getOwner()).thenReturn(null);

        wishlistService.addToWishlist(123L, 1L);
        wishlistService.addToWishlist(123L, 2L);

        verify(wishlistItemRepo, times(2)).save(any());
    }

    @Test
    void testNotifyAvailabilityCreatesNotifications() {
        WishlistItem item = WishlistItem.builder().tenantId(123L).houseId(1L).build();
        when(wishlistItemRepo.findByHouseId(1L)).thenReturn(List.of(item));
        House house = mock(House.class);
        when(houseRepo.findById(1L)).thenReturn(java.util.Optional.of(house));
        when(house.getOwner()).thenReturn(null); // Tenant will be notified

        wishlistService.initNotifier(); // Make sure observers are registered
        wishlistService.addToWishlist(123L, 1L); // Registers observer
        wishlistService.notifyAvailability(1L);  // Should trigger notification

        verify(notificationRepo, atLeastOnce()).save(argThat(notification ->
                notification.getTenantId().equals(123L) &&
                        notification.getMessage().contains("House ID 1")
        ));
    }
    @SpringBootTest
    @AutoConfigureTestDatabase
    class WishlistServiceImplTest {

        @MockBean
        private WishlistItemRepository wishlistItemRepo;

        @MockBean
        private NotificationRepository notificationRepo;

        @MockBean
        private HouseRepository houseRepo;

        @MockBean
        private WishlistNotifier notifier;

        @Autowired
        private WishlistServiceImpl wishlistService;

        private final Long tenantId = 1L;
        private final Long houseId = 100L;
        private final Long ownerId = 200L;

        private House sampleHouse;

        @BeforeEach
        void setUp() {
            sampleHouse = new House();
            sampleHouse.setId(houseId);
            User owner = new User();
            owner.setId(ownerId);
            sampleHouse.setOwner(owner);
        }

        @Test
        void testAddToWishlistSuccess() {
            Mockito.when(houseRepo.findById(houseId)).thenReturn(Optional.of(sampleHouse));
            Mockito.when(wishlistItemRepo.findByTenantIdAndHouseId(tenantId, houseId)).thenReturn(null);

            wishlistService.addToWishlist(tenantId, houseId);

            Mockito.verify(wishlistItemRepo, times(1)).save(any(WishlistItem.class));
            Mockito.verify(notifier, times(1)).registerObserver(eq(houseId), any());
            Mockito.verify(notifier, times(1)).notifyObservers(houseId, ownerId);
        }

        @Test
        void testAddToWishlistWhenAlreadyExists() {
            Mockito.when(houseRepo.findById(houseId)).thenReturn(Optional.of(sampleHouse));
            Mockito.when(wishlistItemRepo.findByTenantIdAndHouseId(tenantId, houseId))
                    .thenReturn(new WishlistItem());

            wishlistService.addToWishlist(tenantId, houseId);

            Mockito.verify(wishlistItemRepo, never()).save(any());
            Mockito.verify(notifier, never()).registerObserver(any(), any());
            Mockito.verify(notifier, never()).notifyObservers(any(), any());
        }

        @Test
        void testRemoveFromWishlist() {
            WishlistItem item = WishlistItem.builder().id(1L).tenantId(tenantId).houseId(houseId).build();
            Mockito.when(wishlistItemRepo.findByTenantIdAndHouseId(tenantId, houseId)).thenReturn(item);

            wishlistService.removeFromWishlist(tenantId, houseId);

            Mockito.verify(wishlistItemRepo, times(1)).delete(item);
        }

        @Test
        void testGetNotificationsByTenant() {
            Notification n1 = Notification.builder().tenantId(tenantId).message("Hello").build();
            Notification n2 = Notification.builder().tenantId(tenantId).message("World").build();
            Mockito.when(notificationRepo.findByTenantId(tenantId)).thenReturn(List.of(n1, n2));

            List<String> messages = wishlistService.getNotificationsByTenant(tenantId);

            assertEquals(List.of("Hello", "World"), messages);
        }
    }

}
