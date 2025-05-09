package id.ac.ui.cs.advprog.papikos.wishlist.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TenantTest {

    private Tenant tenant;

    @BeforeEach
    void setUp() {
        tenant = new Tenant("tenant123", "Ayu");
    }

    @Test
    void testAddToWishlist() {
        tenant.addToWishlist(1L);
        List<Long> wishlist = tenant.getWishlist();
        assertEquals(1, wishlist.size());
        assertEquals(1L, wishlist.get(0));
    }

    @Test
    void testRemoveFromWishlist() {
        tenant.addToWishlist(1L);
        tenant.removeFromWishlist(1L);
        assertTrue(tenant.getWishlist().isEmpty());
    }

    @Test
    void testReceiveNotification() {
        tenant.receiveNotification("Room available!");
        List<String> notifications = tenant.getNotifications();
        assertEquals(1, notifications.size());
        assertEquals("Room available!", notifications.get(0));
    }

    @Test
    void testGetId() {
        assertEquals("tenant123", tenant.getId());
    }

    @Test
    void testAddDuplicateWishlistItem() {
        tenant.addToWishlist(1L);
        tenant.addToWishlist(1L);  // Add again
        assertEquals(1, tenant.getWishlist().size(), "Duplicate wishlist items should not be added.");
    }

    @Test
    void testRemoveNonExistentWishlistItem() {
        tenant.addToWishlist(1L);
        tenant.removeFromWishlist(2L); // Not added
        assertEquals(1, tenant.getWishlist().size(), "Removing nonexistent item shouldn't affect wishlist.");
    }

    @Test
    void testMultipleNotifications() {
        tenant.receiveNotification("Notif 1");
        tenant.receiveNotification("Notif 2");
        List<String> notifications = tenant.getNotifications();
        assertEquals(2, notifications.size());
        assertTrue(notifications.contains("Notif 1"));
        assertTrue(notifications.contains("Notif 2"));
    }

}
