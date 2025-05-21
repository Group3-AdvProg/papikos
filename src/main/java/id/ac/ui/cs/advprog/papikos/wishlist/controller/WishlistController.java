package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.DTO.WishlistRequest;
import id.ac.ui.cs.advprog.papikos.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    // For test compatibility: POST /api/wishlist/add
    @PostMapping("/add")
    public ResponseEntity<Void> addToWishlistTest(@RequestBody WishlistRequest request) {
        wishlistService.addToWishlist(request.getTenantId(), request.getHouseId());
        return ResponseEntity.status(201).build();
    }

    // For test compatibility: GET /api/wishlist/tenant/{tenantId}
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Long>> getWishlistByTenant(@PathVariable Long tenantId) {
        List<Long> wishlist = wishlistService.getWishlistByTenant(tenantId);
        return ResponseEntity.ok(wishlist);
    }

    // For test compatibility: GET /api/wishlist/notifications/{tenantId}
    @GetMapping("/notifications/{tenantId}")
    public ResponseEntity<List<String>> getNotificationsByTenant(@PathVariable Long tenantId) {
        List<String> notifications = wishlistService.getNotificationsByTenant(tenantId);
        return ResponseEntity.ok(notifications);
    }

    // General RESTful endpoints (optional, for completeness)
    @PostMapping
    public ResponseEntity<String> addToWishlist(@RequestBody WishlistRequest request) {
        wishlistService.addToWishlist(request.getTenantId(), request.getHouseId());
        return ResponseEntity.status(201).body("Added to wishlist.");
    }

    @DeleteMapping
    public ResponseEntity<String> removeFromWishlist(@RequestBody WishlistRequest request) {
        wishlistService.removeFromWishlist(request.getTenantId(), request.getHouseId());
        return ResponseEntity.status(200).body("Removed from wishlist.");
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<List<Long>> getWishlist(@PathVariable Long tenantId) {
        List<Long> wishlist = wishlistService.getWishlistByTenant(tenantId);
        return ResponseEntity.ok(wishlist);
    }
}
