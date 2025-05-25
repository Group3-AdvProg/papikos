package id.ac.ui.cs.advprog.papikos.RentalTest.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.house.rental.controller.BoardingHouseController;
import id.ac.ui.cs.advprog.papikos.house.rental.service.BoardingHouseService;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

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

    // Handler untuk runtime error jadi 404
    @RestControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<String> handleRuntime(RuntimeException ex) {
            if (ex.getMessage().contains("House not found")) {
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
}
