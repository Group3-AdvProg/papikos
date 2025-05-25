package id.ac.ui.cs.advprog.papikos.house.Rental.controller;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.BoardingHouseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/houses")
public class BoardingHouseController {

    private static final Logger logger =
            LoggerFactory.getLogger(BoardingHouseController.class);

    private final BoardingHouseService service;

    @GetMapping
    public ResponseEntity<List<House>> list() {
        logger.info("GET /api/houses – fetching all houses");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<House> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(house -> {
                    logger.info("House [{}] found", id);
                    return ResponseEntity.ok(house);
                })
                .orElseGet(() -> {
                    logger.warn("House [{}] not found", id);
                    return ResponseEntity.notFound().build();
                });
    }
}
