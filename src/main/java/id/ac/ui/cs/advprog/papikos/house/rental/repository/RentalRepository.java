package id.ac.ui.cs.advprog.papikos.house.rental.repository;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.house.rental.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    Optional<Rental> findTopByTenantAndHouseOwnerAndIsPaidFalseOrderByIdDesc(User tenant, User owner);
    // ID pakai Long, sesuai dengan rental.java
}
