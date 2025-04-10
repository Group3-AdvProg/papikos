package id.ac.ui.cs.advprog.papikos.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import id.ac.ui.cs.advprog.papikos.auth.dto.AuthRequest;
import id.ac.ui.cs.advprog.papikos.auth.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    void testRegisterNewUser() throws Exception {
        RegisterRequest regRequest = new RegisterRequest();
        regRequest.setEmail("newuser@example.com");
        regRequest.setPassword("password123");
        regRequest.setRole("TENANT");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testRegisterDuplicateUser() throws Exception {
        RegisterRequest regRequest = new RegisterRequest();
        regRequest.setEmail("duplicate@example.com");
        regRequest.setPassword("password123");
        regRequest.setRole("TENANT");

        // First registration should succeed.
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isOk());

        // Duplicate registration should return Bad Request.
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPublicEndpointAccessible() throws Exception {
        // GET /api/auth/register is public.
        mockMvc.perform(get("/api/auth/register"))
                .andExpect(status().isOk());
    }
}
