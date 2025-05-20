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
    private HouseManagementServiceImpl houseService;

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
        houseService.addHouse(house);
        verify(houseRepository, times(1)).save(house);
    }

    @Test
    void testFindById() {
        when(houseRepository.findById(1L)).thenReturn(Optional.of(house));
        Optional<House> result = houseService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(house, result.get());
    }

    @Test
    void testFindAllByOwner() {
        when(houseRepository.findByOwner(owner)).thenReturn(List.of(house));
        List<House> result = houseService.findAllByOwner(owner);
        assertEquals(1, result.size());
        assertEquals(house.getId(), result.get(0).getId());
    }

    @Test
    void testFindByIdAndOwner() {
        when(houseRepository.findByIdAndOwner(1L, owner)).thenReturn(Optional.of(house));
        Optional<House> result = houseService.findByIdAndOwner(1L, owner);
        assertTrue(result.isPresent());
        assertEquals(house, result.get());
    }

    @Test
    void testUpdateHouse() {
        when(houseRepository.findById(1L)).thenReturn(Optional.of(house));
        house.setName("Updated Name");

        houseService.updateHouse(1L, house);
        verify(houseRepository).save(house);
    }

    @Test
    void testUpdateHouseNotFound() {
        when(houseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            houseService.updateHouse(1L, house);
        });

        verify(houseRepository, never()).save(any());
    }

    @Test
    void testDeleteHouse() {
        houseService.deleteHouse(1L);
        verify(houseRepository).deleteById(1L);
    }
}
