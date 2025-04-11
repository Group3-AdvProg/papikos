package id.ac.ui.cs.advprog.papikos.RentalTest.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.Rental.controller.BoardingHouseController;
import id.ac.ui.cs.advprog.papikos.Rental.model.BoardingHouse;
import id.ac.ui.cs.advprog.papikos.Rental.service.BoardingHouseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BoardingHouseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BoardingHouseService boardingHouseService;

    @InjectMocks
    private BoardingHouseController boardingHouseController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(boardingHouseController).build();
    }

    @Test
    void testCreateBoardingHouse() throws Exception {
        BoardingHouse house = new BoardingHouse();
        house.setName("Kos A");

        when(boardingHouseService.create(any(BoardingHouse.class))).thenReturn(house);

        mockMvc.perform(post("/boardinghouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(house)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kos A"));
    }

    @Test
    void testFindAllBoardingHouses() throws Exception {
        BoardingHouse house1 = new BoardingHouse();
        house1.setName("Kos A");

        BoardingHouse house2 = new BoardingHouse();
        house2.setName("Kos B");

        List<BoardingHouse> houses = Arrays.asList(house1, house2);
        when(boardingHouseService.findAll()).thenReturn(houses);

        mockMvc.perform(get("/boardinghouses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testFindBoardingHouseById_found() throws Exception {
        BoardingHouse house = new BoardingHouse();
        house.setName("Kos C");

        when(boardingHouseService.findById(1L)).thenReturn(Optional.of(house));

        mockMvc.perform(get("/boardinghouses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kos C"));
    }

    @Test
    void testFindBoardingHouseById_notFound() throws Exception {
        when(boardingHouseService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/boardinghouses/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateBoardingHouse() throws Exception {
        BoardingHouse updated = new BoardingHouse();
        updated.setName("Kos D");

        when(boardingHouseService.update(eq(1L), any(BoardingHouse.class))).thenReturn(updated);

        mockMvc.perform(put("/boardinghouses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kos D"));
    }

    @Test
    void testDeleteBoardingHouse() throws Exception {
        doNothing().when(boardingHouseService).delete(1L);

        mockMvc.perform(delete("/boardinghouses/1"))
                .andExpect(status().isNoContent());
    }
}
