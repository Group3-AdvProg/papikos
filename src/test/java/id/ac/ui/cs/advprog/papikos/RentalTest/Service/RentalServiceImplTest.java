package id.ac.ui.cs.advprog.papikos.RentalTest.Service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.BoardingHouse;
import id.ac.ui.cs.advprog.papikos.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.Rental.model.Tenant;
import id.ac.ui.cs.advprog.papikos.Rental.repository.RentalRepository;
import id.ac.ui.cs.advprog.papikos.Rental.service.RentalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
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
    void testCreateRental() {
        Rental rental = new Rental();
        when(repository.save(rental)).thenReturn(rental);

        Rental result = service.createRental(rental);
        assertEquals(rental, result);
    }

    @Test
    void testGetAllRentals() {
        List<Rental> rentals = List.of(new Rental(), new Rental());
        when(repository.findAll()).thenReturn(rentals);

        List<Rental> result = service.getAllRentals();
        assertEquals(2, result.size());
    }

    @Test
    void testGetRentalById() {
        Rental rental = new Rental();
        rental.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(rental));

        Optional<Rental> result = service.getRentalById(1L);
        assertTrue(result.isPresent());
        assertEquals(rental, result.get());
    }

    @Test
    void testUpdateRentalSuccess() {
        Rental existingRental = new Rental();
        existingRental.setId(1L);

        Rental updatedRental = new Rental();
        updatedRental.setCheckInDate(LocalDate.now());
        updatedRental.setDurationInMonths(6);
        updatedRental.setApproved(true);
        updatedRental.setCancelled(false);

        Tenant tenant = new Tenant();
        BoardingHouse house = new BoardingHouse();
        updatedRental.setTenant(tenant);
        updatedRental.setBoardingHouse(house);

        when(repository.findById(1L)).thenReturn(Optional.of(existingRental));
        when(repository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rental result = service.updateRental(1L, updatedRental);

        assertEquals(updatedRental.getCheckInDate(), result.getCheckInDate());
        assertEquals(updatedRental.getDurationInMonths(), result.getDurationInMonths());
        assertEquals(updatedRental.isApproved(), result.isApproved());
        assertEquals(updatedRental.isCancelled(), result.isCancelled());
        assertEquals(updatedRental.getTenant(), result.getTenant());
        assertEquals(updatedRental.getBoardingHouse(), result.getBoardingHouse());
    }

    @Test
    void testUpdateRentalNotFound() {
        Rental updatedRental = new Rental();
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.updateRental(99L, updatedRental);
        });

        assertEquals("Rental not found with id: 99", exception.getMessage());
    }

    @Test
    void testCancelRentalSuccess() {
        Rental rental = new Rental();
        rental.setCancelled(false);

        when(repository.findById(1L)).thenReturn(Optional.of(rental));

        service.cancelRental(1L);

        assertTrue(rental.isCancelled());
        verify(repository).save(rental);
    }

    @Test
    void testCancelRentalNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // no exception thrown, just no action
        service.cancelRental(99L);

        verify(repository, never()).save(any());
    }
}
