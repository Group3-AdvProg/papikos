package id.ac.ui.cs.advprog.papikos.house.management.controller;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.house.management.service.HouseManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/management")
public class HouseManagementController {

    @Autowired
    private HouseManagementService houseManagementService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/houses")
    public ResponseEntity<List<House>> getAllHouses(@AuthenticationPrincipal UserDetails userDetails) {
        User owner = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        List<House> allHouses = houseManagementService.findAllByOwner(owner);
        return ResponseEntity.ok(allHouses);
    }

    @PostMapping("/houses")
    public ResponseEntity<House> createHouse(@RequestBody House house, @AuthenticationPrincipal UserDetails userDetails) {
        User owner = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        house.setOwner(owner);
        houseManagementService.addHouse(house);
        return ResponseEntity.ok(house);
    }

    @GetMapping("/houses/{id}")
    public ResponseEntity<?> getHouseById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User owner = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Optional<House> houseOpt = houseManagementService.findByIdAndOwner(id, owner);

        return houseOpt
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).body("Forbidden: You do not own this house."));
    }

    @PutMapping("/houses/{id}")
    public ResponseEntity<?> updateHouse(@PathVariable Long id, @RequestBody House updatedHouse, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<House> existingHouse = houseManagementService.findById(id);
        if (existingHouse.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User owner = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        if (!existingHouse.get().getOwner().getId().equals(owner.getId())) {
            return ResponseEntity.status(403).body("Forbidden: You do not own this house.");
        }

        updatedHouse.setOwner(owner);
        houseManagementService.updateHouse(id, updatedHouse);
        return ResponseEntity.ok(updatedHouse);
    }


    @DeleteMapping("/houses/{id}")
    public ResponseEntity<?> deleteHouse(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<House> house = houseManagementService.findById(id);
        if (house.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User owner = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        if (!house.get().getOwner().getId().equals(owner.getId())) {
            return ResponseEntity.status(403).body("Forbidden: You do not own this house.");
        }

        houseManagementService.deleteHouse(id);
        return ResponseEntity.noContent().build();
    }

}
