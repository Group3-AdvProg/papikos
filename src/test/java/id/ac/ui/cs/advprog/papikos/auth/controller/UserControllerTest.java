package id.ac.ui.cs.advprog.papikos.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User pendingLandlord;

    @BeforeEach
    void setup() {
        // Clean up and insert one unapproved landlord before each test
        userRepository.deleteAll();

        pendingLandlord = new User();
        pendingLandlord.setEmail("testlandlord@example.com");
        pendingLandlord.setPassword("dummy");
        pendingLandlord.setRole("ROLE_LANDLORD");
        pendingLandlord.setFullName("Test Landlord");
        pendingLandlord.setPhoneNumber("081234567890");
        pendingLandlord.setApproved(false);
        userRepository.save(pendingLandlord);
    }

    @Test
    void testApproveLandlord() throws Exception {
        mockMvc.perform(put("/api/auth/users/approve/" + pendingLandlord.getId()))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(pendingLandlord.getId()).orElseThrow();
        assertThat(updatedUser.isApproved()).isTrue();
    }

    @Test
    void testRejectLandlord() throws Exception {
        mockMvc.perform(delete("/api/auth/users/reject/" + pendingLandlord.getId()))
                .andExpect(status().isOk());

        boolean exists = userRepository.existsById(pendingLandlord.getId());
        assertThat(exists).isFalse();
    }

    @Test
    void testGetPendingLandlords() throws Exception {
        mockMvc.perform(get("/api/auth/users/pending-landlords"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("testlandlord@example.com"));
    }

    @Test
    void testApproveNonLandlordShouldFail() throws Exception {
        User tenant = new User();
        tenant.setEmail("tenant@example.com");
        tenant.setPassword("dummy");
        tenant.setRole("ROLE_TENANT");
        tenant.setFullName("Some Tenant");
        tenant.setPhoneNumber("08222222222");
        tenant.setApproved(false);
        userRepository.save(tenant);

        mockMvc.perform(put("/api/auth/users/approve/" + tenant.getId()))
                .andExpect(status().isBadRequest());
    }
}
