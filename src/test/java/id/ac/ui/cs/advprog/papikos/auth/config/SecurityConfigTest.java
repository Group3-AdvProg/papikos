package id.ac.ui.cs.advprog.papikos.auth.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testProtectedEndpointRequiresAuthentication() throws Exception {
        // Attempt to access a secured endpoint (adjust "/api/protected" as needed).
        mockMvc.perform(get("/api/protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testPublicEndpointIsAccessible() throws Exception {
        // /api/auth/register is configured as public.
        mockMvc.perform(get("/api/auth/register"))
                .andExpect(status().isOk());
    }
}
