package id.ac.ui.cs.advprog.papikos.auth.controller;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/auth/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PutMapping("/approve/{userId}")
    public ResponseEntity<?> approveLandlord(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        if (!"ROLE_LANDLORD".equals(user.getRole())) {
            return ResponseEntity.badRequest().body("Only landlords can be approved");
        }

        user.setApproved(true);
        userRepository.save(user);

        return ResponseEntity.ok("Landlord approved successfully");
    }

    @DeleteMapping("/reject/{userId}")
    public ResponseEntity<?> rejectLandlord(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        userRepository.delete(user);
        return ResponseEntity.ok("Landlord rejected and deleted");
    }

    @GetMapping("/pending-landlords")
    public ResponseEntity<List<User>> getUnapprovedLandlords() {
        List<User> unapproved = userRepository.findByRoleAndIsApprovedFalse("ROLE_LANDLORD");
        return ResponseEntity.ok(unapproved);
    }
}
