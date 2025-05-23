package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:63342")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final WishlistService wishlistService;

    @GetMapping("/notifications/tenant/{tenantId}")
    public ResponseEntity<List<String>> getNotificationsByTenant(@PathVariable Long tenantId) {
        List<String> notifications = wishlistService.getNotificationsByTenant(tenantId);
        if (notifications == null || notifications.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(notifications);
    }
}
