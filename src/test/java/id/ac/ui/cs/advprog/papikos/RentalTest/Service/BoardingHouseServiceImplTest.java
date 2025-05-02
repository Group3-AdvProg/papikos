package id.ac.ui.cs.advprog.papikos.RentalTest.Service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.BoardingHouse;
import id.ac.ui.cs.advprog.papikos.house.Rental.repository.BoardingHouseRepository;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.BoardingHouseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BoardingHouseServiceImplTest {

    private BoardingHouseRepository repository;
    private BoardingHouseServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(BoardingHouseRepository.class);
        service = new BoardingHouseServiceImpl(repository);
    }

    @Test
    void testCreate() {
        BoardingHouse bh = new BoardingHouse();
        when(repository.save(bh)).thenReturn(bh);
        BoardingHouse result = service.create(bh);
        assertEquals(bh, result);
    }

    @Test
    void testFindAll() {
        List<BoardingHouse> list = List.of(new BoardingHouse(), new BoardingHouse());
        when(repository.findAll()).thenReturn(list);
        assertEquals(2, service.findAll().size());
    }

    @Test
    void testFindById() {
        BoardingHouse bh = new BoardingHouse();
        bh.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(bh));
        Optional<BoardingHouse> result = service.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testUpdate() {
        Long id = 1L;
        BoardingHouse existing = new BoardingHouse();
        existing.setId(id);
        existing.setName("Old Name");

        BoardingHouse updated = new BoardingHouse();
        updated.setName("New Name");
        updated.setAddress("New Address");
        updated.setDescription("New Desc");
        updated.setRoomCount(4);
        updated.setMonthlyPrice(1500000);

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(BoardingHouse.class))).thenAnswer(inv -> inv.getArgument(0));

        BoardingHouse result = service.update(id, updated);

        assertEquals("New Name", result.getName());
        assertEquals("New Address", result.getAddress());
        assertEquals("New Desc", result.getDescription());
        assertEquals(4, result.getRoomCount());
        assertEquals(1500000, result.getMonthlyPrice());
    }

    @Test
    void testUpdateNotFound() {
        Long id = 99L;
        BoardingHouse updated = new BoardingHouse();

        when(repository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.update(id, updated);
        });

        assertEquals("BoardingHouse not found", exception.getMessage());
        verify(repository, times(1)).findById(id);
    }

    @Test
    void testDelete() {
        service.delete(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}
