package id.ac.ui.cs.advprog.papikos.house.Rental.service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.Rental.repository.RentalRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository repository;

    public RentalServiceImpl(RentalRepository repository) {
        this.repository = repository;
    }

    @Override
    public Rental createRental(Rental rental) {
        return repository.save(rental);
    }

    @Override
    public List<Rental> getAllRentals() {
        return repository.findAll();
    }

    @Override
    public Optional<Rental> getRentalById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Rental updateRental(Long id, Rental updated) {
        return repository.findById(id)
                .map(r -> {
                    r.setCheckInDate(updated.getCheckInDate());
                    r.setDurationInMonths(updated.getDurationInMonths());
                    r.setApproved(updated.isApproved());
                    r.setCancelled(updated.isCancelled());
                    r.setTenant(updated.getTenant());
                    r.setHouse(updated.getHouse());
                    return repository.save(r);
                })
                .orElseThrow(() -> new RuntimeException("Rental not found with id: " + id));
    }

    @Override
    public void cancelRental(Long id) {
        repository.findById(id).ifPresent(r -> {
            r.setCancelled(true);
            repository.save(r);
        });
    }
}