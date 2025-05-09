package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import id.ac.ui.cs.advprog.papikos.wishlist.observer.TenantNotificationObserver;
import id.ac.ui.cs.advprog.papikos.wishlist.observer.WishlistNotifier;
import id.ac.ui.cs.advprog.papikos.wishlist.observer.WishlistNotifierImpl;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.WishlistItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistItemRepo;
    private final NotificationRepository notificationRepo;
    private final HouseRepository houseRepository;
    private final WishlistNotifier notifier = new WishlistNotifierImpl();

    @Override
    public void registerTenant(String tenantId, String tenantName) {
        // No-op or implementation as needed
    }

    @Override
    public void addToWishlist(String tenantId, Long houseId) {
        houseRepository.findById(houseId)
                .orElseThrow(() -> new EntityNotFoundException("House not found with id: " + houseId));

        WishlistItem existing = wishlistItemRepo.findByTenantIdAndHouseId(tenantId, houseId);
        if (existing != null) {
            return;
        }

        WishlistItem item = WishlistItem.builder()
                .tenantId(tenantId)
                .houseId(houseId)
                .build();
        wishlistItemRepo.save(item);

        notifier.registerObserver(houseId, new TenantNotificationObserver(tenantId, notificationRepo));
        notifier.notifyObservers(houseId);
    }

    @Override
    public void removeFromWishlist(String tenantId, Long houseId) {
        WishlistItem item = wishlistItemRepo.findByTenantIdAndHouseId(tenantId, houseId);
        if (item != null) {
            wishlistItemRepo.delete(item);
        }
    }

    @Override
    public List<Long> getWishlistByTenant(String tenantId) {
        return wishlistItemRepo.findByTenantId(tenantId)
                .stream()
                .map(WishlistItem::getHouseId)
                .toList();
    }

    @Override
    public List<String> getNotificationsByTenant(String tenantId) {
        return notificationRepo.findByTenantId(tenantId)
                .stream().map(Notification::getMessage).toList();
    }

    @Override
    public void notifyAvailability(Long houseId) {
        List<WishlistItem> items = wishlistItemRepo.findByHouseId(houseId);
        if (items.isEmpty()) {
            return;
        }
        notifier.notifyObservers(houseId);
    }
}
