package id.ac.ui.cs.advprog.papikos.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.house.rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.house.rental.service.RentalService;
import id.ac.ui.cs.advprog.papikos.house.management.service.HouseManagementService;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.wishlist.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class HouseManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HouseManagementService houseManagementService;

    @MockBean
    private RentalService rentalService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private House house;
    private User owner;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("owner@example.com");

        house = new House("Kos A", "Jl. UI", "Dekat kampus", 10, 1500000.0,
                "https://example.com/img.jpg", owner);
        house.setId(1L);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
    }

    @Test
    void testGetAllHouses() throws Exception {
        when(houseManagementService.findAllByOwner(owner)).thenReturn(List.of(house));

        mockMvc.perform(get("/api/management/houses")
                        .principal(() -> "owner@example.com")) // inject principal
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Kos A"));
    }

    @Test
    void testCreateHouse_ApprovedLandlord() throws Exception {
        owner.setApproved(true);
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(houseManagementService.addHouse(any(House.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(asyncDispatch(
                        mockMvc.perform(post("/api/management/houses")
                                        .principal(() -> "owner@example.com")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(house)))
                                .andReturn()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kos A"));
    }

    @Test
    void testCreateHouse_UnapprovedLandlord() throws Exception {
        owner.setApproved(false);
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));

        mockMvc.perform(asyncDispatch(
                        mockMvc.perform(post("/api/management/houses")
                                        .principal(() -> "owner@example.com")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(house)))
                                .andReturn()))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Account not approved by admin yet"));
    }

    @Test
    void testGetHouseById_ValidOwner() throws Exception {
        when(houseManagementService.findByIdAndOwner(1L, owner)).thenReturn(Optional.of(house));

        mockMvc.perform(get("/api/management/houses/1")
                        .principal(() -> "owner@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kos A"));
    }

    @Test
    void testUpdateHouse_ValidOwner() throws Exception {
        when(houseManagementService.findById(1L)).thenReturn(Optional.of(house));
        when(houseManagementService.updateHouse(eq(1L), any(House.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(asyncDispatch(
                        mockMvc.perform(put("/api/management/houses/1")
                                        .principal(() -> "owner@example.com")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(house)))
                                .andReturn()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kos A"));
    }

    @Test
    void testUpdateHouse_NotFound() throws Exception {
        when(houseManagementService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(asyncDispatch(
                        mockMvc.perform(put("/api/management/houses/1")
                                        .principal(() -> "owner@example.com")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(house)))
                                .andReturn()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateHouse_Forbidden() throws Exception {
        User otherUser = new User();
        otherUser.setId(2L);
        house.setOwner(otherUser);
        when(houseManagementService.findById(1L)).thenReturn(Optional.of(house));

        mockMvc.perform(asyncDispatch(
                        mockMvc.perform(put("/api/management/houses/1")
                                        .principal(() -> "owner@example.com")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(house)))
                                .andReturn()))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Forbidden: You do not own this house."));
    }

    @Test
    void testUpdateHouse_TriggersWishlistNotification() throws Exception {
        house.setNumberOfRooms(2);
        House updated = new House("Kos A", "Jl. UI", "Updated", 5, 1500000.0,
                "https://example.com/img.jpg", owner);
        updated.setId(1L);

        when(houseManagementService.findById(1L)).thenReturn(Optional.of(house));
        when(houseManagementService.updateHouse(eq(1L), any(House.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(asyncDispatch(
                        mockMvc.perform(put("/api/management/houses/1")
                                        .principal(() -> "owner@example.com")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(updated)))
                                .andReturn()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kos A"));

        verify(notificationService, times(1)).notifyAvailability(1L);
    }

    @Test
    void testDeleteHouse_ValidOwner() throws Exception {
        when(houseManagementService.findById(1L)).thenReturn(Optional.of(house));
        when(houseManagementService.deleteHouse(1L))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(asyncDispatch(
                        mockMvc.perform(delete("/api/management/houses/1")
                                        .principal(() -> "owner@example.com"))
                                .andReturn()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteHouse_NotFound() throws Exception {
        when(houseManagementService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(asyncDispatch(
                        mockMvc.perform(delete("/api/management/houses/1")
                                        .principal(() -> "owner@example.com"))
                                .andReturn()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteHouse_Forbidden() throws Exception {
        User otherUser = new User();
        otherUser.setId(2L);
        house.setOwner(otherUser);
        when(houseManagementService.findById(1L)).thenReturn(Optional.of(house));

        mockMvc.perform(asyncDispatch(
                        mockMvc.perform(delete("/api/management/houses/1")
                                        .principal(() -> "owner@example.com"))
                                .andReturn()))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Forbidden: You do not own this house."));
    }

    @Test
    void testSearchHouses_KeywordOnly() throws Exception {
        when(houseManagementService.searchHouses(owner, "UI", null, null))
                .thenReturn(List.of(house));

        mockMvc.perform(get("/api/management/houses/search")
                        .principal(() -> "owner@example.com")
                        .param("keyword", "UI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Kos A"))
                .andExpect(jsonPath("$[0].address").value("Jl. UI"));
    }

    @Test
    void testSearchHouses_AllFilters() throws Exception {
        when(houseManagementService.searchHouses(owner, "Kos", 1000000.0, 2000000.0))
                .thenReturn(List.of(house));

        mockMvc.perform(get("/api/management/houses/search")
                        .principal(() -> "owner@example.com")
                        .param("keyword", "Kos")
                        .param("minRent", "1000000")
                        .param("maxRent", "2000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].monthlyRent").value(1500000.0));
    }

    @Test
    void testApproveRental_ValidOwner_DecrementsRoomAndApproves() throws Exception {
        Rental rental = new Rental();
        rental.setId(10L);
        rental.setApproved(false);

        User tenant = new User();
        tenant.setId(99L);
        rental.setTenant(tenant);

        House testHouse = new House("Kos Z", "Jl. Kucing", "Deskripsi", 3, 2000000.0,
                "https://example.com/kosz.jpg", owner);
        testHouse.setId(1L);
        rental.setHouse(testHouse);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(rentalService.getRentalById(10L)).thenReturn(Optional.of(rental));
        when(houseManagementService.updateHouse(eq(1L), any(House.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(post("/api/management/rentals/10/approve")
                        .principal(() -> "owner@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Rental approved."));

        verify(rentalService, times(1)).updateRental(eq(10L), any(Rental.class));
        verify(houseManagementService, times(1)).updateHouse(eq(1L), any(House.class));
    }

    @Test
    void testApproveRental_Forbidden() throws Exception {
        Rental rental = new Rental();
        rental.setId(10L);

        House otherHouse = new House("Kos X", "Jl. Gajah", "Not yours", 2, 1900000.0,
                "https://example.com/kosx.jpg", new User());
        otherHouse.setId(5L);
        otherHouse.getOwner().setId(2L);
        rental.setHouse(otherHouse);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(rentalService.getRentalById(10L)).thenReturn(Optional.of(rental));

        mockMvc.perform(post("/api/management/rentals/10/approve")
                        .principal(() -> "owner@example.com"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("You do not own this house."));
    }

    @Test
    void testApproveRental_InvalidOwner() throws Exception {
        Rental rental = new Rental();
        rental.setId(10L);
        rental.setApproved(false);

        User differentOwner = new User();
        differentOwner.setId(999L);

        House house = new House();
        house.setId(1L);
        house.setOwner(differentOwner);
        rental.setHouse(house);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(rentalService.getRentalById(10L)).thenReturn(Optional.of(rental));

        mockMvc.perform(post("/api/management/rentals/10/approve")
                        .principal(() -> "owner@example.com"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("You do not own this house."));
    }

    @Test
    void testApproveRental_AlreadyApproved() throws Exception {
        Rental rental = new Rental();
        rental.setId(11L);
        rental.setApproved(true);

        House testHouse = new House("Kos Already", "Jl. Approved", "desc", 3, 2000000.0,
                "https://example.com/kosz.jpg", owner);
        testHouse.setId(1L);
        rental.setHouse(testHouse);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(rentalService.getRentalById(11L)).thenReturn(Optional.of(rental));

        mockMvc.perform(post("/api/management/rentals/11/approve")
                        .principal(() -> "owner@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Rental approved."));

        verify(rentalService, never()).updateRental(anyLong(), any());
        verify(houseManagementService, never()).updateHouse(anyLong(), any());
    }

    @Test
    void testApproveRental_NoRoomsLeft() throws Exception {
        Rental rental = new Rental();
        rental.setId(12L);
        rental.setApproved(false);

        User tenant = new User();
        tenant.setId(99L);
        rental.setTenant(tenant);

        House fullHouse = new House("Kos Full", "Jl. Penuh", "desc", 0, 2000000.0,
                "https://example.com/kosz.jpg", owner);
        fullHouse.setId(1L);
        rental.setHouse(fullHouse);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(rentalService.getRentalById(12L)).thenReturn(Optional.of(rental));

        mockMvc.perform(post("/api/management/rentals/12/approve")
                        .principal(() -> "owner@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Rental approved."));

        verify(rentalService, times(1)).updateRental(eq(12L), any());
        verify(houseManagementService, never()).updateHouse(anyLong(), any());
    }

    @Test
    void testRejectRental_ValidOwner_Success() throws Exception {
        Rental rental = new Rental();
        rental.setId(20L);
        rental.setApproved(false);

        rental.setTenant(new User() {{ setId(3L); }});

        House house = new House("Kos Y", "Jl. Rejeki", "desc", 2, 1800000.0,
                "https://example.com/kosy.jpg", owner);
        house.setId(2L);
        rental.setHouse(house);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(rentalService.getRentalById(20L)).thenReturn(Optional.of(rental));

        mockMvc.perform(post("/api/management/rentals/20/reject")
                        .principal(() -> "owner@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Rental rejected."));

        verify(rentalService, times(1)).deleteRental(20L);
        verify(notificationService, times(1)).notifyTenantRentalRejected(1L, 3L, 2L);
    }

    @Test
    void testRejectRental_Forbidden() throws Exception {
        Rental rental = new Rental();
        rental.setId(30L);
        rental.setApproved(false);

        User otherLandlord = new User();
        otherLandlord.setId(99L);

        House house = new House("Kos X", "Jl. Gajah", "desc", 5, 1900000.0,
                "https://example.com/kosx.jpg", otherLandlord);
        house.setId(5L);
        rental.setHouse(house);

        rental.setTenant(new User() {{ setId(100L); }});

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(rentalService.getRentalById(30L)).thenReturn(Optional.of(rental));

        mockMvc.perform(post("/api/management/rentals/30/reject")
                        .principal(() -> "owner@example.com"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("You do not own this house."));

        verify(rentalService, never()).deleteRental(anyLong());
        verify(notificationService, never()).notifyTenantRentalRejected(any(), any(), any());
    }
}
