package id.ac.ui.cs.advprog.papikos.wishlist.service;

import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import id.ac.ui.cs.advprog.papikos.wishlist.repository.WishlistItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceImplTest {

    @Mock
    private WishlistItemRepository wishlistRepo;

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    private WishlistItem item;

    @BeforeEach
    void setUp() {
        item = WishlistItem.builder()
                .id(1L)
                .tenantId(1L)
                .houseId(100L)
                .build();
    }

    @Test
    void testAddToWishlist_Success() {
        when(wishlistRepo.findByTenantIdAndHouseId(1L, 100L)).thenReturn(null);
        wishlistService.addToWishlist(1L, 100L);

        verify(wishlistRepo, times(1)).save(any(WishlistItem.class));
    }

    @Test
    void testRemoveFromWishlist() {
        when(wishlistRepo.findByTenantIdAndHouseId(1L, 100L)).thenReturn(item);
        wishlistService.removeFromWishlist(1L, 100L);

        verify(wishlistRepo, times(1)).delete(item);
    }

    @Test
    void testGetWishlistByTenant() {
        WishlistItem item1 = WishlistItem.builder().houseId(101L).build();
        WishlistItem item2 = WishlistItem.builder().houseId(102L).build();

        when(wishlistRepo.findByTenantId(1L)).thenReturn(List.of(item1, item2));

        List<Long> result = wishlistService.getWishlistByTenant(1L);
        assertEquals(List.of(101L, 102L), result);
    }

    @Test
    void testAddToWishlist_ItemAlreadyExists() {
        when(wishlistRepo.findByTenantIdAndHouseId(1L, 100L)).thenReturn(item);
        wishlistService.addToWishlist(1L, 100L);

        verify(wishlistRepo, never()).save(any(WishlistItem.class)); // nothing should be saved
    }

    @Test
    void testRemoveFromWishlist_ItemDoesNotExist() {
        when(wishlistRepo.findByTenantIdAndHouseId(1L, 100L)).thenReturn(null);
        wishlistService.removeFromWishlist(1L, 100L);

        verify(wishlistRepo, never()).delete(any());
    }

}
