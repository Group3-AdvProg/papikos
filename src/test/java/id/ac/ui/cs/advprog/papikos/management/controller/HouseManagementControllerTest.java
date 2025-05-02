package id.ac.ui.cs.advprog.papikos.management.controller;

import id.ac.ui.cs.advprog.papikos.house.management.controller.HouseManagementController;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.management.service.HouseManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HouseManagementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HouseManagementService houseManagementService;

    @InjectMocks
    private HouseManagementController houseManagementController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(houseManagementController).build();
    }

    @Test
    void testCreateHousePage() throws Exception {
        mockMvc.perform(get("/management/create"))
                .andExpect(view().name("CreateHouse"))
                .andExpect(model().attributeExists("house"));
    }

    @Test
    void testCreateHousePost() throws Exception {
        House house = new House();
        house.setName("Kos A");

        mockMvc.perform(post("/management/create")
                        .flashAttr("house", house))
                .andExpect(redirectedUrl("/management/list"));

        verify(houseManagementService, times(1)).addHouse(any(House.class));
    }

    @Test
    void testHouseListPage() throws Exception {
        when(houseManagementService.findAll()).thenReturn(Arrays.asList(new House(), new House()));

        mockMvc.perform(get("/management/list"))
                .andExpect(view().name("HouseList"))
                .andExpect(model().attributeExists("houses"));

        verify(houseManagementService, times(1)).findAll();
    }

    @Test
    void testEditHousePage() throws Exception {
        House house = new House();
        house.setId(1L);
        when(houseManagementService.findById(1L)).thenReturn(Optional.of(house));

        mockMvc.perform(get("/management/edit/1"))
                .andExpect(view().name("EditHouse"))
                .andExpect(model().attributeExists("house"));

        verify(houseManagementService, times(1)).findById(1L);
    }

    @Test
    void testEditHousePost() throws Exception {
        House house = new House();
        house.setId(1L);
        house.setName("Updated Kos");

        mockMvc.perform(post("/management/edit")
                        .flashAttr("house", house))
                .andExpect(redirectedUrl("/management/list"));

        verify(houseManagementService, times(1)).updateHouse(eq(1L), any(House.class));
    }

    @Test
    void testDeleteHouse() throws Exception {
        mockMvc.perform(get("/management/delete/1"))
                .andExpect(redirectedUrl("/management/list"));

        verify(houseManagementService, times(1)).deleteHouse(1L);
    }
}
