package id.ac.ui.cs.advprog.papikos.auth.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.auth.service.UserDetailsServiceImpl;
import id.ac.ui.cs.advprog.papikos.auth.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class JwtFilterTest {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setupTestUser() {
        // Clear any leftover authentication
        SecurityContextHolder.clearContext();

        // Ensure our test user exists
        if (!userRepository.existsByEmail("found@example.com")) {
            User user = new User();
            user.setEmail("found@example.com");
            user.setPassword(new BCryptPasswordEncoder().encode("password"));
            user.setRole("USER");
            user.setFullName("Found User");
            user.setPhoneNumber("081234567890");
            userRepository.save(user);
        }
    }

    @Test
    void testDoFilterValidToken() throws Exception {
        // Arrange
        UserDetails userDetails = userDetailsService.loadUserByUsername("found@example.com");
        String token = jwtUtil.generateToken(userDetails);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // Act
        jwtFilter.doFilter(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication(),
                "Security context should have authentication set");
        assertEquals(userDetails.getUsername(),
                SecurityContextHolder.getContext().getAuthentication().getName(),
                "Authenticated username should match");
    }

    @Test
    void testDoFilterInvalidToken_shouldHandleExceptionGracefully() throws Exception {
        // Arrange: an invalid JWT that will throw inside extractUsername()
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid.token.value");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // Act & Assert: should not bubble up an exception, and auth remains null
        jwtFilter.doFilter(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Security context should have no authentication for invalid token");
    }
}
