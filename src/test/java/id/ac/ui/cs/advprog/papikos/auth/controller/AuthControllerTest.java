package id.ac.ui.cs.advprog.papikos.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.auth.dto.AuthRequest;
import id.ac.ui.cs.advprog.papikos.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("nonexistent@example.com");
        authRequest.setPassword("wrongpass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRegisterNewTenant() throws Exception {
        RegisterRequest regRequest = new RegisterRequest();
        regRequest.setEmail("tenant@example.com");
        regRequest.setPassword("password123");
        regRequest.setRole("ROLE_TENANT");
        regRequest.setFullName("Tenant User");
        regRequest.setPhoneNumber("081234567890");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isOk());

        User savedUser = userRepository.findByEmail("tenant@example.com").orElseThrow();
        assertThat(savedUser.isApproved()).isTrue();
    }

    @Test
    void testRegisterNewLandlordShouldBeUnapproved() throws Exception {
        RegisterRequest regRequest = new RegisterRequest();
        regRequest.setEmail("landlord@example.com");
        regRequest.setPassword("securepass");
        regRequest.setRole("ROLE_LANDLORD");
        regRequest.setFullName("Landlord User");
        regRequest.setPhoneNumber("0811111111");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isOk());

        User savedUser = userRepository.findByEmail("landlord@example.com").orElseThrow();
        assertThat(savedUser.isApproved()).isFalse();
    }

    @Test
    void testRegisterDuplicateUser() throws Exception {
        RegisterRequest regRequest = new RegisterRequest();
        regRequest.setEmail("dupe@example.com");
        regRequest.setPassword("password123");
        regRequest.setRole("ROLE_TENANT");
        regRequest.setFullName("Dup User");
        regRequest.setPhoneNumber("081234567891");

        // First registration should succeed
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isOk());

        // Duplicate registration should return Bad Request
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPublicRegisterEndpointAccessible() throws Exception {
        mockMvc.perform(get("/api/auth/register"))
                .andExpect(status().isOk());
    }
}
