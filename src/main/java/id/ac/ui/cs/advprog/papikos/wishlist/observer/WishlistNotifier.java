package id.ac.ui.cs.advprog.papikos.wishlist.observer;

public interface WishlistNotifier {
    void registerObserver(Long houseId, NotificationObserver observer);
    void removeObserver(Long houseId, NotificationObserver observer);
    void notifyObservers(Long houseId);
}
