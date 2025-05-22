package id.ac.ui.cs.advprog.papikos.wishlist.observer;

// ...existing imports...

@RequiredArgsConstructor
public class TenantNotificationObserver {
    private final Long tenantId;
    private final NotificationRepository notificationRepo;

    public TenantNotificationObserver(Long tenantId, NotificationRepository repo) {
        this.tenantId = tenantId;
        this.notificationRepo = repo;
    }

    public void update(Long houseId, Long ownerId) {
        Notification notification = Notification.builder()
                .tenantId(tenantId)
                .ownerId(ownerId)
                .houseId(houseId)
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
