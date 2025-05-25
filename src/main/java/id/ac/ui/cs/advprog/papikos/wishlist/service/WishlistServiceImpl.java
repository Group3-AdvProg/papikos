package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.WishlistItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistRepo;

    @Override
    public void addToWishlist(Long tenantId, Long houseId) {
        if (!wishlistRepo.existsByTenantIdAndHouseId(tenantId, houseId)) {
            wishlistRepo.save(
                    WishlistItem.builder()
                            .tenantId(tenantId)
                            .houseId(houseId)
                            .build()
            );
        }
    }

    @Override
    public void removeFromWishlist(Long tenantId, Long houseId) {
        wishlistRepo.deleteByTenantIdAndHouseId(tenantId, houseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getWishlistByTenant(Long tenantId) {
        return wishlistRepo.findHouseIdsByTenantId(tenantId);
    }
}
