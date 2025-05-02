package id.ac.ui.cs.advprog.papikos.house.management.service;

import id.ac.ui.cs.advprog.papikos.house.model.House;

import java.util.List;
import java.util.Optional;

public interface HouseManagementService {
    void addHouse(House house);
    List<House> findAll();
    Optional<House> findById(Long id);
    void updateHouse(Long id, House updatedHouse);
    void deleteHouse(Long id);
}
