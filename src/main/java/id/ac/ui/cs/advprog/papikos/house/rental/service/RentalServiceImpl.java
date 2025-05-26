package id.ac.ui.cs.advprog.papikos.house.rental.service;

import id.ac.ui.cs.advprog.papikos.house.rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final RentalRepository repo;

    // ─── in-memory caches ───────────────────────────────────────────────
    private final ConcurrentMap<Long, Rental> rentalCache = new ConcurrentHashMap<>();
    private volatile List<Rental> rentalsCache;

    @Override
    public Rental createRental(Rental rental) {
        Rental saved = repo.save(rental);
        rentalCache.put(saved.getId(), saved);
        rentalsCache = null;
        return saved;
    }

    @Override
    @Async("asyncExecutor")
    public CompletableFuture<Rental> createRentalAsync(Rental rental) {
        Rental saved = repo.save(rental);
        rentalCache.put(saved.getId(), saved);
        rentalsCache = null;
        return CompletableFuture.completedFuture(saved);
    }

    @Override
    public List<Rental> getAllRentals() {
        if (rentalsCache != null) {
            return rentalsCache;
        }
        List<Rental> rentals = repo.findAll();
        rentalsCache = rentals;
        rentals.forEach(r -> rentalCache.putIfAbsent(r.getId(), r));
        return rentals;
    }

    @Override
    @Async("asyncExecutor")
    public CompletableFuture<List<Rental>> getAllRentalsAsync() {
        return CompletableFuture.completedFuture(getAllRentals());
    }

    @Override
    public Optional<Rental> getRentalById(Long id) {
        Optional<Rental> result = repo.findById(id); // 🚫 skip cache
        result.ifPresent(r -> {
            rentalCache.put(r.getId(), r);   // refresh cache
            rentalsCache = null;
        });
        return result;
    }


    @Override
    @Async("asyncExecutor")
    public CompletableFuture<Optional<Rental>> getRentalByIdAsync(Long id) {
        return CompletableFuture.completedFuture(getRentalById(id));
    }

    @Override
    public Rental updateRental(Long id, Rental rentalDetails) {
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
                .orElseThrow(() -> new RuntimeException("rental not found: " + id));
        rentalCache.put(updated.getId(), updated);
        rentalsCache = null;
        return updated;
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
                .orElseThrow(() -> new RuntimeException("rental not found: " + id));
        rentalCache.put(updated.getId(), updated);
        rentalsCache = null;
        return CompletableFuture.completedFuture(updated);
    }

    @Override
    public void deleteRental(Long id) {
        repo.deleteById(id);
        rentalCache.remove(id);
        rentalsCache = null;
    }

    @Override
    @Async("asyncExecutor")
    public CompletableFuture<Void> deleteRentalAsync(Long id) {
        repo.deleteById(id);
        rentalCache.remove(id);
        rentalsCache = null;
        return CompletableFuture.completedFuture(null);
    }

    public void updateRentalCache(Rental rental) {
        rentalCache.put(rental.getId(), rental);
        rentalsCache = null;
    }
}
