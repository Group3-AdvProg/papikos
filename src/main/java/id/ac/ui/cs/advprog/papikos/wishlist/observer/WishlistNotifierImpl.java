package id.ac.ui.cs.advprog.papikos.wishlist.observer;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;
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
    private final Map<Long, List<NotificationObserver>> observersByHouseId = new HashMap<>();

    @Override
    public void registerObserver(Long houseId, NotificationObserver observer) {
        observersByHouseId
                .computeIfAbsent(houseId, k -> new ArrayList<>())
                .add(observer);
    }

    @Override
    public void removeObserver(Long houseId, NotificationObserver observer) {
        List<NotificationObserver> observers = observersByHouseId.get(houseId);
        if (observers != null) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers(Long houseId, Long ownerId) {
        Notification notification = Notification.builder()
                .ownerId(ownerId)
                .message("House with ID " + houseId + " has been updated.")
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();
        notificationRepo.save(notification);

        // Optionally notify observers if needed
        List<NotificationObserver> observers = observersByHouseId.get(houseId);
        if (observers != null) {
            for (NotificationObserver observer : observers) {
                observer.update(houseId);
            }
        }
    }
}
