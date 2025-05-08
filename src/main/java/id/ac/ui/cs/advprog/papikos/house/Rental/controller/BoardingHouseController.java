package id.ac.ui.cs.advprog.papikos.house.Rental.controller;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.BoardingHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/houses")
public class BoardingHouseController {

    private final BoardingHouseService service;

    @Autowired
    public BoardingHouseController(BoardingHouseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<House> create(@RequestBody House house) {
        return ResponseEntity.ok(service.create(house));
    }

    @GetMapping
    public ResponseEntity<List<House>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<House> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<House> update(@PathVariable Long id, @RequestBody House house) {
        try {
            return ResponseEntity.ok(service.update(id, house));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
