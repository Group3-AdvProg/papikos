package id.ac.ui.cs.advprog.papikos.wishlist.service;

import java.util.List;

public interface NotificationService {
    List<String> getNotificationsByTenant(Long tenantId);
    List<String> getNotificationsByOwner(Long ownerId);
    void notifyAvailability(Long houseId);
}
