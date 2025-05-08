package id.ac.ui.cs.advprog.papikos.house.management.controller;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.management.service.HouseManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/management")
public class HouseManagementController {

    @Autowired
    private HouseManagementService houseManagementService;

    @GetMapping("/houses")
    public ResponseEntity<List<House>> getAllHouses() {
        List<House> allHouses = houseManagementService.findAll();
        return ResponseEntity.ok(allHouses);
    }

    @PostMapping("/houses")
    public ResponseEntity<House> createHouse(@RequestBody House house) {
        houseManagementService.addHouse(house);
        return ResponseEntity.ok(house);
    }

    @GetMapping("/houses/{id}")
    public ResponseEntity<?> getHouseById(@PathVariable Long id) {
        Optional<House> house = houseManagementService.findById(id);
        return house.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/houses/{id}")
    public ResponseEntity<?> updateHouse(@PathVariable Long id, @RequestBody House updatedHouse) {
        Optional<House> existingHouse = houseManagementService.findById(id);
        if (existingHouse.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        houseManagementService.updateHouse(id, updatedHouse);
        return ResponseEntity.ok(updatedHouse);
    }

    @DeleteMapping("/houses/{id}")
    public ResponseEntity<Void> deleteHouse(@PathVariable Long id) {
        houseManagementService.deleteHouse(id);
        return ResponseEntity.noContent().build();
    }
}
