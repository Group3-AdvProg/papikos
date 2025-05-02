package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import id.ac.ui.cs.advprog.papikos.wishlist.observer.TenantNotificationObserver;
import id.ac.ui.cs.advprog.papikos.wishlist.observer.WishlistNotifierImpl;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.WishlistItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistItemRepo;
    private final NotificationRepository notificationRepo;
    private final HouseRepository houseRepository;

    private final WishlistNotifierImpl notifier = new WishlistNotifierImpl();
    private final Map<String, List<Long>> wishlistMap = new HashMap<>();

    @Override
    public void registerTenant(String tenantId, String tenantName) {
        // In a real setup, save tenant to DB or cache
        wishlistMap.putIfAbsent(tenantId, new ArrayList<>());
    }

    @Override
    public void addToWishlist(String tenantId, String roomType) {
        WishlistItem item = WishlistItem.builder()
                .tenantId(tenantId)
                .roomType(roomType)
                .build();
        wishlistItemRepo.save(item);

        notifier.registerObserver(roomType, new TenantNotificationObserver(tenantId, notificationRepo));
        notifier.notifyObservers(roomType);
    }

    @Override
    public void removeFromWishlist(String tenantId, String roomType) {
        WishlistItem item = wishlistItemRepo.findByTenantIdAndRoomType(tenantId, roomType);
        if (item != null) {
            wishlistItemRepo.delete(item);
        }
        wishlistMap.getOrDefault(tenantId, new ArrayList<>()).remove(roomType);
    }

    @Override
    public List<String> getWishlistByTenant(String tenantId) {
        return wishlistItemRepo.findByTenantId(tenantId)
                .stream().map(WishlistItem::getRoomType).toList();
    }

    @Override
    public List<String> getNotificationsByTenant(String tenantId) {
        return notificationRepo.findByTenantId(tenantId)
                .stream().map(Notification::getMessage).toList();
    }

    @Override
    public void notifyAvailability(String roomType) {
        notifier.notifyObservers(roomType);
    }
}
