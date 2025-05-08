package id.ac.ui.cs.advprog.papikos.RentalTest.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.house.Rental.controller.TenantController;
import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.TenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TenantControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TenantService tenantService;

    @InjectMocks
    private TenantController controller;

    private ObjectMapper mapper = new ObjectMapper();
    private UUID sampleId = UUID.randomUUID();

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testList() throws Exception {
        Tenant t1 = new Tenant("A","081");
        Tenant t2 = new Tenant("B","082");
        t1.setId(sampleId);
        t2.setId(UUID.randomUUID());
        when(tenantService.getAllTenants()).thenReturn(Arrays.asList(t1, t2));

        mockMvc.perform(get("/api/tenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetById() throws Exception {
        Tenant t = new Tenant("X","083");
        t.setId(sampleId);
        when(tenantService.getTenantById(sampleId)).thenReturn(Optional.of(t));

        mockMvc.perform(get("/api/tenants/" + sampleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("X"));
    }

    @Test
    void testCreate() throws Exception {
        Tenant out = new Tenant("New","084");
        out.setId(sampleId);
        when(tenantService.createTenant(any(Tenant.class))).thenReturn(out);

        Tenant in = new Tenant("New","084");
        mockMvc.perform(post("/api/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleId.toString()));
    }

    @Test
    void testUpdate() throws Exception {
        Tenant upd = new Tenant("Y","085");
        upd.setId(sampleId);
        when(tenantService.updateTenant(eq(sampleId), any(Tenant.class))).thenReturn(upd);

        mockMvc.perform(put("/api/tenants/" + sampleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(upd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value("085"));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(tenantService).deleteTenant(sampleId);

        mockMvc.perform(delete("/api/tenants/" + sampleId))
                .andExpect(status().isNoContent());
    }
}
