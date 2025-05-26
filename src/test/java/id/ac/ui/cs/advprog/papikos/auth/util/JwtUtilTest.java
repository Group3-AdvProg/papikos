package id.ac.ui.cs.advprog.papikos.auth.util;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    private SecretKey secretKey;
    private UserDetails userDetails;

    @BeforeEach
    void setup() throws Exception {
        // grab the key so our test tokens use the same signing key
        Field keyField = JwtUtil.class.getDeclaredField("secretKey");
        keyField.setAccessible(true);
        secretKey = (SecretKey) keyField.get(jwtUtil);

        userDetails = new User("testUser", "", Collections.emptyList());
    }

    @Test
    void generateToken_and_extractUsername() {
        String token = jwtUtil.generateToken(userDetails);
        assertEquals("testUser", jwtUtil.extractUsername(token));
    }

    @Test
    void validateToken_valid() {
        String token = jwtUtil.generateToken(userDetails);
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void validateToken_invalidUsername() {
        String otherToken = Jwts.builder()
                .setSubject("otherUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(secretKey)
                .compact();

        assertFalse(jwtUtil.validateToken(otherToken, userDetails));
    }

    @Test
    void validateToken_expired_throwsExpiredJwtException() {
        String expiredToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 120_000))
                .setExpiration(new Date(System.currentTimeMillis() - 1_000))
                .signWith(secretKey)
                .compact();

        assertThrows(ExpiredJwtException.class,
                () -> jwtUtil.validateToken(expiredToken, userDetails));
    }
}
