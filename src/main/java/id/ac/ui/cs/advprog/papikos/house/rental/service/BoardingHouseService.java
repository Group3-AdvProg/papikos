package id.ac.ui.cs.advprog.papikos.house.rental.service;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import java.util.List;
import java.util.Optional;

public interface BoardingHouseService {

    List<House> findAll();

    Optional<House> findById(Long id);
}
