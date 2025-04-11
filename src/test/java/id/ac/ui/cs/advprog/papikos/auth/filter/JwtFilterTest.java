package id.ac.ui.cs.advprog.papikos.auth.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import id.ac.ui.cs.advprog.papikos.auth.service.UserDetailsServiceImpl;
import id.ac.ui.cs.advprog.papikos.auth.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
        // Check if the test user exists; if not, create one.
        if (!userRepository.existsByEmail("found@example.com")) {
            User user = new User();
            user.setEmail("found@example.com");
            // Make sure to encode the password if your security configuration expects so.
            user.setPassword(new BCryptPasswordEncoder().encode("password"));
            user.setRole("USER");
            userRepository.save(user);
        }
    }

    @Test
    void testDoFilterValidToken() throws Exception {
        // Now load the test user (it should be found)
        UserDetails userDetails = userDetailsService.loadUserByUsername("found@example.com");
        String token = jwtUtil.generateToken(userDetails);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtFilter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication(),
                "Security context should have authentication set");
        assertEquals(userDetails.getUsername(),
                SecurityContextHolder.getContext().getAuthentication().getName(),
                "Authenticated username should match");
    }
}
