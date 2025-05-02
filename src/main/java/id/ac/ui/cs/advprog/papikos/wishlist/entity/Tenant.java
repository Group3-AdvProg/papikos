package id.ac.ui.cs.advprog.papikos.wishlist.entity;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Tenant {

    private final String id;
    private final String name;
    private List<Long> wishlist = new ArrayList<>();
    private List<String> notifications = new ArrayList<>();

    public Tenant(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addToWishlist(Long houseId) {
        if (!wishlist.contains(houseId)) {
            wishlist.add(houseId);
        }
    }

    public void removeFromWishlist(Long houseId) {
        wishlist.remove(houseId);
    }

    public void receiveNotification(String message) {
        notifications.add(message);
        System.out.println("Notification for " + name + ": " + message);
    }
}
