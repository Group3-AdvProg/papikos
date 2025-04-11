package id.ac.ui.cs.advprog.papikos.Rental.service;

import id.ac.ui.cs.advprog.papikos.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.Rental.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;

    // Constructor for dependency injection (Spring & testing)
    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    @Override
    public Rental createRental(Rental rental) {
        return rentalRepository.save(rental);
    }

    @Override
    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    @Override
    public Optional<Rental> getRentalById(Long id) {
        return rentalRepository.findById(id);
    }

    @Override
    public Rental updateRental(Long id, Rental updatedRental) {
        Optional<Rental> existingRental = rentalRepository.findById(id);
        if (existingRental.isPresent()) {
            Rental rental = existingRental.get();
            rental.setCheckInDate(updatedRental.getCheckInDate());
            rental.setDurationInMonths(updatedRental.getDurationInMonths());
            rental.setApproved(updatedRental.isApproved());
            rental.setCancelled(updatedRental.isCancelled());
            rental.setTenant(updatedRental.getTenant());
            rental.setBoardingHouse(updatedRental.getBoardingHouse());
            return rentalRepository.save(rental);
        }
        throw new RuntimeException("Rental not found with id: " + id);
    }

    @Override
    public void cancelRental(Long id) {
        Optional<Rental> rentalOpt = rentalRepository.findById(id);
        rentalOpt.ifPresent(rental -> {
            rental.setCancelled(true);
            rentalRepository.save(rental);
        });
    }
}
