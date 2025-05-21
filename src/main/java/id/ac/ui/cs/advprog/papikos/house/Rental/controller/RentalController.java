package id.ac.ui.cs.advprog.papikos.house.Rental.controller;

import id.ac.ui.cs.advprog.papikos.house.Rental.dto.RentalDTO;
import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import id.ac.ui.cs.advprog.papikos.house.Rental.repository.TenantRepository;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.RentalService;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService service;
    private final HouseRepository houseRepository;
    private final TenantRepository tenantRepository;

    @PostMapping
    public ResponseEntity<Rental> create(@RequestBody RentalDTO dto) {
        House house = houseRepository.findById(dto.getHouseId())
                .orElseThrow(() -> new RuntimeException("House not found"));
        Tenant tenant = tenantRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        Rental rental = new Rental();
        rental.setHouse(house);
        rental.setTenant(tenant);
        rental.setFullName(dto.getFullName());
        rental.setPhoneNumber(dto.getPhoneNumber());
        rental.setCheckInDate(dto.getCheckInDate());
        rental.setDurationInMonths(dto.getDurationInMonths());
        rental.setApproved(dto.isApproved());
        rental.setTotalPrice(dto.getTotalPrice());
        rental.setPaid(dto.isPaid());

        Rental created = service.createRental(rental);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Rental>> findAll() {
        return ResponseEntity.ok(service.getAllRentals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rental> findById(@PathVariable Long id) {
        return service.getRentalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rental> update(@PathVariable Long id, @RequestBody Rental rental) {
        return ResponseEntity.ok(service.updateRental(id, rental));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteRental(id);
        return ResponseEntity.noContent().build();
    }
}
