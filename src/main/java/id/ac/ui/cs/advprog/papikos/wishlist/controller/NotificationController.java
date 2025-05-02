package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.service.WishlistService;
import id.ac.ui.cs.advprog.papikos.house.model.House;
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
    public ResponseEntity<List<String>> getNotifications(@PathVariable Long tenantId) {
        List<String> notifications = wishlistService.getNotificationsByTenant(String.valueOf(tenantId));;
        return ResponseEntity.ok(notifications);
    }
}
