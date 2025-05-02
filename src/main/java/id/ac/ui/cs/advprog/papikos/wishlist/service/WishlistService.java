package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;

import java.util.List;

public interface WishlistService {
    void registerTenant(String id, String name);
    void addToWishlist(String tenantId, String roomType);
    void removeFromWishlist(String tenantId, String roomType);
    List<String> getWishlistByTenant(String tenantId);
    List<Notification> getNotificationsByTenant(String tenantId);
    void notifyAvailability(String roomType);
}
