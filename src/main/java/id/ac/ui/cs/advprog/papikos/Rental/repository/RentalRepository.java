package id.ac.ui.cs.advprog.papikos.Rental.repository;

import id.ac.ui.cs.advprog.papikos.Rental.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
}
