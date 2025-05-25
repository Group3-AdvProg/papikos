package id.ac.ui.cs.advprog.papikos.house.rental.repository;

import id.ac.ui.cs.advprog.papikos.house.rental.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    // ID pakai Long, sesuai dengan rental.java
}
