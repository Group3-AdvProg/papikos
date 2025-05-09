package id.ac.ui.cs.advprog.papikos.wishlist.observer;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import id.ac.ui.cs.advprog.papikos.house.model.House;

public class WishlistNotifierImpl implements WishlistNotifier {

    private final Map<Long, List<NotificationObserver>> observersByHouseId = new HashMap<>();

    @Override
    public void registerObserver(Long houseId, NotificationObserver observer) {
        observersByHouseId
                .computeIfAbsent(houseId, k -> new ArrayList<>())
                .add(observer);
    }

    @Override
    public void removeObserver(Long houseId, NotificationObserver observer) {
        List<NotificationObserver> observers = observersByHouseId.get(houseId);
        if (observers != null) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers(Long houseId) {
        List<NotificationObserver> observers = observersByHouseId.get(houseId);
        if (observers != null) {
            for (NotificationObserver observer : observers) {
                observer.update(houseId);
            }
        }
    }
}
