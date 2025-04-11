package id.ac.ui.cs.advprog.papikos.management.repository;

import id.ac.ui.cs.advprog.papikos.management.model.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {
}
