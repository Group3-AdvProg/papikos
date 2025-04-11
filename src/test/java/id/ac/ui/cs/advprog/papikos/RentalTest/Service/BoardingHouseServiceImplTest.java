package id.ac.ui.cs.advprog.papikos.RentalTest.Service;

import id.ac.ui.cs.advprog.papikos.Rental.model.BoardingHouse;
import id.ac.ui.cs.advprog.papikos.Rental.repository.BoardingHouseRepository;
import id.ac.ui.cs.advprog.papikos.Rental.service.BoardingHouseServiceImpl;
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
        assertTrue(service.findById(1L).isPresent());
    }

    @Test
    void testUpdate() {
        BoardingHouse bh = new BoardingHouse();
        bh.setId(1L);
        when(repository.save(bh)).thenReturn(bh);
        assertEquals(bh, service.update(1L, bh));
    }

    @Test
    void testDelete() {
        service.delete(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}
