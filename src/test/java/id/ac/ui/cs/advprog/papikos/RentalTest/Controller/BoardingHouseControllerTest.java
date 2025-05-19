package id.ac.ui.cs.advprog.papikos.RentalTest.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.house.Rental.controller.BoardingHouseController;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.BoardingHouseService;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BoardingHouseControllerTest {

    @Mock
    private BoardingHouseService service;

    @InjectMocks
    private BoardingHouseController controller;

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .build();
    }

    private House makeHouse(Long id, String name) {
        House h = new House();
        h.setId(id);
        h.setName(name);
        h.setAddress(name + " Addr");
        h.setDescription(name + " Desc");
        h.setNumberOfRooms(2);
        h.setMonthlyRent(150.0);
        h.setImageUrl(name + ".png");
        return h;
    }

    @Test
    void testListHouses() throws Exception {
        House h1 = makeHouse(1L, "A");
        House h2 = makeHouse(2L, "B");
        when(service.findAll()).thenReturn(Arrays.asList(h1, h2));

        mockMvc.perform(get("/api/houses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("A"));
    }

    @Test
    void testGetById_Found() throws Exception {
        House h = makeHouse(42L, "X");
        when(service.findById(42L)).thenReturn(Optional.of(h));

        mockMvc.perform(get("/api/houses/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("X Addr"));
    }

    @Test
    void testGetById_NotFound() throws Exception {
        when(service.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/houses/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateHouse() throws Exception {
        House in = makeHouse(null, "New");
        in.setId(null);
        House out = makeHouse(7L, "New");
        when(service.create(any(House.class))).thenReturn(out);

        mockMvc.perform(post("/api/houses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("New"));
    }

    @Test
    void testUpdateHouse_Success() throws Exception {
        House updated = makeHouse(5L, "Upd");
        when(service.update(eq(5L), any(House.class))).thenReturn(updated);

        mockMvc.perform(put("/api/houses/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Upd Desc"));
    }

    @Test
    void testUpdateHouse_NotFound() throws Exception {
        House body = makeHouse(5L, "Nope");
        when(service.update(eq(5L), any(House.class)))
                .thenThrow(new RuntimeException("House not found"));

        mockMvc.perform(put("/api/houses/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteHouse() throws Exception {
        doNothing().when(service).delete(123L);

        mockMvc.perform(delete("/api/houses/123"))
                .andExpect(status().isNoContent());
    }
}
