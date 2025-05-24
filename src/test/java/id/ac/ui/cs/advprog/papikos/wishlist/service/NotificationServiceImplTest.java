package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.house.model.House;
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

    @Mock
    private UserRepository userRepo;

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

        wishlistItem = WishlistItem.builder()
                .tenantId(2L)
                .houseId(10L)
                .build();
    }

    @Test
    void testGetNotificationsByReceiver() {
        Notification notif = Notification.builder()
                .receiverId(2L)
                .senderId(1L)
                .message("Test message")
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        when(notificationRepo.findByReceiverId(2L)).thenReturn(List.of(notif));

        var result = notificationService.getNotificationsByReceiver(2L);
        assertEquals(1, result.size());
        assertEquals("Test message", result.get(0).getMessage());
    }

    @Test
    void testGetNotificationsBySender() {
        Notification notif = Notification.builder()
                .receiverId(2L)
                .senderId(1L)
                .message("Sender message")
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        when(notificationRepo.findBySenderId(1L)).thenReturn(List.of(notif));

        var result = notificationService.getNotificationsBySender(1L);
        assertEquals(1, result.size());
        assertEquals("Sender message", result.get(0).getMessage());
    }

    @Test
    void testNotifyAvailability() {
        when(houseRepo.findById(10L)).thenReturn(Optional.of(house));
        when(wishlistRepo.findByHouseId(10L)).thenReturn(List.of(wishlistItem));

        notificationService.notifyAvailability(10L).join();

        verify(notificationRepo, times(1)).save(argThat(n ->
                n.getReceiverId().equals(2L) &&
                        n.getSenderId().equals(1L) &&
                        n.getMessage().contains("House 10 is now available") &&
                        !n.isRead()
        ));
    }

    @Test
    void testNotifyAvailability_HouseWithoutOwner() {
        house.setOwner(null);
        when(houseRepo.findById(10L)).thenReturn(Optional.of(house));
        when(wishlistRepo.findByHouseId(10L)).thenReturn(List.of(wishlistItem));

        notificationService.notifyAvailability(10L).join();

        verify(notificationRepo, times(1)).save(argThat(n ->
                n.getReceiverId().equals(2L) &&
                        n.getSenderId() == null &&
                        n.getMessage().contains("House 10 is now available") &&
                        !n.isRead()
        ));
    }

    @Test
    void testSendToAllUsers() {
        User u1 = new User(); u1.setId(10L);
        User u2 = new User(); u2.setId(20L);

        when(userRepo.findAll()).thenReturn(List.of(u1, u2));

        notificationService.sendToAllUsers(99L, "Admin broadcast");

        verify(notificationRepo, times(2)).save(argThat(n ->
                (n.getReceiverId().equals(10L) || n.getReceiverId().equals(20L)) &&
                        n.getSenderId().equals(99L) &&
                        n.getMessage().equals("Admin broadcast") &&
                        !n.isRead()
        ));
    }

    @Test
    void testNotifyTenantRentalApproved_savesNotification() {
        User tenant = new User();
        tenant.setId(2L);

        House house = new House();
        house.setId(1L);
        house.setName("Kos UI");
        house.setOwner(new User());

        when(houseRepo.findById(1L)).thenReturn(Optional.of(house));

        notificationService.notifyTenantRentalApproved(99L, 2L, 1L).join();

        verify(notificationRepo, times(1)).save(argThat(n ->
                n.getReceiverId().equals(2L) &&
                        n.getSenderId().equals(99L) &&
                        n.getMessage().equals("Your rental request for Kos UI has been approved!") &&
                        !n.isRead()
        ));
    }

    @Test
    void testNotifyTenantRentalRejected_savesNotification() {
        User tenant = new User();
        tenant.setId(2L);

        House house = new House();
        house.setId(1L);
        house.setName("Kos UI");
        house.setOwner(new User());

        when(houseRepo.findById(1L)).thenReturn(Optional.of(house));

        notificationService.notifyTenantRentalRejected(99L, 2L, 1L).join();

        verify(notificationRepo, times(1)).save(argThat(n ->
                n.getReceiverId().equals(2L) &&
                        n.getSenderId().equals(99L) &&
                        n.getMessage().equals("Your rental request for Kos UI has been rejected.") &&
                        !n.isRead()
        ));
    }
}
