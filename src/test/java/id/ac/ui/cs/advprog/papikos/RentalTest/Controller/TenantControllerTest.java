package id.ac.ui.cs.advprog.papikos.RentalTest.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.Rental.controller.TenantController;
import id.ac.ui.cs.advprog.papikos.Rental.model.Tenant;
import id.ac.ui.cs.advprog.papikos.Rental.service.TenantService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TenantController.class)
public class TenantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TenantService tenantService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllTenants() throws Exception {
        Tenant tenant = new Tenant();
        tenant.setId(1L);
        tenant.setFullName("John Doe");
        tenant.setPhoneNumber("08123456789");

        when(tenantService.getAllTenants()).thenReturn(List.of(tenant));

        mockMvc.perform(get("/tenant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$[0].phoneNumber").value("08123456789"));
    }

    @Test
    public void testGetTenantById() throws Exception {
        Tenant tenant = new Tenant();
        tenant.setId(1L);
        tenant.setFullName("John Doe");
        tenant.setPhoneNumber("08123456789");

        when(tenantService.getTenantById(1L)).thenReturn(tenant);

        mockMvc.perform(get("/tenant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("08123456789"));
    }

    @Test
    public void testCreateTenant() throws Exception {
        Tenant tenant = new Tenant();
        tenant.setId(1L);
        tenant.setFullName("John Doe");
        tenant.setPhoneNumber("08123456789");

        when(tenantService.createTenant(Mockito.any(Tenant.class))).thenReturn(tenant);

        mockMvc.perform(post("/tenant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tenant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("08123456789"));
    }

    @Test
    public void testUpdateTenant() throws Exception {
        Tenant tenant = new Tenant();
        tenant.setId(1L);
        tenant.setFullName("Jane Doe");
        tenant.setPhoneNumber("0822334455");

        when(tenantService.updateTenant(Mockito.eq(1L), Mockito.any(Tenant.class))).thenReturn(tenant);

        mockMvc.perform(put("/tenant/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tenant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Jane Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("0822334455"));
    }

    @Test
    public void testDeleteTenant() throws Exception {
        mockMvc.perform(delete("/tenant/1"))
                .andExpect(status().isOk());
    }
}
