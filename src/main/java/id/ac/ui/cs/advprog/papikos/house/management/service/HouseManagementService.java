package id.ac.ui.cs.advprog.papikos.house.management.service;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface HouseManagementService {
    CompletableFuture<Void> addHouse(House house);
    List<House> findAllByOwner(User owner);
    Optional<House> findById(Long id);
    CompletableFuture<Void> updateHouse(Long id, House updatedHouse);
    CompletableFuture<Void> deleteHouse(Long id);
    Optional<House> findByIdAndOwner(Long id, User owner);
    List<House> searchHouses(User owner, String keyword, Double minRent, Double maxRent);
}