package id.ac.ui.cs.advprog.papikos.RentalTest.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.house.Rental.controller.BoardingHouseController;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.BoardingHouseService;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BoardingHouseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BoardingHouseService boardingHouseService;

    @InjectMocks
    private BoardingHouseController controller;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testListHouses() throws Exception {
        House h1 = new House(1L, "Kos A", "Alamat A", "Desc A", 2, 100.0, "imgA");
        House h2 = new House(2L, "Kos B", "Alamat B", "Desc B", 3, 200.0, "imgB");
        when(boardingHouseService.findAll()).thenReturn(Arrays.asList(h1, h2));

        mockMvc.perform(get("/api/houses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Kos A"));
    }

    @Test
    void testGetHouseById() throws Exception {
        House h = new House(1L, "Kos C", "Alamat C", "Desc C", 1, 150.0, "imgC");
        when(boardingHouseService.findById(1L)).thenReturn(java.util.Optional.of(h));

        mockMvc.perform(get("/api/houses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("Alamat C"));
    }

    @Test
    void testCreateHouse() throws Exception {
        House input = new House("Kos D", "Alamat D", "Desc D", 4, 300.0, "imgD");
        House created = new House(5L, "Kos D", "Alamat D", "Desc D", 4, 300.0, "imgD");
        when(boardingHouseService.create(input)).thenReturn(created);

        mockMvc.perform(post("/api/houses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Kos D"));
    }

    @Test
    void testUpdateHouse() throws Exception {
        House updated = new House(1L, "Kos E", "Alamat E", "Desc E", 2, 120.0, "imgE");
        when(boardingHouseService.update(1L, updated)).thenReturn(updated);

        mockMvc.perform(put("/api/houses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kos E"));
    }

    @Test
    void testDeleteHouse() throws Exception {
        doNothing().when(boardingHouseService).delete(1L);

        mockMvc.perform(delete("/api/houses/1"))
                .andExpect(status().isNoContent());
    }
}
