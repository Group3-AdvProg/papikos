package id.ac.ui.cs.advprog.papikos.wishlist.observer;

public interface WishlistNotifier {
    void registerObserver(String roomType, NotificationObserver observer);
    void removeObserver(String roomType, NotificationObserver observer);
    void notifyObservers(String roomType);
}
