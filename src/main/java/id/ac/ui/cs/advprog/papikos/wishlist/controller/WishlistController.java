package id.ac.ui.cs.advprog.papikos.wishlist.controller;

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
        return ResponseEntity.ok(wishlistService.getWishlistByTenant(userId));
    }
}
