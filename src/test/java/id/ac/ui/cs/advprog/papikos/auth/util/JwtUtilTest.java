package id.ac.ui.cs.advprog.papikos.auth.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testGenerateAndValidateToken() {
        // Create a dummy UserDetails instance.
        UserDetails userDetails = new User("dummy@example.com", "password",
                new ArrayList<SimpleGrantedAuthority>() {{
                    add(new SimpleGrantedAuthority("ROLE_TENANT"));
                }});

        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token, "Token should not be null");

        boolean valid = jwtUtil.validateToken(token, userDetails);
        assertTrue(valid, "Token should be valid");
    }

    @Test
    void testExtractUsername() {
        UserDetails userDetails = new User("dummy@example.com", "password", new ArrayList<>());
        String token = jwtUtil.generateToken(userDetails);
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals("dummy@example.com", extractedUsername);
    }
}
