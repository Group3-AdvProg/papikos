package id.ac.ui.cs.advprog.papikos.RentalTest.Service;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.rental.repository.RentalRepository;
import id.ac.ui.cs.advprog.papikos.house.rental.service.RentalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RentalServiceImplTest {

    private RentalRepository repo;
    private RentalServiceImpl service;
    private final Long id = 1L;

    @BeforeEach
    void init() {
        repo = Mockito.mock(RentalRepository.class);
        service = new RentalServiceImpl(repo);
    }

    private Rental baseRental(String houseName, Long houseId) {
        Rental r = new Rental();
        r.setId(id);

        House house = new House();
        house.setId(houseId);
        house.setName(houseName);
        r.setHouse(house);

        r.setFullName("Foo");
        r.setPhoneNumber("081234");
        r.setCheckInDate(LocalDate.of(2025, 1, 1));
        r.setDurationInMonths(3);
        r.setApproved(false);
        r.setTotalPrice(1000000);
        r.setPaid(false);
        return r;
    }

    // === CRUD SYNC ===

    @Test
    void create_and_getAll_and_getById_and_delete() {
        Rental r = baseRental("H1", 11L);

        when(repo.save(r)).thenReturn(r);
        Rental created = service.createRental(r);
        assertSame(r, created);
        verify(repo).save(r);

        when(repo.findAll()).thenReturn(List.of(r));
        List<Rental> all = service.getAllRentals();
        assertEquals(1, all.size());
        assertEquals(r, all.get(0));

        when(repo.findById(id)).thenReturn(Optional.of(r));
        Optional<Rental> opt = service.getRentalById(id);
        assertTrue(opt.isPresent());
        assertEquals("H1", opt.get().getHouse().getName());

        doNothing().when(repo).deleteById(id);
        assertDoesNotThrow(() -> service.deleteRental(id));
        verify(repo).deleteById(id);
    }

    @Test
    void update_without_changing_tenant() {
        Rental stored = baseRental("OldHouse", 22L);
        Rental details = baseRental("NewHouse", 33L);

        when(repo.findById(id)).thenReturn(Optional.of(stored));
        when(repo.save(any(Rental.class))).thenAnswer(inv -> inv.getArgument(0));

        Rental updated = service.updateRental(id, details);

        assertEquals("NewHouse", updated.getHouse().getName());
        assertEquals(3, updated.getDurationInMonths());
        assertEquals("Foo", updated.getFullName());
        verify(repo).findById(id);
        verify(repo).save(stored);
    }

    @Test
    void update_with_new_tenant() {
        Rental stored = baseRental("SomeKos", 77L);
        User t = new User();
        t.setId(55L);
        t.setFullName("Bar");
        t.setPhoneNumber("082");
        t.setRole("ROLE_TENANT");
        stored.setTenant(null);

        Rental details = baseRental("SomeKos", 77L);
        details.setTenant(t);

        when(repo.findById(id)).thenReturn(Optional.of(stored));
        when(repo.save(any(Rental.class))).thenAnswer(inv -> inv.getArgument(0));

        Rental updated = service.updateRental(id, details);

        assertSame(t, updated.getTenant());
        verify(repo).findById(id);
        verify(repo).save(stored);
    }

    @Test
    void update_throws_when_not_found() {
        when(repo.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.updateRental(id, baseRental("FailKos", 999L));
        });

        assertTrue(ex.getMessage().contains("rental not found"));
        verify(repo).findById(id);
        verify(repo, never()).save(any());
    }

    @Test
    void testUpdateRentalSync_ShouldSkipSetTenantWhenNull() {
        Rental existing = baseRental("Kos Lama", 88L);
        existing.setTenant(new User());

        Rental details = baseRental("Kos Baru", 99L);
        details.setTenant(null); // Simulate skip tenant

        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Rental result = service.updateRental(id, details);

        assertNotNull(result.getTenant());
        verify(repo).save(existing);
    }

    @Test
    void testUpdateRentalSync_TenantNotNull_ShouldSetTenant() {
        Rental stored = baseRental("Kos A", 10L);
        stored.setTenant(null);

        User newTenant = new User();
        newTenant.setId(999L);

        Rental details = baseRental("Kos A", 10L);
        details.setTenant(newTenant);

        when(repo.findById(id)).thenReturn(Optional.of(stored));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Rental result = service.updateRental(id, details);

        assertEquals(newTenant, result.getTenant());
    }

    @Test
    void testUpdateRentalSync_TenantNull_ShouldSkipSetTenant() {
        Rental stored = baseRental("Kos B", 11L);
        User existingTenant = new User();
        existingTenant.setId(888L);
        stored.setTenant(existingTenant);

        Rental details = baseRental("Kos B", 11L);
        details.setTenant(null);

        when(repo.findById(id)).thenReturn(Optional.of(stored));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Rental result = service.updateRental(id, details);

        assertEquals(existingTenant, result.getTenant());
    }

    // === ASYNC ===

    @Test
    void createRentalAsync_shouldReturnCreatedRental() throws Exception {
        Rental r = baseRental("AsyncKos", 99L);
        when(repo.save(r)).thenReturn(r);

        Rental result = service.createRentalAsync(r).get();

        assertSame(r, result);
        verify(repo).save(r);
    }

    @Test
    void getAllRentalsAsync_shouldReturnList() throws Exception {
        Rental r = baseRental("KosA", 10L);
        when(repo.findAll()).thenReturn(List.of(r));

        List<Rental> result = service.getAllRentalsAsync().get();

        assertEquals(1, result.size());
        assertEquals("KosA", result.get(0).getHouse().getName());
        verify(repo).findAll();
    }

    @Test
    void getRentalByIdAsync_shouldReturnOptional() throws Exception {
        Rental r = baseRental("KosB", 20L);
        when(repo.findById(id)).thenReturn(Optional.of(r));

        Optional<Rental> result = service.getRentalByIdAsync(id).get();

        assertTrue(result.isPresent());
        assertEquals("KosB", result.get().getHouse().getName());
        verify(repo).findById(id);
    }

    @Test
    void updateRentalAsync_shouldUpdateRental() throws Exception {
        Rental stored = baseRental("OldAsyncKos", 30L);
        Rental details = baseRental("NewAsyncKos", 40L);

        when(repo.findById(id)).thenReturn(Optional.of(stored));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Rental result = service.updateRentalAsync(id, details).get();

        assertEquals("NewAsyncKos", result.getHouse().getName());
        verify(repo).save(stored);
    }

    @Test
    void updateRentalAsync_shouldSkipTenantWhenNull() throws Exception {
        Rental stored = baseRental("KosSkip", 88L);
        stored.setTenant(new User());

        Rental details = baseRental("KosSkip", 88L);
        details.setTenant(null);

        when(repo.findById(id)).thenReturn(Optional.of(stored));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Rental result = service.updateRentalAsync(id, details).get();

        assertNotNull(result.getTenant());
        verify(repo).save(stored);
    }

    @Test
    void updateRentalAsync_shouldThrowIfNotFound() {
        when(repo.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.updateRentalAsync(id, baseRental("FailAsyncKos", 55L));
        });

        assertTrue(ex.getMessage().contains("rental not found"));
    }

    @Test
    void testUpdateRentalAsync_TenantNotNull_ShouldSetTenant() throws Exception {
        Rental stored = baseRental("Async A", 20L);
        stored.setTenant(null);

        User newTenant = new User();
        newTenant.setId(777L);

        Rental details = baseRental("Async A", 20L);
        details.setTenant(newTenant);

        when(repo.findById(id)).thenReturn(Optional.of(stored));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Rental result = service.updateRentalAsync(id, details).get();

        assertEquals(newTenant, result.getTenant());
    }

    @Test
    void testUpdateRentalAsync_TenantNull_ShouldSkipSetTenant() throws Exception {
        Rental stored = baseRental("Async B", 21L);
        User existingTenant = new User();
        existingTenant.setId(666L);
        stored.setTenant(existingTenant);

        Rental details = baseRental("Async B", 21L);
        details.setTenant(null);

        when(repo.findById(id)).thenReturn(Optional.of(stored));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Rental result = service.updateRentalAsync(id, details).get();

        assertEquals(existingTenant, result.getTenant());
    }

    @Test
    void testUpdateRentalAsync_ShouldSkipSetTenantWhenNull() throws Exception {
        Rental existing = baseRental("Async Kos", 77L);
        existing.setTenant(new User());

        Rental details = baseRental("Async Kos", 77L);
        details.setTenant(null);

        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Rental result = service.updateRentalAsync(id, details).get();

        assertNotNull(result.getTenant());
        verify(repo).save(existing);
    }

    @Test
    void deleteRentalAsync_shouldInvokeDelete() throws Exception {
        doNothing().when(repo).deleteById(id);

        assertDoesNotThrow(() -> service.deleteRentalAsync(id).get());

        verify(repo).deleteById(id);
    }

    // === CACHE BEHAVIOR ===

    @Test
    void getAllRentals_shouldReturnCachedOnSubsequentCalls() {
        Rental r = baseRental("CacheKos", 123L);
        List<Rental> initial = List.of(r);

        // first call: repo.findAll()
        when(repo.findAll()).thenReturn(initial);
        List<Rental> first = service.getAllRentals();
        assertEquals(1, first.size());
        assertSame(initial, first);

        // alter stub and call again
        when(repo.findAll()).thenReturn(Collections.emptyList());
        List<Rental> second = service.getAllRentals();
        assertSame(first, second, "Expected cached list on second call");

        // verify repository only called once
        verify(repo, times(1)).findAll();
    }
    @Test
    void updateRentalCache_shouldInsertIntoCacheAndInvalidateListCache() throws Exception {
        Rental rental = baseRental("InsertedViaCache", 404L);
        rental.setId(404L);

        service.updateRentalCache(rental);

        // Inspect internal cache directly instead of relying on getRentalById()
        var cacheField = RentalServiceImpl.class.getDeclaredField("rentalCache");
        cacheField.setAccessible(true);
        var cache = (Map<Long, Rental>) cacheField.get(service);

        assertTrue(cache.containsKey(404L), "rentalCache should contain inserted ID");
        assertEquals(404L, cache.get(404L).getId());

        var listCacheField = RentalServiceImpl.class.getDeclaredField("rentalsCache");
        listCacheField.setAccessible(true);
        assertNull(listCacheField.get(service), "rentalsCache should be invalidated");
    }

}
