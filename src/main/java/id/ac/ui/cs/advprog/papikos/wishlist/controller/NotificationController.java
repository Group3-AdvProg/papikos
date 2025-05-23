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

    @GetMapping("/notifications/user/{userId}")
    public ResponseEntity<List<String>> getNotificationsByUser(@PathVariable Long userId) {
        List<String> notifications = wishlistService.getNotificationsByUser(userId);
        return ResponseEntity.ok(notifications);
    }
}
