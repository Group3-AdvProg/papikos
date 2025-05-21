// src/test/java/id/ac/ui/cs/advprog/papikos/chat/service/ChatRoomServiceTest.java
package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatMessageRepository;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatRoomServiceTest {
    private ChatRoomRepository roomRepo;
    private ChatMessageRepository msgRepo;
    private ChatRoomService service;

    @BeforeEach
    void setUp() {
        roomRepo = mock(ChatRoomRepository.class);
        msgRepo  = mock(ChatMessageRepository.class);
        service  = new ChatRoomService(roomRepo, msgRepo);
    }

    @Test
    void createRoom_assignsNameAndTimestamp() {
        when(roomRepo.save(any(ChatRoom.class))).thenAnswer(invocation -> {
            ChatRoom r = invocation.getArgument(0);
            r.setId(1L);
            r.setCreatedAt(Instant.now());
            return r;
        });

        ChatRoom result = service.createRoom("MyRoom");
        assertEquals("MyRoom", result.getName());
        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        verify(roomRepo).save(any(ChatRoom.class));
    }

    @Test
    void listRooms_delegatesToRepo() {
        ChatRoom a = new ChatRoom(1L, "A", Instant.now());
        ChatRoom b = new ChatRoom(2L, "B", Instant.now());
        when(roomRepo.findAll(any(Sort.class))).thenReturn(Arrays.asList(a, b));

        assertEquals(2, service.listRooms().size());
        verify(roomRepo).findAll(any(Sort.class));
    }

    @Test
    void saveMessage_existingRoom_savesWithRoom() {
        ChatRoom room = new ChatRoom(1L, "R", Instant.now());
        ChatMessage msg = ChatMessage.builder()
                .type(ChatMessage.MessageType.CHAT)
                .content("hi")
                .sender("user")
                .timestamp(Instant.now())
                .build();

        when(roomRepo.findById(1L)).thenReturn(Optional.of(room));
        when(msgRepo.save(any(ChatMessage.class))).thenAnswer(i -> i.getArgument(0));

        ChatMessage saved = service.saveMessage(1L, msg);
        assertEquals(room, saved.getRoom());
        verify(msgRepo).save(msg);
    }

    @Test
    void saveMessage_unknownRoom_throwsException() {
        when(roomRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.saveMessage(1L, new ChatMessage()));
    }

    @Test
    void listMessages_delegatesToRepo() {
        // stub room lookup to prevent EntityNotFoundException
        ChatRoom room = new ChatRoom(1L, "R", Instant.now());
        when(roomRepo.findById(1L)).thenReturn(Optional.of(room));

        // stub message lookup
        when(msgRepo.findByRoomIdOrderByTimestampAsc(1L))
                .thenReturn(Collections.emptyList());

        assertNotNull(service.listMessages(1L));
        verify(msgRepo).findByRoomIdOrderByTimestampAsc(1L);
    }

    @Test
    void updateMessage_existingMessage_updatesContent() {
        ChatRoom room = new ChatRoom(1L, "R", Instant.now());
        ChatMessage existing = ChatMessage.builder()
                .id(5L)
                .content("old")
                .room(room)
                .build();
        ChatMessage update = new ChatMessage();
        update.setContent("new");

        when(msgRepo.findById(5L)).thenReturn(Optional.of(existing));
        when(msgRepo.save(any(ChatMessage.class))).thenAnswer(i -> i.getArgument(0));

        ChatMessage result = service.updateMessage(1L, 5L, update);
        assertEquals("new", result.getContent());
        verify(msgRepo).save(existing);
    }

    @Test
    void updateMessage_wrongRoom_throwsException() {
        ChatRoom otherRoom = new ChatRoom(2L, "R2", Instant.now());
        ChatMessage existing = ChatMessage.builder()
                .id(5L)
                .content("old")
                .room(otherRoom)
                .build();

        when(msgRepo.findById(5L)).thenReturn(Optional.of(existing));
        assertThrows(EntityNotFoundException.class,
                () -> service.updateMessage(1L, 5L, new ChatMessage()));
    }

    @Test
    void deleteMessage_existingMessage_deletes() {
        ChatRoom room = new ChatRoom(1L, "R", Instant.now());
        ChatMessage existing = ChatMessage.builder()
                .id(6L)
                .room(room)
                .build();

        when(msgRepo.findById(6L)).thenReturn(Optional.of(existing));
        doNothing().when(msgRepo).delete(existing);

        service.deleteMessage(1L, 6L);
        verify(msgRepo).delete(existing);
    }

    @Test
    void deleteMessage_wrongRoom_throwsException() {
        ChatRoom otherRoom = new ChatRoom(2L, "R2", Instant.now());
        ChatMessage existing = ChatMessage.builder()
                .id(6L)
                .room(otherRoom)
                .build();

        when(msgRepo.findById(6L)).thenReturn(Optional.of(existing));
        assertThrows(EntityNotFoundException.class, () -> service.deleteMessage(1L, 6L));
    }

    @Test
    void deleteMessage_notFound_throwsException() {
        when(msgRepo.findById(7L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.deleteMessage(1L, 7L));
    }
}
