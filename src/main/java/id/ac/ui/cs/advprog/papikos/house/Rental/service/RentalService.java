package id.ac.ui.cs.advprog.papikos.house.Rental.service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;

import java.util.List;
import java.util.Optional;

import java.util.concurrent.CompletableFuture;

public interface RentalService {
    Rental createRental(Rental rental);
    CompletableFuture<Rental> createRentalAsync(Rental rental); // NEW
    List<Rental> getAllRentals();
    Optional<Rental> getRentalById(Long id);
    Rental updateRental(Long id, Rental rentalDetails);
    void deleteRental(Long id);
}
