package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.WishlistItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepo;
    private final HouseRepository houseRepo;
    private final WishlistItemRepository wishlistRepo;

    @Override
    public List<String> getNotificationsByTenant(Long tenantId) {
        return notificationRepo.findByTenantId(tenantId)
                .stream().map(Notification::getMessage).toList();
    }

    @Override
    public List<String> getNotificationsByOwner(Long ownerId) {
        return notificationRepo.findByOwnerId(ownerId)
                .stream().map(Notification::getMessage).toList();
    }

    @Async
    @Override
    public CompletableFuture<Void> notifyAvailability(Long houseId) {
        houseRepo.findById(houseId).ifPresent(house -> {
            Long ownerId = house.getOwner() != null ? house.getOwner().getId() : null;

            List<Long> tenantIds = wishlistRepo.findByHouseId(houseId)
                    .stream()
                    .map(WishlistItem::getTenantId)
                    .distinct()
                    .toList();

            for (Long tenantId : tenantIds) {
                var notif = Notification.builder()
                        .tenantId(tenantId)
                        .ownerId(ownerId)
                        .message("House " + houseId + " is now available!")
                        .createdAt(LocalDateTime.now())
                        .isRead(false)
                        .build();
                notificationRepo.save(notif);
            }
        });
        return CompletableFuture.completedFuture(null);
    }
}

