package id.ac.ui.cs.advprog.papikos.house.rental.service;

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
    public List<House> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<House> findById(Long id) {
        return repo.findById(id);
    }
}
