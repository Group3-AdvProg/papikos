package id.ac.ui.cs.advprog.papikos.wishlist.service;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface NotificationService {
    List<Notification> getNotificationsByReceiver(Long receiverId);
    List<Notification> getNotificationsBySender(Long senderId);
    CompletableFuture<Void> notifyAvailability(Long houseId);
    void sendToAllUsers(Long senderId, String message);
    CompletableFuture<Void> notifyTenantRentalApproved(Long senderId, Long receiverId, Long houseId);
    CompletableFuture<Void> notifyTenantRentalRejected(Long senderId, Long receiverId, Long houseId);
}
