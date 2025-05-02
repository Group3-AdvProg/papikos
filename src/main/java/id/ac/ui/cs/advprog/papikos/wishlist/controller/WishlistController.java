package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.DTO.WishlistRequest;
import id.ac.ui.cs.advprog.papikos.wishlist.service.WishlistService;
import id.ac.ui.cs.advprog.papikos.house.model.House;
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
    public ResponseEntity<String> addToWishlist(@RequestBody WishlistRequest request) {
        wishlistService.addToWishlist(request.getTenantId(), request.getHouseId());
        return ResponseEntity.ok("Added to wishlist.");
    }


    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFromWishlist(@RequestBody WishlistRequest request) {
        wishlistService.removeFromWishlist(request.getTenantId(), request.getRoomType());
        return ResponseEntity.ok("Removed from wishlist.");
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<List<String>> getWishlist(@PathVariable String tenantId) {
        List<String> wishlist = wishlistService.getWishlistByTenant(tenantId);
        return ResponseEntity.ok(wishlist);
    }
}
