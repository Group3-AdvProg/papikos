package id.ac.ui.cs.advprog.papikos.RentalTest.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.house.Rental.controller.RentalController;
import id.ac.ui.cs.advprog.papikos.house.Rental.dto.RentalDTO;
import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.RentalService;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import id.ac.ui.cs.advprog.papikos.wishlist.service.WishlistService;
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
    @Mock private HouseRepository houseRepository;
    @Mock private UserRepository userRepository;
    @Mock private WishlistService wishlistService;

    @InjectMocks private RentalController controller;

    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private final Long sampleId = 1L;

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
        Rental r1 = createRental("A", 98L);
        Rental r2 = createRental("B", 99L);
        when(rentalService.getAllRentals()).thenReturn(Arrays.asList(r1, r2));

        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].house.id").value(99));
    }

    @Test
    void testGetById_Found() throws Exception {
        Rental r = createRental("X", 88L);
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
        Rental in = createRental("New", 77L);
        in.setId(null); // simulate new rental
        Rental out = createRental("New", 77L);

        when(houseRepository.findById(77L)).thenReturn(Optional.of(in.getHouse()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(in.getTenant()));
        when(rentalService.createRental(any(Rental.class))).thenReturn(out);

        RentalDTO dto = toDTO(in);

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleId))
                .andExpect(jsonPath("$.durationInMonths").value(1))
                .andExpect(jsonPath("$.house.id").value(77));
    }

    @Test
    void testCreateRental_HouseNotFound() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setHouseId(123L); // invalid house ID
        dto.setTenantId(1L);

        when(houseRepository.findById(123L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateRental_TenantNotFound() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setHouseId(10L);
        dto.setTenantId(999L); // invalid tenant ID

        House house = new House();
        house.setId(10L);
        when(houseRepository.findById(10L)).thenReturn(Optional.of(house));
        when(tenantRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateRental_Success() throws Exception {
        Rental update = createRental("U", 66L);
        when(rentalService.updateRental(eq(sampleId), any(Rental.class))).thenReturn(update);

        mockMvc.perform(put("/api/rentals/" + sampleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.house.id").value(66));
    }

    @Test
    void testUpdateRental_NotFound() throws Exception {
        Rental update = createRental("U", 66L);
        when(rentalService.updateRental(eq(sampleId), any(Rental.class)))
                .thenThrow(new RuntimeException("not found"));

        mockMvc.perform(put("/api/rentals/" + sampleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteRental() throws Exception {
        Rental r = createRental("D", 55L);
        when(rentalService.getRentalById(sampleId)).thenReturn(Optional.of(r));
        doNothing().when(rentalService).deleteRental(sampleId);
        when(houseRepository.save(any(House.class))).thenReturn(r.getHouse());
        doNothing().when(wishlistService).notifyAvailability(r.getHouse().getId());

        mockMvc.perform(delete("/api/rentals/" + sampleId))
                .andExpect(status().isOk())
                .andExpect(content().string("\"Rental deleted and availability updated\""));
        ;
    }

    private Rental createRental(String suffix, Long houseId) {
        Rental r = new Rental();
        r.setId(sampleId);

        House house = new House();
        house.setId(houseId);
        house.setName("Kos " + suffix);
        r.setHouse(house);

        r.setFullName("Name" + suffix);
        r.setPhoneNumber("08123");
        r.setCheckInDate(LocalDate.of(2025, 6, 1));
        r.setDurationInMonths(1);
        r.setApproved(false);
        r.setTotalPrice(1000000);
        r.setPaid(false);

        User tenant = new User();
        tenant.setId(1L);
        tenant.setFullName("Tenant " + suffix);
        tenant.setPhoneNumber("08123456789");
        tenant.setRole("ROLE_TENANT");
        r.setTenant(tenant);

        return r;
    }

    private RentalDTO toDTO(Rental rental) {
        RentalDTO dto = new RentalDTO();
        dto.setHouseId(rental.getHouse().getId());
        dto.setTenantId(rental.getTenant().getId());
        dto.setFullName(rental.getFullName());
        dto.setPhoneNumber(rental.getPhoneNumber());
        dto.setCheckInDate(rental.getCheckInDate());
        dto.setDurationInMonths(rental.getDurationInMonths());
        dto.setApproved(rental.isApproved());
        dto.setTotalPrice(rental.getTotalPrice());
        dto.setPaid(rental.isPaid());
        return dto;
    }
}
