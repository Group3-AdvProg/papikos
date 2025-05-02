package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.observer.TenantNotificationObserver;
import id.ac.ui.cs.advprog.papikos.wishlist.observer.WishlistNotifierImpl;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.WishlistItemRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistItemRepo;
    private final NotificationRepository notificationRepo;
    private final WishlistNotifierImpl notifier = new WishlistNotifierImpl();

    // Map to track the wishlist for each tenant
    private final Map<String, List<String>> wishlistMap = new HashMap<>();

    @Override
    public void registerTenant(String id, String name) {
        // Implementation to register the tenant
    }

    @Override
    public void addToWishlist(String tenantId, String roomType) {
        // Create a new wishlist item and save it
        WishlistItem item = WishlistItem.builder()
                .tenantId(tenantId)
                .roomType(roomType)
                .build();
        wishlistItemRepo.save(item);

        // Add the room type to the tenant's wishlist
        wishlistMap.computeIfAbsent(tenantId, k -> new ArrayList<>()).add(roomType);

        // Register the observer to notify the tenant when room type becomes available
        TenantNotificationObserver observer = new TenantNotificationObserver(tenantId, notificationRepo);
        notifier.registerObserver(roomType, observer);

        // Notify all registered observers about the room type availability
        notifier.notifyObservers(roomType);
    }

    @Override
    public void removeFromWishlist(String tenantId, String roomType) {
        WishlistItem item = wishlistItemRepo.findByTenantIdAndRoomType(tenantId, roomType);
        if (item != null) {
            wishlistItemRepo.delete(item);
        }

        // Remove from the wishlist map as well
        if (wishlistMap.containsKey(tenantId)) {
            wishlistMap.get(tenantId).remove(roomType);
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
    public List<Notification> getNotificationsByTenant(String tenantId) {
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

        // Notify all registered observers about the availability
        notifier.notifyObservers(roomType);
    }
}
