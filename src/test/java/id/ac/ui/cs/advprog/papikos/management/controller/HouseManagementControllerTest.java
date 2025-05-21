package id.ac.ui.cs.advprog.papikos.house.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.house.management.service.HouseManagementService;
import id.ac.ui.cs.advprog.papikos.house.model.House;
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
        doNothing().when(houseManagementService).addHouse(any(House.class));

        mockMvc.perform(post("/api/management/houses")
                        .principal(() -> "owner@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(house)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kos A"));
    }

    @Test
    void testCreateHouse_UnapprovedLandlord() throws Exception {
        owner.setApproved(false);
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));

        mockMvc.perform(post("/api/management/houses")
                        .principal(() -> "owner@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(house)))
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

        mockMvc.perform(put("/api/management/houses/1")
                        .principal(() -> "owner@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(house)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kos A"));

        verify(houseManagementService, times(1)).updateHouse(eq(1L), any(House.class));
    }

    @Test
    void testDeleteHouse_ValidOwner() throws Exception {
        when(houseManagementService.findById(1L)).thenReturn(Optional.of(house));

        mockMvc.perform(delete("/api/management/houses/1")
                        .principal(() -> "owner@example.com"))
                .andExpect(status().isNoContent());

        verify(houseManagementService, times(1)).deleteHouse(1L);
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

}
