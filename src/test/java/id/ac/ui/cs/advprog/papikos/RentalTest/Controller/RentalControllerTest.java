package id.ac.ui.cs.advprog.papikos.RentalTest.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import id.ac.ui.cs.advprog.papikos.house.Rental.controller.RentalController;
import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.RentalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RentalControllerTest {

    @Mock private RentalService rentalService;
    @InjectMocks private RentalController controller;
    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private final Long sampleId = 1L; //  pakai Long

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
    void setup() {
        MockitoAnnotations.openMocks(this);
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new TestExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .build();
    }

    @Test
    void testGetAllRentals() throws Exception {
        Rental r1 = createRental("A");
        Rental r2 = createRental("B");
        when(rentalService.getAllRentals()).thenReturn(Arrays.asList(r1, r2));

        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].houseId").value("houseB"));
    }

    @Test
    void testGetById_Found() throws Exception {
        Rental r = createRental("X");
        when(rentalService.getRentalById(sampleId)).thenReturn(Optional.of(r));

        mockMvc.perform(get("/api/rentals/" + sampleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("NameX"));
    }

    @Test
    void testGetById_NotFound() throws Exception {
        when(rentalService.getRentalById(sampleId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/rentals/" + sampleId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateRental() throws Exception {
        Rental in = createRental("New");
        in.setId(null);
        Rental out = createRental("New");
        when(rentalService.createRental(any(Rental.class))).thenReturn(out);

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(out.getId())) //  Gak perlu toString()
                .andExpect(jsonPath("$.durationInMonths").value(1));
    }

    @Test
    void testUpdateRental_Success() throws Exception {
        Rental update = createRental("U");
        when(rentalService.updateRental(eq(sampleId), any(Rental.class))).thenReturn(update);

        mockMvc.perform(put("/api/rentals/" + sampleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.houseId").value("houseU"));
    }

    @Test
    void testUpdateRental_NotFound() throws Exception {
        Rental update = createRental("U");
        when(rentalService.updateRental(eq(sampleId), any(Rental.class)))
                .thenThrow(new RuntimeException("not found"));

        mockMvc.perform(put("/api/rentals/" + sampleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteRental() throws Exception {
        doNothing().when(rentalService).deleteRental(sampleId);

        mockMvc.perform(delete("/api/rentals/" + sampleId))
                .andExpect(status().isNoContent());
    }

    private Rental createRental(String suffix) {
        Rental r = new Rental();
        r.setId(sampleId); //  pakai Long
        r.setHouseId("house" + suffix);
        r.setFullName("Name" + suffix);
        r.setPhoneNumber("08123");
        r.setCheckInDate(LocalDate.of(2025, 6, 1));
        r.setDurationInMonths(1);
        r.setApproved(false);
        return r;
    }
}
