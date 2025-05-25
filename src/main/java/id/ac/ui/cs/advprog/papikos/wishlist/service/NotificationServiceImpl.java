package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.NotificationRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.WishlistItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepo;
    private final HouseRepository houseRepo;
    private final WishlistItemRepository wishlistRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByReceiver(Long receiverId) {
        return notificationRepo.findByReceiverId(receiverId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsBySender(Long senderId) {
        return notificationRepo.findBySenderId(senderId);
    }

    @Async
    @Override
    public CompletableFuture<Void> notifyAvailability(Long houseId) {
        houseRepo.findById(houseId).ifPresent(house -> {
            Long ownerId = house.getOwner() != null
                    ? house.getOwner().getId()
                    : null;

            // <-- use our new repository method here -->
            List<Long> tenantIds = wishlistRepo
                    .findDistinctTenantIdsByHouseId(houseId);

            tenantIds.forEach(tenantId -> {
                var notif = Notification.builder()
                        .receiverId(tenantId)
                        .senderId(ownerId)
                        .message("House " + houseId + " is now available!")
                        .createdAt(LocalDateTime.now())
                        .isRead(false)
                        .build();
                notificationRepo.save(notif);
            });
        });
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void sendToAllUsers(Long senderId, String message) {
        List<User> users = userRepo.findAll();
        users.forEach(user -> {
            var notif = Notification.builder()
                    .receiverId(user.getId())
                    .senderId(senderId)
                    .message(message)
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .build();
            notificationRepo.save(notif);
        });
    }

    @Async
    @Override
    public CompletableFuture<Void> notifyTenantRentalApproved(
            Long senderId, Long receiverId, Long houseId) {
        houseRepo.findById(houseId).ifPresent(house -> {
            var notif = Notification.builder()
                    .senderId(senderId)
                    .receiverId(receiverId)
                    .message("Your rental request for "
                            + house.getName()
                            + " has been approved!")
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .build();
            notificationRepo.save(notif);
        });
        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Override
    public CompletableFuture<Void> notifyTenantRentalRejected(
            Long senderId, Long receiverId, Long houseId) {
        houseRepo.findById(houseId).ifPresent(house -> {
            var notif = Notification.builder()
                    .senderId(senderId)
                    .receiverId(receiverId)
                    .message("Your rental request for "
                            + house.getName()
                            + " has been rejected.")
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .build();
            notificationRepo.save(notif);
        });
        return CompletableFuture.completedFuture(null);
    }
}
