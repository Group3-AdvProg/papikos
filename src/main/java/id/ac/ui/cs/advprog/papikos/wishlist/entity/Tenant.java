package id.ac.ui.cs.advprog.papikos.wishlist.entity;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Tenant {

    private final String id;
    private final String name;
    private final List<String> wishlist = new ArrayList<>();
    private final List<String> notifications = new ArrayList<>();

    public Tenant(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addToWishlist(String propertyId) {
        if (!wishlist.contains(propertyId)) {
            wishlist.add(propertyId);
        }
    }

    public void removeFromWishlist(String propertyId) {
        wishlist.remove(propertyId);
    }

    public void receiveNotification(String message) {
        notifications.add(message);
        System.out.println("Notification for " + name + ": " + message);
    }
}
