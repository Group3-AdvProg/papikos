package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.WishlistItemRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistItemRepo;
    private final NotificationRepository notificationRepo;

    @Override
    public void registerTenant(String id, String name) {

    }

    @Override
    public void addToWishlist(String tenantId, String roomType) {
        WishlistItem item = WishlistItem.builder()
                .tenantId(tenantId)
                .roomType(roomType)
                .build();
        wishlistItemRepo.save(item);
    }

    @Override
    public void removeFromWishlist(String tenantId, String roomType) {
        WishlistItem item = wishlistItemRepo.findByTenantIdAndRoomType(tenantId, roomType);
        if (item != null) {
            wishlistItemRepo.delete(item);
        }
    }

    @Override
    public List<String> getWishlistByTenant(String tenantId) {
        List<WishlistItem> items = wishlistItemRepo.findByTenantId(tenantId);
        return items.stream()
                .map(WishlistItem::getRoomType)
                .toList();
    }

    @Override
    public List<String> getNotificationsByTenant(String tenantId) {
        List<Notification> notifications = notificationRepo.findByTenantId(tenantId);
        return notifications.stream()
                .map(Notification::getMessage)
                .toList();
    }

    @Override
    public void notifyAvailability(String roomType) {
        List<WishlistItem> interestedTenants = wishlistItemRepo.findByRoomType(roomType);

        for (WishlistItem item : interestedTenants) {
            Notification notification = Notification.builder()
                    .tenantId(item.getTenantId())
                    .message("Room type " + roomType + " is now available!")
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .build();
            notificationRepo.save(notification);
        }
    }
}
