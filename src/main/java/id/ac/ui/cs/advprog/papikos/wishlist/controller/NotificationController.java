package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<List<String>> getNotificationsByReceiver(@PathVariable Long receiverId) {
        var notifications = notificationService.getNotificationsByReceiver(receiverId);
        if (notifications.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<String>> getNotificationsBySender(@PathVariable Long senderId) {
        var notifications = notificationService.getNotificationsBySender(senderId);
        if (notifications.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/house/{houseId}/trigger")
    public ResponseEntity<Void> triggerHouseNotification(@PathVariable Long houseId) {
        notificationService.notifyAvailability(houseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> sendGlobalNotification(@RequestParam Long senderId, @RequestParam String message) {
        notificationService.sendToAllUsers(senderId, message);
        return ResponseEntity.ok().build();
    }

}
