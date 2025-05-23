package id.ac.ui.cs.advprog.papikos.auth.controller;

import id.ac.ui.cs.advprog.papikos.auth.dto.AuthRequest;
import id.ac.ui.cs.advprog.papikos.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.papikos.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.papikos.auth.dto.UserProfileDTO;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.auth.service.UserDetailsServiceImpl;
import id.ac.ui.cs.advprog.papikos.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        System.out.println("Received register request: " + registerRequest);
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("User already exists");
        }
        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setRole(registerRequest.getRole());
        newUser.setFullName(registerRequest.getFullName());
        newUser.setPhoneNumber(registerRequest.getPhoneNumber());

        if ("ROLE_LANDLORD".equals(newUser.getRole())) {
            newUser.setApproved(false);
        } else {
            newUser.setApproved(true);
        }

        userRepository.save(newUser);
        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/register")
    public ResponseEntity<?> registerGet() {
        return ResponseEntity.ok("Register endpoint is public");
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        // Only return email and balance (or other fields you want to expose)
        return ResponseEntity.ok(new UserProfileDTO(user.getId(), user.getEmail(), user.getBalance()));

    }

}
