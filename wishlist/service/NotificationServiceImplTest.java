package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.observer.WishlistNotifier;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@EnableAsync
@ContextConfiguration
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepo;

    @Mock
    private HouseRepository houseRepo;

    @Mock
    private WishlistNotifier notifier;

    private NotificationServiceImpl notificationService;

    private final Long tenantId = 1L;
    private final Long ownerId = 2L;
    private final Long houseId = 3L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationService = new NotificationServiceImpl(notificationRepo, houseRepo, notifier);
    }

    @Test
    void testGetNotificationsByTenant() {
        Notification n1 = Notification.builder().tenantId(tenantId).message("msg1").build();
        Notification n2 = Notification.builder().tenantId(tenantId).message("msg2").build();
        when(notificationRepo.findByTenantId(tenantId)).thenReturn(List.of(n1, n2));

        List<String> result = notificationService.getNotificationsByTenant(tenantId);
        assert result.equals(List.of("msg1", "msg2"));
    }

    @Test
    void testGetNotificationsByOwner() {
        Notification n1 = Notification.builder().ownerId(ownerId).message("admin1").build();
        when(notificationRepo.findByOwnerId(ownerId)).thenReturn(List.of(n1));

        List<String> result = notificationService.getNotificationsByOwner(ownerId);
        assert result.equals(List.of("admin1"));
    }

    @Test
    void testNotifyAvailabilityAsync() throws Exception {
        House house = new House();
        house.setId(houseId);
        house.setOwner(new id.ac.ui.cs.advprog.papikos.auth.entity.User());
        house.getOwner().setId(ownerId);

        when(houseRepo.findById(houseId)).thenReturn(Optional.of(house));

        notificationService.notifyAvailability(houseId);

        // Allow async method to execute
        Thread.sleep(100);

        verify(notifier, times(1)).notifyObservers(houseId, ownerId);
    }
}
