package id.ac.ui.cs.advprog.papikos.house.Rental.service;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoardingHouseServiceImpl implements BoardingHouseService {

    private final HouseRepository repository;

    @Autowired
    public BoardingHouseServiceImpl(HouseRepository repository) {
        this.repository = repository;
    }

    @Override
    public House create(House house) {
        return repository.save(house);
    }

    @Override
    public List<House> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<House> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public House update(Long id, House updatedHouse) {
        return repository.findById(id)
                .map(h -> {
                    h.setName(updatedHouse.getName());
                    h.setAddress(updatedHouse.getAddress());
                    h.setDescription(updatedHouse.getDescription());
                    h.setNumberOfRooms(updatedHouse.getNumberOfRooms());
                    h.setMonthlyRent(updatedHouse.getMonthlyRent());
                    h.setImageUrl(updatedHouse.getImageUrl());
                    return repository.save(h);
                })
                .orElseThrow(() -> new RuntimeException("House not found: " + id));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
