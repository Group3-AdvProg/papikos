package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:63342")
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/add")
    public ResponseEntity<Void> addToWishlist(
            @RequestParam Long userId,
            @RequestParam Long houseId
    ) {
        wishlistService.addToWishlist(userId, houseId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFromWishlist(
            @RequestParam Long userId,
            @RequestParam Long houseId
    ) {
        wishlistService.removeFromWishlist(userId, houseId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Long>> getWishlistByUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(wishlistService.getWishlistByUser(userId));
    }

    @GetMapping("/notifications/user/{userId}")
    public ResponseEntity<List<String>> getNotificationsByUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(wishlistService.getNotificationsByUser(userId));
    }

    @GetMapping("/notifications/owner/{ownerId}")
    public ResponseEntity<List<String>> getNotificationsByOwner(
            @PathVariable Long ownerId
    ) {
        return ResponseEntity.ok(wishlistService.getNotificationsByOwner(ownerId));
    }

    @PostMapping("/notify")
    public ResponseEntity<Void> notifyAvailability(
            @RequestParam Long houseId
    ) {
        wishlistService.notifyAvailability(houseId);
        return ResponseEntity.ok().build();
    }
}
