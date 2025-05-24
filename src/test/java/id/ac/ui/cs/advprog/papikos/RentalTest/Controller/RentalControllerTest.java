package id.ac.ui.cs.advprog.papikos.RentalTest.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.house.Rental.controller.RentalController;
import id.ac.ui.cs.advprog.papikos.house.Rental.dto.RentalDTO;
import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.RentalService;
import id.ac.ui.cs.advprog.papikos.wishlist.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
    @Mock private NotificationService notificationService;

    @InjectMocks private RentalController controller;

    private MockMvc mockMvc;
    private ObjectMapper mapper;

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

    private Rental setupRental(Long id, Long houseId, String name) {
        Rental rental = new Rental();
        rental.setId(id);
        House house = new House();
        house.setId(houseId);
        rental.setHouse(house);
        User tenant = new User();
        tenant.setId(1L);
        tenant.setRole("ROLE_TENANT");
        rental.setTenant(tenant);
        rental.setFullName(name);
        rental.setPhoneNumber("08123456789");
        rental.setCheckInDate(LocalDate.of(2025, 6, 1));
        rental.setDurationInMonths(3);
        rental.setApproved(true);
        rental.setTotalPrice(1500000);
        rental.setPaid(true);
        return rental;
    }

    // === SYNC ===
    @Test void testCreateRentalSync_HouseNotFound() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setHouseId(123L);
        dto.setTenantId(1L);

        when(houseRepository.findById(123L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test void testCreateRentalSync_TenantNotFound() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setHouseId(1L);
        dto.setTenantId(321L);

        House house = new House();
        house.setId(1L);

        when(houseRepository.findById(1L)).thenReturn(Optional.of(house));
        when(userRepository.findById(321L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test void testCreateRental_Success() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setHouseId(1L);
        dto.setTenantId(1L);
        dto.setFullName("John Doe");
        dto.setPhoneNumber("08123456789");
        dto.setCheckInDate(LocalDate.of(2025, 6, 1));
        dto.setDurationInMonths(3);
        dto.setApproved(true);
        dto.setTotalPrice(1500000);
        dto.setPaid(true);

        Rental rental = setupRental(1L, 1L, "John Doe");

        when(houseRepository.findById(1L)).thenReturn(Optional.of(rental.getHouse()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(rental.getTenant()));
        when(rentalService.createRental(any())).thenReturn(rental);

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test void testCreateRental_UserNotTenant() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setHouseId(1L);
        dto.setTenantId(1L);

        House house = new House();
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

    @Test void testFindByIdSync_Found() throws Exception {
        Rental rental = setupRental(10L, 2L, "Sync Find");

        when(rentalService.getRentalById(10L)).thenReturn(Optional.of(rental));

        mockMvc.perform(get("/api/rentals/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test void testFindByIdSync_NotFound() throws Exception {
        when(rentalService.getRentalById(404L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/rentals/404"))
                .andExpect(status().isNotFound());
    }

    @Test void testDeleteSync_NotFoundPath() throws Exception {
        when(rentalService.getRentalById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/rentals/999"))
                .andExpect(status().isNotFound());
    }

    @Test void testDeleteSyncRental() throws Exception {
        Rental rental = setupRental(12L, 3L, "Sync Delete");

        when(rentalService.getRentalById(12L)).thenReturn(Optional.of(rental));
        doNothing().when(rentalService).deleteRental(12L);

        mockMvc.perform(delete("/api/rentals/12"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Rental deleted and availability updated")));
    }

    @Test void testUpdateSyncRental() throws Exception {
        Rental rental = setupRental(11L, 2L, "Sync Update");

        when(rentalService.updateRental(eq(11L), any())).thenReturn(rental);

        mockMvc.perform(put("/api/rentals/11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(rental)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11));
    }

    // === ASYNC ===
    @Test void testCreateAsync_HouseNotFound_ShouldThrow() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setHouseId(111L);
        dto.setTenantId(1L);

        when(houseRepository.findById(111L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/rentals/async")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test void testCreateAsync_TenantNotFound_ShouldThrow() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setHouseId(1L);
        dto.setTenantId(222L);

        House house = new House();
        house.setId(1L);

        when(houseRepository.findById(1L)).thenReturn(Optional.of(house));
        when(userRepository.findById(222L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/rentals/async")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test void testCreateRentalAsync_Success() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setHouseId(1L);
        dto.setTenantId(1L);
        dto.setFullName("Async Doe");
        dto.setPhoneNumber("08123456789");
        dto.setCheckInDate(LocalDate.of(2025, 6, 1));
        dto.setDurationInMonths(3);
        dto.setApproved(true);
        dto.setTotalPrice(1500000);
        dto.setPaid(true);

        Rental rental = setupRental(2L, 1L, "Async Doe");

        when(houseRepository.findById(1L)).thenReturn(Optional.of(rental.getHouse()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(rental.getTenant()));
        when(rentalService.createRentalAsync(any())).thenReturn(CompletableFuture.completedFuture(rental));

        var result = mockMvc.perform(post("/api/rentals/async")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test void testCreateRentalAsync_UserNotTenant() throws Exception {
        RentalDTO dto = new RentalDTO();
        dto.setHouseId(1L);
        dto.setTenantId(1L);

        House house = new House();
        User user = new User();
        user.setRole("ROLE_ADMIN");

        when(houseRepository.findById(1L)).thenReturn(Optional.of(house));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/rentals/async")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("User is not a tenant")));
    }

    @Test void testFindByIdAsync_NotFound() throws Exception {
        when(rentalService.getRentalByIdAsync(404L)).thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        var result = mockMvc.perform(get("/api/rentals/async/404"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isNotFound());
    }

    @Test void testDeleteAsync_NotFoundPath() throws Exception {
        when(rentalService.getRentalByIdAsync(888L)).thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        var result = mockMvc.perform(delete("/api/rentals/async/888"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isNotFound());
    }

    @Test void testDeleteRentalAsync_VerifySideEffects() throws Exception {
        Rental rental = setupRental(66L, 321L, "Delete Test");
        House house = rental.getHouse();
        house.setNumberOfRooms(2);

        when(rentalService.getRentalByIdAsync(66L)).thenReturn(CompletableFuture.completedFuture(Optional.of(rental)));
        when(rentalService.deleteRentalAsync(66L)).thenReturn(CompletableFuture.completedFuture(null));

        var result = mockMvc.perform(delete("/api/rentals/async/66"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Rental deleted and availability updated")));

        verify(houseRepository).save(house);
        verify(notificationService).notifyAvailability(house.getId());
    }

    @Test void testFindAllRentalsAsync() throws Exception {
        Rental r1 = setupRental(1L, 10L, "Kos A");
        Rental r2 = setupRental(2L, 20L, "Kos B");

        when(rentalService.getAllRentalsAsync())
                .thenReturn(CompletableFuture.completedFuture(List.of(r1, r2)));

        var result = mockMvc.perform(get("/api/rentals"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test void testUpdateRentalAsync_Found() throws Exception {
        Rental rental = setupRental(99L, 123L, "Update Test");

        when(rentalService.updateRentalAsync(eq(99L), any()))
                .thenReturn(CompletableFuture.completedFuture(rental));

        var result = mockMvc.perform(put("/api/rentals/async/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(rental)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99));
    }
}
