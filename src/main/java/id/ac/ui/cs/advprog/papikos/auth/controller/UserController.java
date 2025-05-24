package id.ac.ui.cs.advprog.papikos.auth.controller;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/auth/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @PutMapping("/approve/{userId}")
    public ResponseEntity<?> approveLandlord(@PathVariable Long userId) {
        logger.info("Approval request for user ID {}", userId);
        User user = userRepository.findById(userId).orElseThrow();

        if (!"ROLE_LANDLORD".equals(user.getRole())) {
            logger.warn("Cannot approve user {}: not a landlord (role={})", userId, user.getRole());
            return ResponseEntity.badRequest().body("Only landlords can be approved");
        }

        user.setApproved(true);
        userRepository.save(user);
        logger.info("Landlord {} approved successfully", user.getEmail());
        return ResponseEntity.ok("Landlord approved successfully");
    }

    @DeleteMapping("/reject/{userId}")
    public ResponseEntity<?> rejectLandlord(@PathVariable Long userId) {
        logger.info("Rejection request for user ID {}", userId);
        User user = userRepository.findById(userId).orElseThrow();
        userRepository.delete(user);
        logger.info("Landlord {} rejected and deleted", user.getEmail());
        return ResponseEntity.ok("Landlord rejected and deleted");
    }

    @GetMapping("/pending-landlords")
    public ResponseEntity<List<User>> getUnapprovedLandlords() {
        logger.info("Fetching list of pending landlords");
        List<User> unapproved = userRepository.findByRoleAndIsApprovedFalse("ROLE_LANDLORD");
        logger.debug("Found {} pending landlords", unapproved.size());
        return ResponseEntity.ok(unapproved);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        logger.info("Fetching current user for principal {}", principal.getName());
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        return ResponseEntity.ok(user);
    }
}
