package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import id.ac.ui.cs.advprog.papikos.wishlist.observer.UserNotificationObserver;
import id.ac.ui.cs.advprog.papikos.wishlist.observer.WishlistNotifier;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.WishlistItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistItemRepo;
    private final NotificationRepository notificationRepo;
    private final HouseRepository houseRepository;
    private final WishlistNotifier notifier;

    @Override
    public void addToWishlist(Long tenantId, Long houseId) {
        House house = houseRepository.findById(houseId)
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

        // Async notification registration
        createObserverAsync(houseId, tenantId);

        if (house.getOwner() != null && house.getOwner().getId() != null) {
            notifier.notifyObservers(houseId, house.getOwner().getId());
        }
    }

    @Async
    public CompletableFuture<Void> createObserverAsync(Long houseId, Long tenantId) {
        notifier.registerObserver(houseId, new UserNotificationObserver(tenantId, notificationRepo));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void removeFromWishlist(Long tenantId, Long houseId) {
        WishlistItem item = wishlistItemRepo.findByTenantIdAndHouseId(tenantId, houseId);
        if (item != null) {
            wishlistItemRepo.delete(item);
        }
    }

    @Override
    public List<Long> getWishlistByTenant(Long tenantId) {
        return wishlistItemRepo.findByTenantId(tenantId)
                .stream()
                .map(WishlistItem::getHouseId)
                .toList();
    }

    @Override
    public List<String> getNotificationsByTenant(Long tenantId) {
        return notificationRepo.findByTenantId(tenantId)
                .stream()
                .map(Notification::getMessage)
                .toList();
    }

    @Override
    public List<String> getNotificationsByOwner(Long ownerId) {
        return notificationRepo.findByOwnerId(ownerId)
                .stream()
                .map(Notification::getMessage)
                .toList();
    }

    @Override
    public void notifyAvailability(Long houseId) {
        houseRepository.findById(houseId).ifPresent(house -> {
            if (house.getOwner() != null && house.getOwner().getId() != null) {
                notifier.notifyObservers(houseId, house.getOwner().getId());
            }
        });
    }
}
