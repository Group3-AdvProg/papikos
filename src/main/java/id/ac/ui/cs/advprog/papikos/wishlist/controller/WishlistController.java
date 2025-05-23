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
            @RequestParam Long tenantId,
            @RequestParam Long houseId
    ) {
        wishlistService.addToWishlist(tenantId, houseId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFromWishlist(
            @RequestParam Long tenantId,
            @RequestParam Long houseId
    ) {
        wishlistService.removeFromWishlist(tenantId, houseId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Long>> getWishlistByTenant(
            @PathVariable Long tenantId
    ) {
        return ResponseEntity.ok(wishlistService.getWishlistByTenant(tenantId));
    }

    @GetMapping("/notifications/tenant/{tenantId}")
    public ResponseEntity<List<String>> getNotificationsByTenant(
            @PathVariable Long tenantId
    ) {
        return ResponseEntity.ok(wishlistService.getNotificationsByTenant(tenantId));
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
