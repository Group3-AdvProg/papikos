package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
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
    private final UserRepository userRepo;

    @Override
    public List<Notification> getNotificationsByReceiver(Long receiverId) {
        return notificationRepo.findByReceiverId(receiverId);
    }

    @Override
    public List<Notification> getNotificationsBySender(Long senderId) {
        return notificationRepo.findBySenderId(senderId);
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
                        .receiverId(tenantId)
                        .senderId(ownerId)
                        .message("House " + house.getName() + " has new available room!")
                        .createdAt(LocalDateTime.now())
                        .isRead(false)
                        .build();
                notificationRepo.save(notif);
            }
        });
        return CompletableFuture.completedFuture(null);
    }

    public void sendToAllUsers(Long senderId, String message) {
        List<User> users = userRepo.findAll();
        for (User user : users) {
            Notification notif = Notification.builder()
                    .receiverId(user.getId())
                    .senderId(senderId)
                    .message(message)
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .build();
            notificationRepo.save(notif);
        }
    }

    @Override
    @Async
    public CompletableFuture<Void> notifyTenantRentalApproved(Long senderId, Long receiverId, Long houseId) {
        houseRepo.findById(houseId).ifPresent(house -> {
            Notification notif = Notification.builder()
                    .senderId(senderId)
                    .receiverId(receiverId)
                    .message("Your rental request for " + house.getName() + " has been approved!")
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .build();
            notificationRepo.save(notif);
        });
        return CompletableFuture.completedFuture(null);
    }


    @Override
    @Async
    public CompletableFuture<Void> notifyTenantRentalRejected(Long senderId, Long receiverId, Long houseId) {
        houseRepo.findById(houseId).ifPresent(house -> {
            Notification notif = Notification.builder()
                    .senderId(senderId)
                    .receiverId(receiverId)
                    .message("Your rental request for " + house.getName() + " has been rejected.")
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .build();
            notificationRepo.save(notif);
        });
        return CompletableFuture.completedFuture(null);
    }
}

