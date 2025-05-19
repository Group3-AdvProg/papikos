package id.ac.ui.cs.advprog.papikos.house.Rental.service;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import java.util.List;
import java.util.Optional;

public interface BoardingHouseService {
    House create(House house);
    List<House> findAll();
    Optional<House> findById(Long id);
    House update(Long id, House updatedHouse);
    void delete(Long id);
}
