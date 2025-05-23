package id.ac.ui.cs.advprog.papikos.wishlist.observer;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.WishlistItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WishlistNotifierImpl implements WishlistNotifier {

    private final NotificationRepository notificationRepo;
    private final WishlistItemRepository wishlistItemRepo;

    @Override
    public void registerObserver(Long houseId, NotificationObserver observer) {
        // No-op, or you can remove this method entirely if unused
    }

    @Override
    public void notifyObservers(Long houseId, Long ownerId) {
        List<Long> tenantIds = wishlistItemRepo.findByHouseId(houseId)
                .stream()
                .map(WishlistItem::getTenantId)
                .distinct()
                .toList();

        for (Long tenantId : tenantIds) {
            Notification notification = Notification.builder()
                    .tenantId(tenantId)
                    .ownerId(ownerId)
                    .message("House " + houseId + " has new availability.")
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepo.save(notification);
        }
    }

    @Override
    public void removeObserver(Long houseId, NotificationObserver observer) {
        // No-op (or remove if unused)
    }
}
