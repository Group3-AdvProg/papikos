package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<String>> getTenantNotifications(@PathVariable Long tenantId) {
        var notifications = notificationService.getNotificationsByTenant(tenantId);
        if (notifications.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<String>> getOwnerNotifications(@PathVariable Long ownerId) {
        var notifications = notificationService.getNotificationsByOwner(ownerId);
        if (notifications.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/house/{houseId}/trigger")
    public ResponseEntity<Void> triggerHouseNotification(@PathVariable Long houseId) {
        notificationService.notifyAvailability(houseId);
        return ResponseEntity.noContent().build();
    }
}
