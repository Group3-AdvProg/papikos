package id.ac.ui.cs.advprog.papikos.house.rental.service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.Rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final RentalRepository repo;

    @Override
    public Rental createRental(Rental rental) {
        return repo.save(rental);
    }

    @Override
    @Async("asyncExecutor")
    public CompletableFuture<Rental> createRentalAsync(Rental rental) {
        Rental saved = repo.save(rental);
        return CompletableFuture.completedFuture(saved);
    }

    @Override
    public List<Rental> getAllRentals() {
        return repo.findAll();
    }

    @Override
    @Async("asyncExecutor")
    public CompletableFuture<List<Rental>> getAllRentalsAsync() {
        List<Rental> rentals = repo.findAll();
        return CompletableFuture.completedFuture(rentals);
    }

    @Override
    public Optional<Rental> getRentalById(Long id) {
        return repo.findById(id);
    }

    @Override
    @Async("asyncExecutor")
    public CompletableFuture<Optional<Rental>> getRentalByIdAsync(Long id) {
        Optional<Rental> result = repo.findById(id);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public Rental updateRental(Long id, Rental rentalDetails) {
        return repo.findById(id)
                .map(r -> {
                    r.setHouse(rentalDetails.getHouse());
                    r.setCheckInDate(rentalDetails.getCheckInDate());
                    r.setDurationInMonths(rentalDetails.getDurationInMonths());
                    r.setApproved(rentalDetails.isApproved());
                    r.setTotalPrice(rentalDetails.getTotalPrice());
                    r.setPaid(rentalDetails.isPaid());
                    if (rentalDetails.getTenant() != null) {
                        r.setTenant(rentalDetails.getTenant());
                    }
                    return repo.save(r);
                })
                .orElseThrow(() -> new RuntimeException("Rental not found: " + id));
    }

    @Override
    @Async("asyncExecutor")
    public CompletableFuture<Rental> updateRentalAsync(Long id, Rental rentalDetails) {
        Rental updated = repo.findById(id)
                .map(r -> {
                    r.setHouse(rentalDetails.getHouse());
                    r.setCheckInDate(rentalDetails.getCheckInDate());
                    r.setDurationInMonths(rentalDetails.getDurationInMonths());
                    r.setApproved(rentalDetails.isApproved());
                    r.setTotalPrice(rentalDetails.getTotalPrice());
                    r.setPaid(rentalDetails.isPaid());
                    if (rentalDetails.getTenant() != null) {
                        r.setTenant(rentalDetails.getTenant());
                    }
                    return repo.save(r);
                })
                .orElseThrow(() -> new RuntimeException("Rental not found: " + id));

        return CompletableFuture.completedFuture(updated);
    }

    @Override
    public void deleteRental(Long id) {
        repo.deleteById(id);
    }

    @Override
    @Async("asyncExecutor")
    public CompletableFuture<Void> deleteRentalAsync(Long id) {
        repo.deleteById(id);
        return CompletableFuture.completedFuture(null);
    }
}
