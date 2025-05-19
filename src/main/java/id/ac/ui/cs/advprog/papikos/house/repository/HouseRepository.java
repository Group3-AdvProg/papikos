package id.ac.ui.cs.advprog.papikos.house.repository;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {
    List<House> findByOwner(User owner);
    Optional<House> findByIdAndOwner(Long id, User owner);
}
