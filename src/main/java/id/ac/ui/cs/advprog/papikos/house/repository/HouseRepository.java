package id.ac.ui.cs.advprog.papikos.house.repository;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {
}
