package id.ac.ui.cs.advprog.papikos.house.Rental.service;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardingHouseServiceImpl implements BoardingHouseService {

    private final HouseRepository repo;

    @Override
    public House create(House house) {
        return repo.save(house);
    }

    @Override
    public List<House> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<House> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public House update(Long id, House updatedHouse) {
        return repo.findById(id)
                .map(h -> {
                    h.setName(updatedHouse.getName());
                    h.setAddress(updatedHouse.getAddress());
                    h.setDescription(updatedHouse.getDescription());
                    h.setNumberOfRooms(updatedHouse.getNumberOfRooms());
                    h.setMonthlyRent(updatedHouse.getMonthlyRent());
                    h.setImageUrl(updatedHouse.getImageUrl());
                    return repo.save(h);
                })
                .orElseThrow(() -> new RuntimeException("House not found: " + id));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
