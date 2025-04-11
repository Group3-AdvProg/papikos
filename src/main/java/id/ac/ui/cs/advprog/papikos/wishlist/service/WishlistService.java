package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.Tenant;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WishlistService {

    private Map<String, Tenant> tenants = new HashMap<>();

    public void registerTenant(String id, String name) {
        if (!tenants.containsKey(id)) {
            tenants.put(id, new Tenant(id, name));
        }
    }

    public void addToWishlist(String tenantId, String propertyId) {
        Tenant tenant = tenants.get(tenantId);
        if (tenant != null) {
            tenant.addToWishlist(propertyId);
        }
    }

    public void notifyAvailability(String propertyId) {
        for (Tenant tenant : tenants.values()) {
            if (tenant.getWishlist().contains(propertyId)) {
                tenant.receiveNotification("Property " + propertyId + " is now available!");
            }
        }
    }

    // Optional: for viewing/testing
    public Tenant getTenant(String tenantId) {
        return tenants.get(tenantId);
    }
}
