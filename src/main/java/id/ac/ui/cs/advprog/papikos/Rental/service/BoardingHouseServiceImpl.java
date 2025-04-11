package id.ac.ui.cs.advprog.papikos.Rental.service;

import id.ac.ui.cs.advprog.papikos.Rental.model.BoardingHouse;
import id.ac.ui.cs.advprog.papikos.Rental.repository.BoardingHouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoardingHouseServiceImpl implements BoardingHouseService {

    private final BoardingHouseRepository repository;

    public BoardingHouseServiceImpl(BoardingHouseRepository repository) {
        this.repository = repository;
    }

    @Override
    public BoardingHouse create(BoardingHouse boardingHouse) {
        return repository.save(boardingHouse);
    }

    @Override
    public List<BoardingHouse> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<BoardingHouse> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public BoardingHouse update(Long id, BoardingHouse updatedBoardingHouse) {
        return repository.findById(id).map(b -> {
            b.setName(updatedBoardingHouse.getName());
            b.setAddress(updatedBoardingHouse.getAddress());
            b.setDescription(updatedBoardingHouse.getDescription());
            b.setRoomCount(updatedBoardingHouse.getRoomCount());
            b.setMonthlyPrice(updatedBoardingHouse.getMonthlyPrice());
            return repository.save(b);
        }).orElseThrow(() -> new RuntimeException("BoardingHouse not found"));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
