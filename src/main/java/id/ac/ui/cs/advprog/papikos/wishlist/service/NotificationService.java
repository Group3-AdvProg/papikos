package id.ac.ui.cs.advprog.papikos.wishlist.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface NotificationService {
    List<String> getNotificationsByTenant(Long tenantId);
    List<String> getNotificationsByOwner(Long ownerId);
    CompletableFuture<Void> notifyAvailability(Long houseId);
}
