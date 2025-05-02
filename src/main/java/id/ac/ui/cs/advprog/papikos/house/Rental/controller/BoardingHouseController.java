package id.ac.ui.cs.advprog.papikos.house.Rental.controller;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.BoardingHouse;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.BoardingHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boardinghouses")
public class BoardingHouseController {

    @Autowired
    private BoardingHouseService boardingHouseService;

    @PostMapping
    public ResponseEntity<BoardingHouse> create(@RequestBody BoardingHouse boardingHouse) {
        return ResponseEntity.ok(boardingHouseService.create(boardingHouse));
    }

    @GetMapping
    public ResponseEntity<List<BoardingHouse>> findAll() {
        return ResponseEntity.ok(boardingHouseService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardingHouse> findById(@PathVariable Long id) {
        return boardingHouseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardingHouse> update(@PathVariable Long id, @RequestBody BoardingHouse updated) {
        return ResponseEntity.ok(boardingHouseService.update(id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boardingHouseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}