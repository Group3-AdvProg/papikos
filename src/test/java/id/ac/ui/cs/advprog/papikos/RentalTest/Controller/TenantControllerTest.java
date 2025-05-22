package id.ac.ui.cs.advprog.papikos.RentalTest.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.house.Rental.controller.TenantController;
import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.TenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*; //  Make sure this is imported
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TenantControllerTest {

    @Mock private TenantService tenantService;
    @InjectMocks private TenantController controller;

    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private final Long sampleId = 100L;

    @RestControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<String> handleRuntime(RuntimeException ex) {
            if (ex.getMessage().toLowerCase().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.internalServerError().body("Unexpected error: " + ex.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new TestExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .build();
    }

    private Tenant makeTenant(String name, String phone, String email) {
        Tenant t = new Tenant(name, phone);
        t.setId(sampleId);
        t.setEmail(email);
        t.setPassword("secure123");
        t.setRole("TENANT");
        t.setBalance(1000.0);
        return t;
    }

    @Test
    void testListTenants() throws Exception {
        Tenant t1 = makeTenant("Alice", "081", "alice@mail.com");
        Tenant t2 = makeTenant("Bob", "082", "bob@mail.com");
        t2.setId(101L);

        when(tenantService.getAllTenants()).thenReturn(Arrays.asList(t1, t2));

        mockMvc.perform(get("/api/tenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fullName").value("Alice"));
    }

    @Test
    void testGetById_Found() throws Exception {
        Tenant t = makeTenant("Carol", "083", "carol@mail.com");
        when(tenantService.getTenantById(sampleId)).thenReturn(Optional.of(t));

        mockMvc.perform(get("/api/tenants/" + sampleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value("083"));
    }

    @Test
    void testGetById_NotFound() throws Exception {
        when(tenantService.getTenantById(sampleId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tenants/" + sampleId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTenant() throws Exception {
        Tenant in = makeTenant("Dave", "084", "dave@mail.com");
        Tenant out = makeTenant("Dave", "084", "dave@mail.com");
        out.setId(sampleId);

        when(tenantService.createTenant(any(Tenant.class))).thenReturn(out);

        mockMvc.perform(post("/api/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleId))
                .andExpect(jsonPath("$.fullName").value("Dave"));
    }

    @Test
    void testUpdateTenant_Success() throws Exception {
        Tenant in = makeTenant("Eve", "085", "eve@mail.com");
        when(tenantService.updateTenant(eq(sampleId), any(Tenant.class))).thenReturn(in);

        mockMvc.perform(put("/api/tenants/" + sampleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Eve"));
    }

    @Test
    void testUpdateTenant_NotFound() throws Exception {
        Tenant in = makeTenant("Eve", "085", "eve@mail.com");

        when(tenantService.updateTenant(eq(sampleId), any(Tenant.class)))
                .thenThrow(new RuntimeException("Tenant not found"));

        mockMvc.perform(put("/api/tenants/" + sampleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(in)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTenant() throws Exception {
        doNothing().when(tenantService).deleteTenant(sampleId);

        mockMvc.perform(delete("/api/tenants/" + sampleId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testAddAndRemoveRentalHelpers() {
        Tenant tenant = new Tenant("Fiona", "086");
        tenant.setId(200L);

        Rental rental = new Rental();
        rental.setId(1L);
        rental.setHouseId("H001");

        tenant.addRental(rental);
        assertEquals(1, tenant.getRentals().size());
        assertEquals(tenant, rental.getTenant());

        tenant.removeRental(rental);
        assertTrue(tenant.getRentals().isEmpty());
        assertNull(rental.getTenant());
    }
}
