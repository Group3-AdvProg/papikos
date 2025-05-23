package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.observer.WishlistNotifier;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepo;
    private final HouseRepository houseRepository;
    private final WishlistNotifier notifier;

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

    @Async
    @Override
    public void notifyAvailability(Long houseId) {
        houseRepository.findById(houseId).ifPresent(house -> {
            if (house.getOwner() != null && house.getOwner().getId() != null) {
                notifier.notifyObservers(houseId, house.getOwner().getId());
            }
        });
    }
}
