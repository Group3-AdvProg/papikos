package id.ac.ui.cs.advprog.papikos.RentalTest.Controller;

import id.ac.ui.cs.advprog.papikos.Rental.controller.RentalController;
import id.ac.ui.cs.advprog.papikos.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.Rental.service.RentalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(RentalController.class)
public class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalService rentalService;

    private Rental sampleRental;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        sampleRental = new Rental();
        sampleRental.setId(1L);
        sampleRental.setDurationInMonths(2);
        sampleRental.setCheckInDate(LocalDate.of(2025, 4, 11));

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreateRental() throws Exception {
        when(rentalService.createRental(any(Rental.class))).thenReturn(sampleRental);

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRental)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetAllRentals() throws Exception {
        when(rentalService.getAllRentals()).thenReturn(Arrays.asList(sampleRental));

        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetRentalById() throws Exception {
        when(rentalService.getRentalById(1L)).thenReturn(Optional.of(sampleRental));

        mockMvc.perform(get("/api/rentals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.durationInMonths").value(2));
    }

    @Test
    void testUpdateRental() throws Exception {
        Rental updatedRental = new Rental();
        updatedRental.setId(1L);
        updatedRental.setDurationInMonths(3);
        updatedRental.setCheckInDate(LocalDate.of(2025, 5, 1));

        when(rentalService.updateRental(eq(1L), any(Rental.class))).thenReturn(updatedRental);

        mockMvc.perform(put("/api/rentals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRental)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.durationInMonths").value(3))
                .andExpect(jsonPath("$.checkInDate").value("2025-05-01"));
    }

    @Test
    void testCancelRental() throws Exception {
        doNothing().when(rentalService).cancelRental(1L);

        mockMvc.perform(delete("/api/rentals/1"))
                .andExpect(status().isOk());
    }
}
