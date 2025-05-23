package id.ac.ui.cs.advprog.papikos.wishlist.observer;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class UserNotificationObserver implements NotificationObserver {
    private final Long tenantId;
    private final NotificationRepository notificationRepo;

    // Match the NotificationObserver interface exactly
    @Override
    public void update(Long houseId) {
        Notification notification = Notification.builder()
                .tenantId(tenantId)
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
