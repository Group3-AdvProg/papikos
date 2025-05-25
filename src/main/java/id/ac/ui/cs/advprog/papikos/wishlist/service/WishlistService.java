package id.ac.ui.cs.advprog.papikos.wishlist.service;

import java.util.List;

public interface WishlistService {
    void addToWishlist(Long userId, Long houseId);
    void removeFromWishlist(Long userId, Long houseId);
    List<Long> getWishlistByTenant(Long tenantId);
}
