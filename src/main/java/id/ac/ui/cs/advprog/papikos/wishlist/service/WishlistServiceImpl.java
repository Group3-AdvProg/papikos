package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.Tenant;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final Map<String, Tenant> tenants = new HashMap<>();

    public void registerTenant(Tenant tenant) {
        tenants.put(tenant.getId(), tenant);
    }

    public Tenant getTenant(String id) {
        return tenants.get(id);
    }

    @Override
    public void registerTenant(String id, String name) {

    }

    @Override
    public void addToWishlist(String tenantId, String roomType) {
        Tenant tenant = tenants.get(tenantId);
        if (tenant != null) {
            tenant.addToWishlist(roomType);
        }
    }

    @Override
    public void removeFromWishlist(String tenantId, String roomType) {
        Tenant tenant = tenants.get(tenantId);
        if (tenant != null) {
            tenant.removeFromWishlist(roomType);
        }
    }

    @Override
    public List<String> getWishlistByTenant(String tenantId) {
        Tenant tenant = tenants.get(tenantId);
        return tenant != null ? tenant.getWishlist() : Collections.emptyList();
    }

    @Override
    public List<String> getNotificationsByTenant(String tenantId) {
        Tenant tenant = tenants.get(tenantId);
        return tenant != null ? tenant.getNotifications() : Collections.emptyList();
    }

    @Override
    public void notifyAvailability(String roomType) {
        for (Tenant tenant : tenants.values()) {
            if (tenant.getWishlist().contains(roomType)) {
                tenant.receiveNotification("Room type " + roomType + " is now available!");
            }
        }
    }
}
