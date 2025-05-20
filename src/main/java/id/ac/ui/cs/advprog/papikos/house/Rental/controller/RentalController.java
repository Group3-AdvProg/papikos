package id.ac.ui.cs.advprog.papikos.house.Rental.controller;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService service;

    @PostMapping
    public ResponseEntity<Rental> create(@RequestBody Rental rental) {
        Rental created = service.createRental(rental);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Rental>> findAll() {
        return ResponseEntity.ok(service.getAllRentals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rental> findById(@PathVariable Long id) { //  UUID → Long
        return service.getRentalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rental> update(@PathVariable Long id, @RequestBody Rental rental) { //  UUID → Long
        return ResponseEntity.ok(service.updateRental(id, rental));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { //  UUID → Long
        service.deleteRental(id);
        return ResponseEntity.noContent().build();
    }
}
