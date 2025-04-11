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
        tenant.addToWishlist("Kamar AC");
        List<String> wishlist = tenant.getWishlist();
        assertEquals(1, wishlist.size());
        assertEquals("Kamar AC", wishlist.get(0));
    }

    @Test
    void testRemoveFromWishlist() {
        tenant.addToWishlist("Kamar AC");
        tenant.removeFromWishlist("Kamar AC");
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
}
