package id.ac.ui.cs.advprog.papikos.house.rental.repository;

import id.ac.ui.cs.advprog.papikos.house.rental.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
}
