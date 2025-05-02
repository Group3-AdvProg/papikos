package id.ac.ui.cs.advprog.papikos.wishlist.observer;
import id.ac.ui.cs.advprog.papikos.house.model.House;


import java.util.*;

public class WishlistNotifierImpl implements WishlistNotifier {

    private final Map<String, List<NotificationObserver>> observersByRoomType = new HashMap<>();

    @Override
    public void registerObserver(String roomType, NotificationObserver observer) {
        observersByRoomType
                .computeIfAbsent(roomType, k -> new ArrayList<>())
                .add(observer);
    }

    @Override
    public void removeObserver(String roomType, NotificationObserver observer) {
        List<NotificationObserver> observers = observersByRoomType.get(roomType);
        if (observers != null) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers(String roomType) {
        List<NotificationObserver> observers = observersByRoomType.get(roomType);
        if (observers != null) {
            for (NotificationObserver observer : observers) {
                observer.update(roomType);
            }
        }
    }
}
