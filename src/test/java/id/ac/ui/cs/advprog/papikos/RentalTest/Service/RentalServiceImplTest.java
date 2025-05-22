package id.ac.ui.cs.advprog.papikos.RentalTest.Service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.house.Rental.repository.RentalRepository;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.RentalServiceImpl;
import id.ac.ui.cs.advprog.papikos.house.model.House;
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
    private final Long id = 1L;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
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

        assertTrue(ex.getMessage().contains("Rental not found"));
        verify(repo).findById(id);
        verify(repo, never()).save(any());
    }
}
