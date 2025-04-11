package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.Tenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WishlistServiceImplTest {

    private WishlistServiceImpl wishlistService;

    @BeforeEach
    void setUp() {
        wishlistService = new WishlistServiceImpl();
    }

    @Test
    void testRegisterTenantAndRetrieve() {
        Tenant tenant = new Tenant("tenant123", "Ayu");
        wishlistService.registerTenant(tenant);
        Tenant result = wishlistService.getTenant("tenant123");
        assertNotNull(result);
        assertEquals("tenant123", result.getId());
    }

    @Test
    void testAddToWishlist() {
        Tenant tenant = new Tenant("tenant123", "Ayu");
        wishlistService.registerTenant(tenant);

        wishlistService.addToWishlist("tenant123", "Kamar AC + WiFi");
        List<String> wishlist = wishlistService.getWishlistByTenant("tenant123");

        assertEquals(1, wishlist.size());
        assertEquals("Kamar AC + WiFi", wishlist.get(0));
    }

    @Test
    void testRemoveFromWishlist() {
        Tenant tenant = new Tenant("tenant123", "Ayu");
        wishlistService.registerTenant(tenant);

        wishlistService.addToWishlist("tenant123", "Kamar AC + WiFi");
        wishlistService.removeFromWishlist("tenant123", "Kamar AC + WiFi");

        List<String> wishlist = wishlistService.getWishlistByTenant("tenant123");
        assertTrue(wishlist.isEmpty());
    }

    @Test
    void testNotifyAvailability() {
        Tenant tenant = new Tenant("tenant123", "Ayu");
        wishlistService.registerTenant(tenant);

        wishlistService.addToWishlist("tenant123", "Kamar AC + WiFi");
        wishlistService.notifyAvailability("Kamar AC + WiFi");

        List<String> notifications = wishlistService.getNotificationsByTenant("tenant123");

        assertEquals(1, notifications.size());
        assertEquals("Room type Kamar AC + WiFi is now available!", notifications.get(0));
    }
}
