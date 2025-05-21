package id.ac.ui.cs.advprog.papikos.wishlist.service;

import java.util.List;

public interface WishlistService {
    void registerTenant(Long tenantId, String tenantName);
    void addToWishlist(Long tenantId, Long houseId);
    void removeFromWishlist(Long tenantId, Long houseId);
    List<Long> getWishlistByTenant(Long tenantId);
    List<String> getNotificationsByTenant(Long tenantId);
    List<String> getNotificationsByOwner(Long ownerId);
    void notifyAvailability(Long houseId);
}
