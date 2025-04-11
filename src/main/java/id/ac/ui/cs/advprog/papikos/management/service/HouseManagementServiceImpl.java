package id.ac.ui.cs.advprog.papikos.management.service;

import id.ac.ui.cs.advprog.papikos.management.model.House;
import id.ac.ui.cs.advprog.papikos.management.repository.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HouseManagementServiceImpl implements HouseManagementService {

    @Autowired
    private HouseRepository houseRepository;

    @Override
    public void addHouse(House house) {
        houseRepository.save(house);
    }

    @Override
    public List<House> findAll() {
        return houseRepository.findAll();
    }

    @Override
    public Optional<House> findById(Long id) {
        return houseRepository.findById(id);
    }

    @Override
    public void updateHouse(Long id, House updatedHouse) {
        if (houseRepository.findById(id).isPresent()) {
            updatedHouse.setId(id);
            houseRepository.save(updatedHouse);
        } else {
            throw new IllegalArgumentException("House not found with ID: " + id);
        }
    }

    @Override
    public void deleteHouse(Long id) {
        houseRepository.deleteById(id);
    }
}
