package id.ac.ui.cs.advprog.papikos.house.Rental.service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.BoardingHouse;

import java.util.List;
import java.util.Optional;

public interface BoardingHouseService {
    BoardingHouse create(BoardingHouse boardingHouse);
    List<BoardingHouse> findAll();
    Optional<BoardingHouse> findById(Long id);
    BoardingHouse update(Long id, BoardingHouse updatedBoardingHouse);
    void delete(Long id);
}
