package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.WishlistItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepo;

    @Mock
    private HouseRepository houseRepo;

    @Mock
    private WishlistItemRepository wishlistRepo;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private House house;
    private User owner;
    private WishlistItem wishlistItem;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);

        house = new House();
        house.setId(10L);
        house.setOwner(owner);

        wishlistItem = WishlistItem.builder().tenantId(2L).houseId(10L).build();
    }

    @Test
    void testGetNotificationsByTenant() {
        Notification notif = Notification.builder()
                .tenantId(2L)
                .ownerId(1L)
                .message("Test message")
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        when(notificationRepo.findByTenantId(2L)).thenReturn(List.of(notif));

        var result = notificationService.getNotificationsByTenant(2L);
        assertEquals(List.of("Test message"), result);
    }


    @Test
    void testGetNotificationsByOwner() {
        Notification notif = Notification.builder()
                .tenantId(2L)
                .ownerId(1L)
                .message("Owner message")
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        when(notificationRepo.findByOwnerId(1L)).thenReturn(List.of(notif));

        var result = notificationService.getNotificationsByOwner(1L);
        assertEquals(List.of("Owner message"), result);
    }


    @Test
    void testNotifyAvailability() {
        when(houseRepo.findById(10L)).thenReturn(Optional.of(house));
        when(wishlistRepo.findByHouseId(10L)).thenReturn(List.of(wishlistItem));

        notificationService.notifyAvailability(10L).join();

        verify(notificationRepo, times(1)).save(any(Notification.class));
    }

    @Test
    void testNotifyAvailability_HouseWithoutOwner() {
        house.setOwner(null);
        when(houseRepo.findById(10L)).thenReturn(Optional.of(house));
        when(wishlistRepo.findByHouseId(10L)).thenReturn(List.of(wishlistItem));

        notificationService.notifyAvailability(10L).join();

        verify(notificationRepo).save(argThat(n ->
                n.getTenantId().equals(2L) &&
                        n.getOwnerId() == null &&
                        n.getMessage().contains("House 10 is now available") &&
                        n.getCreatedAt() != null &&
                        !n.isRead()
        ));
    }

}
