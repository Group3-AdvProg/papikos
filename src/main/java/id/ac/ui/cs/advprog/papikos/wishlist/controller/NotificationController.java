package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.Notification;
import id.ac.ui.cs.advprog.papikos.wishlist.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<List<Notification>> getNotificationsByReceiver(@PathVariable Long receiverId) {
        logger.info("Fetching notifications for receiver ID: {}", receiverId);
        var notifications = notificationService.getNotificationsByReceiver(receiverId);
        if (notifications.isEmpty()) {
            logger.info("No notifications found for receiver ID: {}", receiverId);
            return ResponseEntity.noContent().build();
        }
        logger.info("Returning {} notifications for receiver ID: {}", notifications.size(), receiverId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<Notification>> getNotificationsBySender(@PathVariable Long senderId) {
        logger.info("Fetching notifications sent by sender ID: {}", senderId);
        var notifications = notificationService.getNotificationsBySender(senderId);
        if (notifications.isEmpty()) {
            logger.info("No notifications found for sender ID: {}", senderId);
            return ResponseEntity.noContent().build();
        }
        logger.info("Returning {} notifications from sender ID: {}", notifications.size(), senderId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/house/{houseId}/trigger")
    public ResponseEntity<Void> triggerHouseNotification(@PathVariable Long houseId) {
        logger.info("Triggering house availability notification for house ID: {}", houseId);
        notificationService.notifyAvailability(houseId);
        logger.info("Notification triggered for house ID: {}", houseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> sendGlobalNotification(@RequestParam Long senderId, @RequestParam String message) {
        logger.info("Broadcasting global message from sender ID: {} - Message: {}", senderId, message);
        notificationService.sendToAllUsers(senderId, message);
        logger.info("Global message sent from sender ID: {}", senderId);
        return ResponseEntity.ok().build();
    }
}
