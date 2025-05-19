package id.ac.ui.cs.advprog.papikos.house.Rental.service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentalService {
    Rental createRental(Rental rental);
    List<Rental> getAllRentals();
    Optional<Rental> getRentalById(UUID id);
    Rental updateRental(UUID id, Rental rental);
    void deleteRental(UUID id);    // ← make sure this exists
}
