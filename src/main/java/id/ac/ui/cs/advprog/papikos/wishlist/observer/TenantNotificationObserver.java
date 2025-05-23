package id.ac.ui.cs.advprog.papikos.wishlist.observer;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class TenantNotificationObserver {
    private final Long tenantId;
    private final NotificationRepository notificationRepo;

    public void update(Long houseId, Long ownerId) {
        Notification notification = Notification.builder()
                .tenantId(tenantId)
                .ownerId(ownerId)
                .message("House with ID " + houseId + " is now available.")
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        notificationRepo.save(notification);
    }

    public Long getTenantId() {
        return tenantId;
    }
}
