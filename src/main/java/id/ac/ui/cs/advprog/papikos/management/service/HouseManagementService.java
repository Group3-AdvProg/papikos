package id.ac.ui.cs.advprog.papikos.management.service;

import id.ac.ui.cs.advprog.papikos.management.model.House;
import id.ac.ui.cs.advprog.papikos.management.repository.HouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HouseManagementService {

    private final HouseRepository houseRepository;

    public HouseManagementService(HouseRepository houseRepository) {
        this.houseRepository = houseRepository;
    }

    public void addHouse(House house) {
        houseRepository.save(house);
    }

    public List<House> findAll() {
        return houseRepository.findAll();
    }

    public Optional<House> findById(Long id) {
        return houseRepository.findById(id);
    }

    public void updateHouse(Long id, House updated) {
        if (houseRepository.findById(id).isPresent()) {
            updated.setId(id);
            houseRepository.save(updated);
        } else throw new IllegalArgumentException("Not found");
    }

    public void deleteHouse(Long id) {
        houseRepository.deleteById(id);
    }
}
