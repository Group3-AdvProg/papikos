package id.ac.ui.cs.advprog.papikos.wishlist.service;

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
        // given no existing entry
        when(wishlistRepo.existsByTenantIdAndHouseId(1L, 100L)).thenReturn(false);

        // when
        wishlistService.addToWishlist(1L, 100L);

        // then we save exactly one new WishlistItem
        verify(wishlistRepo, times(1))
                .save(argThat(w ->
                        w.getTenantId().equals(1L) &&
                                w.getHouseId().equals(100L)
                ));
    }

    @Test
    void testAddToWishlist_ItemAlreadyExists() {
        // given an existing entry
        when(wishlistRepo.existsByTenantIdAndHouseId(1L, 100L)).thenReturn(true);

        // when
        wishlistService.addToWishlist(1L, 100L);

        // then we never call save()
        verify(wishlistRepo, never()).save(any());
    }

    @Test
    void testRemoveFromWishlist() {
        // when
        wishlistService.removeFromWishlist(1L, 100L);

        // then we delete by tenant+house in one SQL call
        verify(wishlistRepo, times(1))
                .deleteByTenantIdAndHouseId(1L, 100L);
    }

    @Test
    void testGetWishlistByTenant() {
        // given two saved house IDs
        List<Long> expected = List.of(101L, 102L);
        when(wishlistRepo.findHouseIdsByTenantId(1L)).thenReturn(expected);

        // when
        List<Long> actual = wishlistService.getWishlistByTenant(1L);

        // then
        assertEquals(expected, actual);
    }
}
