package id.ac.ui.cs.advprog.papikos.wishlist.repository;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverId(Long receiverId);
    List<Notification> findBySenderId(Long senderId);
}
