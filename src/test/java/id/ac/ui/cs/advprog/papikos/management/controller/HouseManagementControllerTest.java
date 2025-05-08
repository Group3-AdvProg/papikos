package id.ac.ui.cs.advprog.papikos.house.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.house.management.service.HouseManagementService;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class HouseManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HouseManagementService houseManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    private House house;

    @BeforeEach
    void setUp() {
        house = new House();
        house.setId(1L);
        house.setName("Kos A");
        house.setAddress("Jl. UI");
        house.setDescription("Dekat kampus");
        house.setNumberOfRooms(10);
        house.setMonthlyRent(1500000.0);
        house.setImageUrl("https://example.com/img.jpg");
    }

    @Test
    void testCreateHousePage() throws Exception {
        when(houseManagementService.findAll()).thenReturn(Arrays.asList(house));

        mockMvc.perform(get("/api/management/houses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Kos A"));
    }

    @Test
    void testCreateHousePost() throws Exception {
        doNothing().when(houseManagementService).addHouse(any(House.class));

        mockMvc.perform(post("/api/management/houses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(house)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kos A"));
    }

    @Test
    void testManagementPage() throws Exception {
        when(houseManagementService.findById(1L)).thenReturn(Optional.of(house));

        mockMvc.perform(get("/api/management/houses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kos A"));
    }

    @Test
    void testEditHousePage() throws Exception {
        when(houseManagementService.findById(1L)).thenReturn(Optional.of(house));

        mockMvc.perform(put("/api/management/houses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(house)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kos A"));
    }

    @Test
    void testEditHousePost() throws Exception {
        when(houseManagementService.findById(1L)).thenReturn(Optional.of(house));

        mockMvc.perform(put("/api/management/houses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(house)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kos A"));

        verify(houseManagementService, times(1)).updateHouse(eq(1L), any(House.class));
    }

    @Test
    void testDeleteHouse() throws Exception {
        doNothing().when(houseManagementService).deleteHouse(1L);

        mockMvc.perform(delete("/api/management/houses/1"))
                .andExpect(status().isNoContent());

        verify(houseManagementService, times(1)).deleteHouse(1L);
    }
}
