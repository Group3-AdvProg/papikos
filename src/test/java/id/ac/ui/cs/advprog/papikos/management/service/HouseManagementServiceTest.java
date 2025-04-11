package id.ac.ui.cs.advprog.papikos.management.service;

import id.ac.ui.cs.advprog.papikos.management.model.House;
import id.ac.ui.cs.advprog.papikos.management.repository.HouseRepository;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class HouseManagementServiceTest {

    @Test
    void testAddHouse() {
        HouseRepository repo = mock(HouseRepository.class);
        HouseManagementService service = new HouseManagementService(repo);
        House house = new House("Kos A", "Jakarta", "Desc", 4, 1200000);
        service.addHouse(house);
        verify(repo).save(house);
    }

    @Test
    void testFindAll() {
        HouseRepository repo = mock(HouseRepository.class);
        HouseManagementService service = new HouseManagementService(repo);
        when(repo.findAll()).thenReturn(Arrays.asList(new House(), new House()));
        assertEquals(2, service.findAll().size());
    }

    @Test
    void testFindById() {
        HouseRepository repo = mock(HouseRepository.class);
        HouseManagementService service = new HouseManagementService(repo);
        House house = new House();
        when(repo.findById(1L)).thenReturn(Optional.of(house));
        assertEquals(Optional.of(house), service.findById(1L));
    }

    @Test
    void testUpdateHouse() {
        HouseRepository repo = mock(HouseRepository.class);
        HouseManagementService service = new HouseManagementService(repo);
        House updated = new House(1L, "Kos Update", "NewAddr", "Updated", 5, 1500000);
        when(repo.findById(1L)).thenReturn(Optional.of(updated));
        service.updateHouse(1L, updated);
        verify(repo).save(updated);
    }

    @Test
    void testDeleteHouse() {
        HouseRepository repo = mock(HouseRepository.class);
        HouseManagementService service = new HouseManagementService(repo);
        service.deleteHouse(1L);
        verify(repo).deleteById(1L);
    }
}
