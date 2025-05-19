package id.ac.ui.cs.advprog.papikos.RentalTest.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.house.Rental.controller.TenantController;
import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
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
    private final UUID sampleId = UUID.randomUUID();

    // Local exception handler untuk unit test
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
                .setControllerAdvice(new TestExceptionHandler()) // ✅ inject error handler
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .build();
    }

    @Test
    void testListTenants() throws Exception {
        Tenant t1 = new Tenant("Alice", "081");
        Tenant t2 = new Tenant("Bob", "082");
        t1.setId(sampleId);
        t2.setId(UUID.randomUUID());
        when(tenantService.getAllTenants()).thenReturn(Arrays.asList(t1, t2));

        mockMvc.perform(get("/api/tenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fullName").value("Alice"));
    }

    @Test
    void testGetById_Found() throws Exception {
        Tenant t = new Tenant("Carol", "083");
        t.setId(sampleId);
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
        Tenant in = new Tenant("Dave", "084");
        Tenant out = new Tenant("Dave", "084");
        out.setId(sampleId);
        when(tenantService.createTenant(any(Tenant.class))).thenReturn(out);

        mockMvc.perform(post("/api/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleId.toString()))
                .andExpect(jsonPath("$.fullName").value("Dave"));
    }

    @Test
    void testUpdateTenant_Success() throws Exception {
        Tenant in = new Tenant("Eve", "085");
        in.setId(sampleId);
        when(tenantService.updateTenant(eq(sampleId), any(Tenant.class))).thenReturn(in);

        mockMvc.perform(put("/api/tenants/" + sampleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Eve"));
    }

    @Test
    void testUpdateTenant_NotFound() throws Exception {
        Tenant in = new Tenant("Eve", "085");
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
}
