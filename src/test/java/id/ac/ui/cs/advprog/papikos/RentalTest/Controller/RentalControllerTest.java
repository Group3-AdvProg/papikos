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
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
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

        CompletableFuture<List<Rental>> rentalFuture = CompletableFuture.completedFuture(Arrays.asList(r1, r2));
        when(rentalService.getAllRentalsAsync()).thenReturn(rentalFuture);

        // First perform async request
        var mvcResult = mockMvc.perform(get("/api/rentals"))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Then dispatch the result (waits for async to complete)
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].house.id").value(98))
                .andExpect(jsonPath("$[1].house.id").value(99))
                .andExpect(jsonPath("$.length()").value(2));
    }


    @Test
    void testCreateRental_UserIsNotTenant() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setHouseId(1L);
        dto.setTenantId(1L);

        House house = new House();
        house.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setRole("ROLE_ADMIN");

        when(houseRepository.findById(1L)).thenReturn(Optional.of(house));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("User is not a tenant")));
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
