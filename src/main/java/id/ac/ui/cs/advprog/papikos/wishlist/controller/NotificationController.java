package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final WishlistService wishlistService;

    @GetMapping("/{tenantId}")
    public ResponseEntity<List<String>> getNotifications(@PathVariable String tenantId) {
        List<String> notifications = wishlistService.getNotificationsByTenant(tenantId);
        return ResponseEntity.ok(notifications);
    }
}
