package id.ac.ui.cs.advprog.papikos.wishlist.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface NotificationService {
    List<String> getNotificationsByReceiver(Long receiverId);
    List<String> getNotificationsBySender(Long senderId);
    CompletableFuture<Void> notifyAvailability(Long houseId);
    void sendToAllUsers(Long senderId, String message);
}
