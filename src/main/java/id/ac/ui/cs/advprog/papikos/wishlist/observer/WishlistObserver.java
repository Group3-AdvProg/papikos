package id.ac.ui.cs.advprog.papikos.wishlist.observer;

public interface WishlistObserver {
    // This method is triggered when the room availability changes
    void update(String roomType);
}
