package id.ac.ui.cs.advprog.papikos.house.Rental.service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.Rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final RentalRepository repo;

    @Override
    public Rental createRental(Rental rental) {
        return repo.save(rental);
    }

    @Override
    public List<Rental> getAllRentals() {
        return repo.findAll();
    }

    @Override
    public Optional<Rental> getRentalById(UUID id) {
        return repo.findById(id);
    }

    @Override
    public Rental updateRental(UUID id, Rental rentalDetails) {
        return repo.findById(id)
                .map(r -> {
                    r.setHouseId(rentalDetails.getHouseId());
                    r.setCheckInDate(rentalDetails.getCheckInDate());
                    r.setDurationInMonths(rentalDetails.getDurationInMonths());
                    r.setApproved(rentalDetails.isApproved());
                    if (rentalDetails.getTenant() != null) {
                        r.setTenant(rentalDetails.getTenant());
                    }
                    return repo.save(r);
                })
                .orElseThrow(() -> new RuntimeException("Rental not found: " + id));
    }

    @Override
    public void deleteRental(UUID id) {
        repo.deleteById(id);
    }
}
