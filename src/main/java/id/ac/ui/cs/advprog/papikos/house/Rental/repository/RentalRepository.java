package id.ac.ui.cs.advprog.papikos.house.Rental.repository;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RentalRepository extends JpaRepository<Rental, UUID> {
    // sekarang UUID, bukan Long
}
