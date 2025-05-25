package id.ac.ui.cs.advprog.papikos.house.Rental.controller;

import id.ac.ui.cs.advprog.papikos.house.Rental.dto.RentalDTO;
import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.RentalService;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService service;
    private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // --- synchronous endpoints ---

    @PostMapping
    public ResponseEntity<Rental> create(@RequestBody RentalDTO dto) {
        House house = houseRepository.findById(dto.getHouseId())
                .orElseThrow(() -> new RuntimeException("House not found"));
        User tenant = userRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        if (!"ROLE_TENANT".equals(tenant.getRole())) {
            throw new RuntimeException("User is not a tenant");
        }

        Rental rental = new Rental();
        rental.setHouse(house);
        rental.setTenant(tenant);
        rental.setFullName(tenant.getFullName());
        rental.setPhoneNumber(tenant.getPhoneNumber());
        rental.setCheckInDate(LocalDate.now());
        rental.setDurationInMonths(dto.getDurationInMonths());
        rental.setApproved(false);

        double baseRent = house.getMonthlyRent();
        int totalPrice = (int) (baseRent * dto.getDurationInMonths());
        rental.setTotalPrice(totalPrice);
        rental.setPaid(false);

        Rental created = service.createRental(rental);
        notificationService.notifyAvailability(house.getId());
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
    public ResponseEntity<Rental> update(
            @PathVariable Long id,
            @RequestBody Rental rental
    ) {
        return ResponseEntity.ok(service.updateRental(id, rental));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        Rental existing = service.getRentalById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found"));
        House house = existing.getHouse();

        service.deleteRental(id);
        house.setNumberOfRooms(house.getNumberOfRooms() + 1);
        houseRepository.save(house);
        notificationService.notifyAvailability(house.getId());

        return ResponseEntity.ok("Rental deleted and availability updated");
    }

    // --- asynchronous endpoints ---

    @PostMapping("/async")
    public CompletableFuture<ResponseEntity<Rental>> createAsync(@RequestBody RentalDTO dto) {
        House house = houseRepository.findById(dto.getHouseId())
                .orElseThrow(() -> new RuntimeException("House not found"));
        User tenant = userRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        if (!"ROLE_TENANT".equals(tenant.getRole())) {
            throw new RuntimeException("User is not a tenant");
        }

        Rental rental = new Rental();
        rental.setHouse(house);
        rental.setTenant(tenant);
        rental.setFullName(tenant.getFullName());
        rental.setPhoneNumber(tenant.getPhoneNumber());
        rental.setCheckInDate(LocalDate.now());
        rental.setDurationInMonths(dto.getDurationInMonths());
        rental.setApproved(false);

        double baseRent = house.getMonthlyRent();
        int totalPrice = (int) (baseRent * dto.getDurationInMonths());
        rental.setTotalPrice(totalPrice);
        rental.setPaid(false);

        return service.createRentalAsync(rental)
                .thenApply(saved -> {
                    notificationService.notifyAvailability(house.getId());
                    return ResponseEntity.ok(saved);
                });
    }

    @GetMapping("/async")
    public CompletableFuture<ResponseEntity<List<Rental>>> findAllAsync() {
        return service.getAllRentalsAsync()
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/async/{id}")
    public CompletableFuture<ResponseEntity<Rental>> findByIdAsync(@PathVariable Long id) {
        return service.getRentalByIdAsync(id)
                .thenApply(opt -> opt
                        .map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @PutMapping("/async/{id}")
    public CompletableFuture<ResponseEntity<Rental>> updateAsync(
            @PathVariable Long id,
            @RequestBody Rental rental
    ) {
        return service.updateRentalAsync(id, rental)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/async/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteAsync(@PathVariable Long id) {
        return service.getRentalByIdAsync(id)
                .thenCompose(opt -> {
                    if (opt.isEmpty()) {
                        return CompletableFuture.completedFuture(
                                ResponseEntity.notFound().build()
                        );
                    }
                    Rental rental = opt.get();
                    House house = rental.getHouse();
                    house.setNumberOfRooms(house.getNumberOfRooms() + 1);
                    houseRepository.save(house);
                    notificationService.notifyAvailability(house.getId());

                    return service.deleteRentalAsync(id)
                            .thenApply(v ->
                                    ResponseEntity.ok("Rental deleted and availability updated")
                            );
                });
    }
}
