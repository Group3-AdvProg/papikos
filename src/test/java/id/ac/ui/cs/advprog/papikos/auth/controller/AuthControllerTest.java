package id.ac.ui.cs.advprog.papikos.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.auth.dto.AuthRequest;
import id.ac.ui.cs.advprog.papikos.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.auth.util.JwtUtil;
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

    @Autowired
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private void registerUser(RegisterRequest request) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        return json.get("token").asText();
    }

    @Test
    void testLoginWithValidCredentials() throws Exception {
        RegisterRequest regRequest = new RegisterRequest();
        regRequest.setEmail("validuser@example.com");
        regRequest.setPassword("validpass");
        regRequest.setRole("ROLE_TENANT");
        regRequest.setFullName("Valid User");
        regRequest.setPhoneNumber("081234567892");

        registerUser(regRequest);

        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setEmail("validuser@example.com");
        loginRequest.setPassword("validpass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

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

        registerUser(regRequest);

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

        registerUser(regRequest);

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

        registerUser(regRequest); // First success
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isBadRequest()); // Duplicate
    }

    @Test
    void testGetCurrentUserWithValidToken() throws Exception {
        RegisterRequest regRequest = new RegisterRequest();
        regRequest.setEmail("meuser@example.com");
        regRequest.setPassword("mypassword");
        regRequest.setRole("ROLE_TENANT");
        regRequest.setFullName("Me User");
        regRequest.setPhoneNumber("081234567893");

        registerUser(regRequest);
        String token = loginAndGetToken("meuser@example.com", "mypassword");

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCurrentUserWithNoToken() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "")) // empty string triggers !startsWith("Bearer ")
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .contains("Missing or invalid token"));
    }

    @Test
    void testGetCurrentUserWithInvalidPrefixToken() throws Exception {
        String malformedHeader = "Token fake.jwt.value";

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", malformedHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .contains("Missing or invalid token"));
    }

    @Autowired
    private AuthController authController; // Tambahkan ini di atas

    @Test
    void testGetCurrentUserWithDeletedUserToken_directCall() throws Exception {
        // 1. Register user
        RegisterRequest regRequest = new RegisterRequest();
        regRequest.setEmail("deleteduser@example.com");
        regRequest.setPassword("deletepass");
        regRequest.setRole("ROLE_TENANT");
        regRequest.setFullName("Deleted User");
        regRequest.setPhoneNumber("0899999999");

        registerUser(regRequest);

        // 2. Login and get token
        String token = loginAndGetToken("deleteduser@example.com", "deletepass");

        // 3. Delete user from DB
        userRepository.delete(userRepository.findByEmail("deleteduser@example.com").orElseThrow());

        // 4. Call controller directly (bypass JwtFilter)
        var response = authController.getCurrentUser("Bearer " + token);

        assertThat(response.getStatusCodeValue()).isEqualTo(401);
        assertThat(response.getBody().toString()).contains("User not found");
    }


    @Test
    void testPublicRegisterEndpointAccessible() throws Exception {
        mockMvc.perform(get("/api/auth/register"))
                .andExpect(status().isOk());
    }
}
