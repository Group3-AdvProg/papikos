package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.WishlistItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistRepo;
    private final HouseRepository houseRepo;

    @Override
    public void addToWishlist(Long tenantId, Long houseId) {
        if (wishlistRepo.findByTenantIdAndHouseId(tenantId, houseId) == null) {
            wishlistRepo.save(WishlistItem.builder()
                    .tenantId(tenantId)
                    .houseId(houseId)
                    .build());
        }
    }

    @Override
    public void removeFromWishlist(Long tenantId, Long houseId) {
        var item = wishlistRepo.findByTenantIdAndHouseId(tenantId, houseId);
        if (item != null) wishlistRepo.delete(item);
    }

    @Override
    public List<Long> getWishlistByTenant(Long tenantId) {
        return wishlistRepo.findByTenantId(tenantId)
                .stream().map(WishlistItem::getHouseId).toList();
    }
}

