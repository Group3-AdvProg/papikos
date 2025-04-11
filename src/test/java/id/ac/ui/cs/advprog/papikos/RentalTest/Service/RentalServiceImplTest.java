package id.ac.ui.cs.advprog.papikos.RentalTest.Service;

import id.ac.ui.cs.advprog.papikos.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.Rental.repository.RentalRepository;
import id.ac.ui.cs.advprog.papikos.Rental.service.RentalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RentalServiceImplTest {

    private RentalRepository repository;
    private RentalServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(RentalRepository.class);
        service = new RentalServiceImpl(repository);
    }

    @Test
    void testCreate() {
        Rental rental = new Rental();
        when(repository.save(rental)).thenReturn(rental);
        assertEquals(rental, service.createRental(rental));
    }

    @Test
    void testGetAll() {
        when(repository.findAll()).thenReturn(List.of(new Rental(), new Rental()));
        assertEquals(2, service.getAllRentals().size());
    }

    // Tambahkan test lainnya jika perlu
}
