package id.ac.ui.cs.advprog.papikos.house.management.service;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;

import java.util.List;
import java.util.Optional;

@Service
public class HouseManagementServiceImpl implements HouseManagementService {

    @Autowired
    private HouseRepository houseRepository;

    @Override
    public CompletableFuture<Void> addHouse(House house) {
        houseRepository.save(house);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public List<House> findAllByOwner(User owner) { return houseRepository.findByOwner(owner); }

    @Override
    public Optional<House> findById(Long id) {
        return houseRepository.findById(id);
    }

    @Override
    public Optional<House> findByIdAndOwner(Long id, User owner) {
        return houseRepository.findByIdAndOwner(id, owner);
    }

    @Override
    public CompletableFuture<Void> updateHouse(Long id, House updatedHouse) {
        House existingHouse = houseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("House not found with ID: " + id));

        updatedHouse.setId(existingHouse.getId());
        houseRepository.save(updatedHouse);

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> deleteHouse(Long id) {
        houseRepository.deleteById(id);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public List<House> searchHouses(User owner, String keyword, Double minRent, Double maxRent) {
        return houseRepository.findAll().stream()
                .filter(h -> h.getOwner().equals(owner))
                .filter(h -> keyword == null || h.getName().toLowerCase().contains(keyword.toLowerCase()) || h.getAddress().toLowerCase().contains(keyword.toLowerCase()))
                .filter(h -> minRent == null || h.getMonthlyRent() >= minRent)
                .filter(h -> maxRent == null || h.getMonthlyRent() <= maxRent)
                .toList();
    }

}
