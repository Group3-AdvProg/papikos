package id.ac.ui.cs.advprog.papikos.repository;

import id.ac.ui.cs.advprog.papikos.model.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {
}
