package id.ac.ui.cs.advprog.papikos.wishlist.entity;

import java.util.ArrayList;
import java.util.List;

public class Tenant {
    private String id;
    private String name;
    private List<String> wishlist = new ArrayList<>();
    private List<String> notifications = new ArrayList<>();

    public Tenant(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addToWishlist(String propertyId) {
        wishlist.add(propertyId);
    }

    public void receiveNotification(String message) {
        notifications.add(message);
        System.out.println("🔔 Notification for " + name + ": " + message);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getWishlist() {
        return wishlist;
    }

    public List<String> getNotifications() {
        return notifications;
    }
}
