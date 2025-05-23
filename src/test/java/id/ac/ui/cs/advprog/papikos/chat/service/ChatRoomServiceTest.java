package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomRepository;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatMessageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {
    @Mock private ChatRoomRepository roomRepo;
    @Mock private ChatMessageRepository msgRepo;
    @Mock private UserRepository userRepo;
    @InjectMocks private ChatRoomService service;

    private User tenant, landlord, sender;
    private ChatRoom room;

    @BeforeEach
    void setUp() {
        tenant   = new User(); tenant.setId(10L);
        landlord = new User(); landlord.setId(20L);
        sender   = new User(); sender.setId(30L);

        room = ChatRoom.builder()
                .id(100L)
                .tenant(tenant)
                .landlord(landlord)
                .build();
    }

    @Test
    void createRoom_existingUsers_savesRoom() {
        when(userRepo.findById(tenant.getId())).thenReturn(Optional.of(tenant));
        when(userRepo.findById(landlord.getId())).thenReturn(Optional.of(landlord));
        when(roomRepo.save(any(ChatRoom.class))).thenAnswer(i -> i.getArgument(0));

        ChatRoom created = service.createRoom(tenant.getId(), landlord.getId());
        assertEquals(tenant,   created.getTenant());
        assertEquals(landlord, created.getLandlord());
        verify(roomRepo).save(any(ChatRoom.class));
    }

    @Test
    void createRoom_missingTenant_throws() {
        when(userRepo.findById(tenant.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.createRoom(tenant.getId(), landlord.getId()));
    }

    @Test
    void createRoom_missingLandlord_throws() {
        when(userRepo.findById(tenant.getId())).thenReturn(Optional.of(tenant));
        when(userRepo.findById(landlord.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.createRoom(tenant.getId(), landlord.getId()));
    }

    @Test
    void listRooms_delegatesToRepo() {
        when(roomRepo.findAll(any(Sort.class))).thenReturn(List.of(room));
        List<ChatRoom> list = service.listRooms();
        assertEquals(1, list.size());
        verify(roomRepo).findAll(any(Sort.class));
    }

    @Test
    void saveMessage_existingRoomAndSender_savesWithRoomAndSender() {
        ChatMessage msg = ChatMessage.builder()
                .type(ChatMessage.MessageType.CHAT)
                .content("hi")
                .build();
        when(roomRepo.findById(room.getId())).thenReturn(Optional.of(room));
        when(userRepo.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(msgRepo.save(any(ChatMessage.class))).thenAnswer(i -> i.getArgument(0));

        CompletableFuture<ChatMessage> future = service.saveMessage(room.getId(), sender.getId(), msg);
        ChatMessage saved = future.join();

        assertEquals(room,  saved.getRoom());
        assertEquals(sender, saved.getSender());
        verify(msgRepo).save(msg);
    }

    @Test
    void saveMessage_unknownRoom_throws() {
        when(roomRepo.findById(room.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.saveMessage(room.getId(), sender.getId(), new ChatMessage()));
    }

    @Test
    void saveMessage_unknownSender_throws() {
        when(roomRepo.findById(room.getId())).thenReturn(Optional.of(room));
        when(userRepo.findById(sender.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.saveMessage(room.getId(), sender.getId(), new ChatMessage()));
    }

    @Test
    void listMessages_unknownRoom_throws() {
        when(roomRepo.findById(room.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.listMessages(room.getId()));
    }

    @Test
    void listMessages_delegatesToRepo() {
        when(roomRepo.findById(room.getId())).thenReturn(Optional.of(room));
        when(msgRepo.findByRoomIdOrderByTimestampAsc(room.getId()))
                .thenReturn(Collections.emptyList());

        assertNotNull(service.listMessages(room.getId()));
        verify(msgRepo).findByRoomIdOrderByTimestampAsc(room.getId());
    }

    @Test
    void updateMessage_messageNotFound_throws() {
        when(msgRepo.findById(55L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.updateMessage(room.getId(), 55L, new ChatMessage()));
    }

    @Test
    void updateMessage_wrongRoom_throws() {
        ChatRoom otherRoom = ChatRoom.builder().id(999L).build();
        ChatMessage existing = ChatMessage.builder()
                .id(5L)
                .room(otherRoom)
                .build();
        when(msgRepo.findById(5L)).thenReturn(Optional.of(existing));

        assertThrows(EntityNotFoundException.class,
                () -> service.updateMessage(room.getId(), 5L, new ChatMessage()));
    }

    @Test
    void updateMessage_existingMessage_updatesContent() {
        ChatMessage existing = ChatMessage.builder()
                .id(5L)
                .room(room)
                .content("old")
                .build();
        ChatMessage update = new ChatMessage(); update.setContent("new");

        when(msgRepo.findById(5L)).thenReturn(Optional.of(existing));
        when(msgRepo.save(any(ChatMessage.class))).thenAnswer(i -> i.getArgument(0));

        ChatMessage result = service.updateMessage(room.getId(), 5L, update);
        assertEquals("new", result.getContent());
        verify(msgRepo).save(existing);
    }

    @Test
    void deleteMessage_messageNotFound_throws() {
        when(msgRepo.findById(88L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.deleteMessage(room.getId(), 88L));
    }

    @Test
    void deleteMessage_wrongRoom_throws() {
        ChatRoom otherRoom = ChatRoom.builder().id(777L).build();
        ChatMessage existing = ChatMessage.builder()
                .id(6L)
                .room(otherRoom)
                .build();
        when(msgRepo.findById(6L)).thenReturn(Optional.of(existing));

        assertThrows(EntityNotFoundException.class,
                () -> service.deleteMessage(room.getId(), 6L));
    }

    @Test
    void deleteMessage_existingMessage_deletes() {
        ChatMessage existing = ChatMessage.builder()
                .id(6L)
                .room(room)
                .build();
        when(msgRepo.findById(6L)).thenReturn(Optional.of(existing));
        doNothing().when(msgRepo).delete(existing);

        service.deleteMessage(room.getId(), 6L);
        verify(msgRepo).delete(existing);
    }
}
