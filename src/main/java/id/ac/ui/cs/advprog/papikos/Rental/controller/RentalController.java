package id.ac.ui.cs.advprog.papikos.house.rental.controller;

import id.ac.ui.cs.advprog.papikos.house.rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.rental.service.RentalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService service;

    public RentalController(RentalService service) {
        this.service = service;
    }

    @PostMapping
    public Rental create(@RequestBody Rental rental) {
        return service.createRental(rental);
    }

    @GetMapping
    public List<Rental> findAll() {
        return service.getAllRentals();
    }

    @GetMapping("/{id}")
    public Optional<Rental> findById(@PathVariable Long id) {
        return service.getRentalById(id);
    }

    @PutMapping("/{id}")
    public Rental update(@PathVariable Long id, @RequestBody Rental rental) {
        return service.updateRental(id, rental);
    }

    @DeleteMapping("/{id}")
    public void cancel(@PathVariable Long id) {
        service.cancelRental(id);
    }
}
