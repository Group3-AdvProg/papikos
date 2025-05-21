package id.ac.ui.cs.advprog.papikos.house.management.controller;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.house.management.service.HouseManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
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
    public ResponseEntity<List<House>> getAllHouses(Principal principal) {
        User owner = userRepository.findByEmail(principal.getName()).orElseThrow();
        List<House> allHouses = houseManagementService.findAllByOwner(owner);
        return ResponseEntity.ok(allHouses);
    }

    @PostMapping("/houses")
    public ResponseEntity<House> createHouse(@RequestBody House house, Principal principal) {
        User owner = userRepository.findByEmail(principal.getName()).orElseThrow();
        house.setOwner(owner);
        houseManagementService.addHouse(house);
        return ResponseEntity.ok(house);
    }

    @GetMapping("/houses/{id}")
    public ResponseEntity<?> getHouseById(@PathVariable Long id, Principal principal) {
        User owner = userRepository.findByEmail(principal.getName()).orElseThrow();
        Optional<House> houseOpt = houseManagementService.findByIdAndOwner(id, owner);

        return houseOpt
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).body("Forbidden: You do not own this house."));
    }

    @PutMapping("/houses/{id}")
    public ResponseEntity<?> updateHouse(@PathVariable Long id, @RequestBody House updatedHouse, Principal principal) {
        Optional<House> existingHouse = houseManagementService.findById(id);
        if (existingHouse.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User owner = userRepository.findByEmail(principal.getName()).orElseThrow();
        if (!existingHouse.get().getOwner().getId().equals(owner.getId())) {
            return ResponseEntity.status(403).body("Forbidden: You do not own this house.");
        }

        updatedHouse.setOwner(owner);
        houseManagementService.updateHouse(id, updatedHouse);
        return ResponseEntity.ok(updatedHouse);
    }


    @DeleteMapping("/houses/{id}")
    public ResponseEntity<?> deleteHouse(@PathVariable Long id, Principal principal) {
        Optional<House> house = houseManagementService.findById(id);
        if (house.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User owner = userRepository.findByEmail(principal.getName()).orElseThrow();
        if (!house.get().getOwner().getId().equals(owner.getId())) {
            return ResponseEntity.status(403).body("Forbidden: You do not own this house.");
        }

        houseManagementService.deleteHouse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/houses/search")
    public ResponseEntity<List<House>> searchHouses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minRent,
            @RequestParam(required = false) Double maxRent,
            Principal principal) {

        User owner = userRepository.findByEmail(principal.getName()).orElseThrow();
        List<House> result = houseManagementService.searchHouses(owner, keyword, minRent, maxRent);
        return ResponseEntity.ok(result);
    }


}
