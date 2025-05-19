package id.ac.ui.cs.advprog.papikos.chat.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomRepository;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatMessageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        // disambiguate findAll by matching Sort.class
        when(roomRepo.findAll(any(Sort.class))).thenReturn(Arrays.asList(a, b));

        List<ChatRoom> list = service.listRooms();
        assertEquals(2, list.size());
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
        ChatMessage msg = new ChatMessage();
        assertThrows(EntityNotFoundException.class, () -> service.saveMessage(1L, msg));
    }

    @Test
    void listMessages_delegatesToRepo() {
        // use Collections.emptyList() instead of Arrays.asList() with no args
        when(msgRepo.findByRoomIdOrderByTimestampAsc(1L))
                .thenReturn(Collections.emptyList());

        List<ChatMessage> msgs = service.listMessages(1L);
        assertNotNull(msgs);
        verify(msgRepo).findByRoomIdOrderByTimestampAsc(1L);
    }
}
