package id.ac.ui.cs.advprog.papikos.house.rental.service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface RentalService {
    Rental createRental(Rental rental);
    CompletableFuture<Rental> createRentalAsync(Rental rental);
    List<Rental> getAllRentals();
    CompletableFuture<List<Rental>> getAllRentalsAsync();
    Optional<Rental> getRentalById(Long id);
    CompletableFuture<Optional<Rental>> getRentalByIdAsync(Long id);
    Rental updateRental(Long id, Rental rentalDetails);
    CompletableFuture<Rental> updateRentalAsync(Long id, Rental rentalDetails);
    void deleteRental(Long id);
    CompletableFuture<Void> deleteRentalAsync(Long id); // async delete
}
