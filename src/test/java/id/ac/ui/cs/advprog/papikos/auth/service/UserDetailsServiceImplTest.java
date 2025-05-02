package id.ac.ui.cs.advprog.papikos.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@SpringBootTest
class UserDetailsServiceImplTest {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent@example.com");
        });
    }

    @Test
    void testLoadUserByUsername_Found() {
        // Create and save a test user.
        User user = new User();
        user.setEmail("found@example.com");
        user.setPassword("password123");
        user.setRole("TENANT");
        userRepository.save(user);

        var userDetails = userDetailsService.loadUserByUsername("found@example.com");
        assertNotNull(userDetails, "UserDetails should not be null");
        assertEquals("found@example.com", userDetails.getUsername());
    }
}
