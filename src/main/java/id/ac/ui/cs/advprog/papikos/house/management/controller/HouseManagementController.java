package id.ac.ui.cs.advprog.papikos.house.management.controller;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.RentalService;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.house.management.service.HouseManagementService;
import id.ac.ui.cs.advprog.papikos.wishlist.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/management")
public class HouseManagementController {

    @Autowired
    private HouseManagementService houseManagementService;

    @Autowired
    private RentalService rentalService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(HouseManagementController.class);

    @GetMapping("/houses")
    public ResponseEntity<List<House>> getAllHouses(Principal principal) {
        User owner = userRepository.findByEmail(principal.getName()).orElseThrow();
        logger.info("User [{}] requested all their houses", principal.getName());
        List<House> allHouses = houseManagementService.findAllByOwner(owner);
        return ResponseEntity.ok(allHouses);
    }

    @PostMapping("/houses")
    public ResponseEntity<?> createHouse(@RequestBody House house, Principal principal) {
        User owner = userRepository.findByEmail(principal.getName()).orElseThrow();

        if (!owner.isApproved()) {
            logger.warn("User [{}] is not approved to create houses", owner.getEmail());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account not approved by admin yet");
        }
        house.setOwner(owner);
        houseManagementService.addHouse(house).join();
        logger.info("User [{}] successfully created house [{}]", owner.getEmail(), house.getName());
        return ResponseEntity.ok(house);
    }

    @GetMapping("/houses/{id}")
    public ResponseEntity<?> getHouseById(@PathVariable Long id, Principal principal) {
        User owner = userRepository.findByEmail(principal.getName()).orElseThrow();
        logger.info("User [{}] is fetching house ID: {}", principal.getName(), id);
        Optional<House> houseOpt = houseManagementService.findByIdAndOwner(id, owner);

        return houseOpt
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).body("Forbidden: You do not own this house."));
    }

    @PutMapping("/houses/{id}")
    public ResponseEntity<?> updateHouse(@PathVariable Long id, @RequestBody House updatedHouse, Principal principal) {
        Optional<House> existingHouse = houseManagementService.findById(id);
        if (existingHouse.isEmpty()) {
            logger.warn("House ID [{}] not found for update", id);
            return ResponseEntity.notFound().build();
        }

        User owner = userRepository.findByEmail(principal.getName()).orElseThrow();
        if (!existingHouse.get().getOwner().getId().equals(owner.getId())) {
            logger.warn("User [{}] attempted to update house [{}] they do not own", owner.getEmail(), id);
            return ResponseEntity.status(403).body("Forbidden: You do not own this house.");
        }

        updatedHouse.setOwner(owner);
        houseManagementService.updateHouse(id, updatedHouse).join();
        logger.info("User [{}] updated house ID [{}]", owner.getEmail(), id);

        if (updatedHouse.getNumberOfRooms() > existingHouse.get().getNumberOfRooms()) {
            logger.info("User [{}] triggered wishlist notification for house [{}]", owner.getEmail(), id);
            notificationService.notifyAvailability(id);
        }

        return ResponseEntity.ok(updatedHouse);
    }


    @DeleteMapping("/houses/{id}")
    public ResponseEntity<?> deleteHouse(@PathVariable Long id, Principal principal) {
        Optional<House> house = houseManagementService.findById(id);
        if (house.isEmpty()) {
            logger.warn("House ID [{}] not found for deletion", id);
            return ResponseEntity.notFound().build();
        }

        User owner = userRepository.findByEmail(principal.getName()).orElseThrow();
        if (!house.get().getOwner().getId().equals(owner.getId())) {
            logger.warn("User [{}] attempted to delete house [{}] they do not own", owner.getEmail(), id);
            return ResponseEntity.status(403).body("Forbidden: You do not own this house.");
        }

        houseManagementService.deleteHouse(id).join();
        logger.info("User [{}] deleted house ID [{}]", owner.getEmail(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/houses/search")
    public ResponseEntity<List<House>> searchHouses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minRent,
            @RequestParam(required = false) Double maxRent,
            Principal principal) {

        User owner = userRepository.findByEmail(principal.getName()).orElseThrow();
        logger.info("User [{}] searched houses with keyword='{}', minRent={}, maxRent={}", owner.getEmail(), keyword, minRent, maxRent);
        List<House> result = houseManagementService.searchHouses(owner, keyword, minRent, maxRent);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/rentals/{rentalId}/approve")
    public ResponseEntity<?> approveRental(@PathVariable Long rentalId, Principal principal) {
        User landlord = userRepository.findByEmail(principal.getName()).orElseThrow();
        Rental rental = rentalService.getRentalById(rentalId).orElseThrow();

        House house = rental.getHouse();
        if (!house.getOwner().getId().equals(landlord.getId())) {
            logger.warn("User [{}] attempted to approve rental [{}] for house [{}] they do not own", landlord.getEmail(), rentalId, house.getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not own this house.");
        }

        if (!rental.isApproved()) {
            rental.setApproved(true);
            rentalService.updateRental(rentalId, rental);
            logger.info("User [{}] approved rental [{}] for house [{}]", landlord.getEmail(), rentalId, house.getId());

            int currentRooms = house.getNumberOfRooms();
            if (currentRooms > 0) {
                house.setNumberOfRooms(currentRooms - 1);
                houseManagementService.updateHouse(house.getId(), house).join();
                logger.info("House [{}] room count updated by user [{}]", house.getId(), landlord.getEmail());
            }

            notificationService.notifyTenantRentalApproved(
                    landlord.getId(),
                    rental.getTenant().getId(),
                    house.getId()
            );
        }

        return ResponseEntity.ok("Rental approved.");
    }
}
