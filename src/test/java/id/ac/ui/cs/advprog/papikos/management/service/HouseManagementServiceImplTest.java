package id.ac.ui.cs.advprog.papikos.management.service;

import id.ac.ui.cs.advprog.papikos.management.model.House;
import id.ac.ui.cs.advprog.papikos.management.repository.HouseRepository;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddHouse() {
        House house = new House("Kos A", "Jakarta", "Desc", 4, 1200000);
        houseService.addHouse(house);
        verify(houseRepository, times(1)).save(house);
    }

    @Test
    void testFindAll() {
        when(houseRepository.findAll()).thenReturn(Arrays.asList(new House(), new House()));
        List<House> result = houseService.findAll();
        assertEquals(2, result.size());
        verify(houseRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        House house = new House();
        when(houseRepository.findById(1L)).thenReturn(Optional.of(house));
        Optional<House> result = houseService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(house, result.get());
    }

    @Test
    void testUpdateHouse() {
        House updated = new House(1L, "New Kos", "New Addr", "Updated", 5, 1500000);
        when(houseRepository.findById(1L)).thenReturn(Optional.of(updated));

        houseService.updateHouse(1L, updated);

        verify(houseRepository).save(updated);
    }

    @Test
    void testDeleteHouse() {
        houseService.deleteHouse(1L);
        verify(houseRepository).deleteById(1L);
    }
}
