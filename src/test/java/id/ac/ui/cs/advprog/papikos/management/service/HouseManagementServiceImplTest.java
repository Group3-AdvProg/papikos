package id.ac.ui.cs.advprog.papikos.management.service;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.house.management.service.HouseManagementServiceImpl;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HouseManagementServiceImplTest {

    @Mock
    private HouseRepository houseRepository;

    @InjectMocks
    private HouseManagementServiceImpl houseManagementService;

    private User owner;
    private House house;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");

        house = new House("Kos A", "Jakarta", "Desc", 4, 1200000,
                "https://dummyimage.com/kos.jpg", owner);
        house.setId(1L);
    }

    @Test
    void testAddHouse() {
        houseManagementService.addHouse(house).join();
        verify(houseRepository, times(1)).save(house);
    }

    @Test
    void testFindById() {
        when(houseRepository.findById(1L)).thenReturn(Optional.of(house));
        Optional<House> result = houseManagementService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(house, result.get());
    }

    @Test
    void testFindAllByOwner() {
        when(houseRepository.findByOwner(owner)).thenReturn(List.of(house));
        List<House> result = houseManagementService.findAllByOwner(owner);
        assertEquals(1, result.size());
        assertEquals(house.getId(), result.get(0).getId());
    }

    @Test
    void testFindByIdAndOwner() {
        when(houseRepository.findByIdAndOwner(1L, owner)).thenReturn(Optional.of(house));
        Optional<House> result = houseManagementService.findByIdAndOwner(1L, owner);
        assertTrue(result.isPresent());
        assertEquals(house, result.get());
    }

    @Test
    void testUpdateHouse() {
        when(houseRepository.findById(1L)).thenReturn(Optional.of(house));
        house.setName("Updated Name");

        houseManagementService.updateHouse(1L, house).join();
        verify(houseRepository).save(house);
    }

    @Test
    void testUpdateHouseNotFound() {
        when(houseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            houseManagementService.updateHouse(1L, house).join();
        });

        verify(houseRepository, never()).save(any());
    }

    @Test
    void testDeleteHouse() {
        houseManagementService.deleteHouse(1L).join();
        verify(houseRepository).deleteById(1L);
    }

    @Test
    void testSearchHouses_All() {
        House otherHouse = new House("Kos B", "Depok", "Other Desc", 3, 1000000,
                "https://dummyimage.com/other.jpg", owner);
        otherHouse.setId(2L);

        when(houseRepository.findAll()).thenReturn(List.of(house, otherHouse));
        List<House> results = houseManagementService.searchHouses(owner, "Jakarta", 1000000.0, 1300000.0);
        assertEquals(1, results.size());
        assertEquals(house, results.get(0));
    }

    @Test
    void testSearchHouses_KeywordOnly() {
        when(houseRepository.findAll()).thenReturn(List.of(house));
        List<House> results = houseManagementService.searchHouses(owner, "Jakarta", null, null);
        assertEquals(1, results.size());
        assertEquals(house, results.get(0));
    }

    @Test
    void testSearchHouses_KeywordDoesNotMatch() {
        when(houseRepository.findAll()).thenReturn(List.of(house));
        List<House> results = houseManagementService.searchHouses(owner, "Nonexistent", null, null);
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearchHouses_KeywordNull() {
        when(houseRepository.findAll()).thenReturn(List.of(house));
        List<House> results = houseManagementService.searchHouses(owner, null, 1000000.0, 1300000.0);
        assertEquals(1, results.size());
        assertEquals(house, results.get(0));
    }

    @Test
    void testSearchHouses_MinRentOnly() {
        when(houseRepository.findAll()).thenReturn(List.of(house));
        List<House> results = houseManagementService.searchHouses(owner, null, 1000000.0, null);
        assertEquals(1, results.size());
    }

    @Test
    void testSearchHouses_MinRentTooHigh() {
        when(houseRepository.findAll()).thenReturn(List.of(house));
        List<House> results = houseManagementService.searchHouses(owner, null, 2000000.0, null);
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearchHouses_MaxRentOnly() {
        when(houseRepository.findAll()).thenReturn(List.of(house));
        List<House> results = houseManagementService.searchHouses(owner, null, null, 1300000.0);
        assertEquals(1, results.size());
    }

    @Test
    void testSearchHouses_MaxRentTooLow() {
        when(houseRepository.findAll()).thenReturn(List.of(house));
        List<House> results = houseManagementService.searchHouses(owner, null, null, 1000000.0);
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearchHouses_NoFilters() {
        when(houseRepository.findAll()).thenReturn(List.of(house));
        List<House> results = houseManagementService.searchHouses(owner, null, null, null);
        assertEquals(1, results.size());
    }

}
