package id.ac.ui.cs.advprog.papikos.RentalTest.Service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.Rental.repository.RentalRepository;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.RentalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RentalServiceImplTest {

    @Mock private RentalRepository repo;
    @InjectMocks private RentalServiceImpl service;
    private UUID id = UUID.randomUUID();

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    private Rental gen() {
        Rental r = new Rental();
        r.setId(id);
        r.setHouseId("H");
        r.setCheckInDate(LocalDate.now());
        r.setDurationInMonths(1);
        r.setApproved(false);
        return r;
    }

    @Test
    void testCreate() {
        Rental r = gen();
        when(repo.save(r)).thenReturn(r);
        Rental out = service.createRental(r);
        assertEquals(r, out);
    }

    @Test
    void testGetAll() {
        Rental r = gen();
        when(repo.findAll()).thenReturn(Arrays.asList(r));
        assertEquals(1, service.getAllRentals().size());
    }

    @Test
    void testGetById() {
        Rental r = gen();
        when(repo.findById(id)).thenReturn(Optional.of(r));
        Optional<Rental> opt = service.getRentalById(id);
        assertTrue(opt.isPresent());
        assertEquals("H", opt.get().getHouseId());
    }

    @Test
    void testUpdate() {
        Rental old = gen();
        Rental upd = gen();
        upd.setDurationInMonths(5);
        when(repo.findById(id)).thenReturn(Optional.of(old));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
        Rental out = service.updateRental(id, upd);
        assertEquals(5, out.getDurationInMonths());
    }

    @Test
    void testDelete() {
        doNothing().when(repo).deleteById(id);
        assertDoesNotThrow(() -> service.deleteRental(id));
        verify(repo).deleteById(id);
    }
}
