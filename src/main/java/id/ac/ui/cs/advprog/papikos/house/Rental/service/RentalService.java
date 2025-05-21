package id.ac.ui.cs.advprog.papikos.house.Rental.service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;

import java.util.List;
import java.util.Optional;

public interface RentalService {
    Rental createRental(Rental rental);
    List<Rental> getAllRentals();
    Optional<Rental> getRentalById(Long id);
    Rental updateRental(Long id, Rental rentalDetails);
    void deleteRental(Long id);
}
