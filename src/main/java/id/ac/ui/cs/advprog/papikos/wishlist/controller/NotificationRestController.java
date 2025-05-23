package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationRestController {

    private final NotificationService notificationService;

    public NotificationRestController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // GET notifications for a tenant
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<String>> getTenantNotifications(@PathVariable Long tenantId) {
        List<String> notifications = notificationService.getNotificationsByTenant(tenantId);
        if (notifications.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content if no notifications
        }
        return ResponseEntity.ok(notifications);
    }

    // GET notifications for an owner
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<String>> getOwnerNotifications(@PathVariable Long ownerId) {
        List<String> notifications = notificationService.getNotificationsByOwner(ownerId);
        if (notifications.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(notifications);
    }

    // Trigger availability notification
    @PostMapping("/house/{houseId}/trigger")
    public ResponseEntity<Void> triggerHouseNotification(@PathVariable Long houseId) {
        notificationService.notifyAvailability(houseId);
        return ResponseEntity.noContent().build(); // 204 No Content to indicate success
    }
}
