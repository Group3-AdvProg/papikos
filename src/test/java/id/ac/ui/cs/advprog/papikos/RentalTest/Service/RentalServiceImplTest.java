package id.ac.ui.cs.advprog.papikos.RentalTest.Service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import id.ac.ui.cs.advprog.papikos.house.Rental.repository.RentalRepository;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.RentalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RentalServiceImplTest {

    @Mock private RentalRepository repo;
    @InjectMocks private RentalServiceImpl service;
    private final Long id = 1L;  //  pakai Long, bukan UUID

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    private Rental baseRental() {
        Rental r = new Rental();
        r.setId(id);
        r.setHouseId("H1");
        r.setFullName("Foo");
        r.setPhoneNumber("081234");
        r.setCheckInDate(LocalDate.of(2025, 1, 1));
        r.setDurationInMonths(3);
        r.setApproved(false);
        return r;
    }

    @Test
    void create_and_getAll_and_getById_and_delete() {
        Rental r = baseRental();

        // createRental
        when(repo.save(r)).thenReturn(r);
        Rental created = service.createRental(r);
        assertSame(r, created);
        verify(repo).save(r);

        // getAllRentals
        when(repo.findAll()).thenReturn(List.of(r));
        List<Rental> all = service.getAllRentals();
        assertEquals(1, all.size());
        assertEquals(r, all.get(0));

        // getRentalById
        when(repo.findById(id)).thenReturn(Optional.of(r));
        Optional<Rental> opt = service.getRentalById(id);
        assertTrue(opt.isPresent());
        assertEquals("H1", opt.get().getHouseId());

        // deleteRental
        doNothing().when(repo).deleteById(id);
        assertDoesNotThrow(() -> service.deleteRental(id));
        verify(repo).deleteById(id);
    }

    @Test
    void update_without_changing_tenant() {
        Rental stored = baseRental();
        Rental details = baseRental();
        // tweak some fields
        details.setHouseId("H2");
        details.setDurationInMonths(5);
        details.setApproved(true);

        when(repo.findById(id)).thenReturn(Optional.of(stored));
        when(repo.save(any(Rental.class))).thenAnswer(inv -> inv.getArgument(0));

        Rental updated = service.updateRental(id, details);

        assertEquals("H2", updated.getHouseId());
        assertEquals(5, updated.getDurationInMonths());
        assertTrue(updated.isApproved());
        assertEquals("Foo", updated.getFullName()); // tidak berubah
        verify(repo).findById(id);
        verify(repo).save(stored);
    }

    @Test
    void update_with_new_tenant() {
        Rental stored = baseRental();
        Tenant t = new Tenant("Bar", "082");
        stored.setTenant(null);

        Rental details = baseRental();
        details.setTenant(t);

        when(repo.findById(id)).thenReturn(Optional.of(stored));
        when(repo.save(any(Rental.class))).thenAnswer(inv -> inv.getArgument(0));

        Rental updated = service.updateRental(id, details);

        assertSame(t, updated.getTenant(), "the tenant should be set on update");
        verify(repo).findById(id);
        verify(repo).save(stored);
    }

    @Test
    void update_throws_when_not_found() {
        when(repo.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.updateRental(id, baseRental());
        });

        assertTrue(ex.getMessage().contains("Rental not found"));
        verify(repo).findById(id);
        verify(repo, never()).save(any());
    }
}
