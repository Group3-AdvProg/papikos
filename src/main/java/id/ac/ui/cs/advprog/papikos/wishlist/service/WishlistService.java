package id.ac.ui.cs.advprog.papikos.wishlist.service;

import java.util.List;

public interface WishlistService {
    void registerTenant(String tenantId, String tenantName);
    void addToWishlist(String tenantId, Long houseId);
    void removeFromWishlist(String tenantId, Long houseId);
    List<Long> getWishlistByTenant(String tenantId);
    List<String> getNotificationsByTenant(String tenantId);
    void notifyAvailability(Long houseId);
}
