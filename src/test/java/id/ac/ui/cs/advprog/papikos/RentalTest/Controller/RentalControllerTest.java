package id.ac.ui.cs.advprog.papikos.RentalTest.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.house.Rental.controller.RentalController;
import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.RentalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RentalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RentalService rentalService;

    @InjectMocks
    private RentalController controller;

    private ObjectMapper mapper = new ObjectMapper();

    private UUID sampleId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetAll() throws Exception {
        Rental r1 = createRental("A");
        Rental r2 = createRental("B");
        when(rentalService.getAllRentals()).thenReturn(Arrays.asList(r1, r2));

        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetById() throws Exception {
        Rental r = createRental("X");
        when(rentalService.getRentalById(sampleId)).thenReturn(Optional.of(r));

        mockMvc.perform(get("/api/rentals/" + sampleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.houseId").value("houseX"));
    }

    @Test
    void testCreate() throws Exception {
        Rental in = createRental("New");
        in.setId(null);
        Rental out = createRental("New");
        when(rentalService.createRental(in)).thenReturn(out);

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.houseId").value("houseNew"));
    }

    @Test
    void testUpdate() throws Exception {
        Rental update = createRental("U");
        when(rentalService.updateRental(sampleId, update)).thenReturn(update);

        mockMvc.perform(put("/api/rentals/" + sampleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.durationInMonths").value(1));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(rentalService).deleteRental(sampleId);

        mockMvc.perform(delete("/api/rentals/" + sampleId))
                .andExpect(status().isNoContent());
    }

    private Rental createRental(String suffix) {
        Rental r = new Rental();
        r.setId(sampleId);
        r.setHouseId("house" + suffix);
        r.setFullName("Name" + suffix);
        r.setPhoneNumber("08123");
        r.setCheckInDate(LocalDate.now());
        r.setDurationInMonths(1);
        r.setApproved(false);
        return r;
    }
}
