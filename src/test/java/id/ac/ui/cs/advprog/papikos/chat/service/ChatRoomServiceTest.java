package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatMessageRepository;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomRepository;
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
        tenant = new User(); tenant.setId(10L); tenant.setEmail("tenant@example.com");
        landlord = new User(); landlord.setId(20L); landlord.setEmail("landlord@example.com");
        sender = new User(); sender.setId(30L); sender.setEmail("sender@example.com");
        room = ChatRoom.builder()
                .id(100L)
                .tenant(tenant)
                .landlord(landlord)
                .build();
    }

    // createRoom
    @Test
    void createRoom_existingUsers_savesRoom() {
        when(userRepo.findById(tenant.getId())).thenReturn(Optional.of(tenant));
        when(userRepo.findById(landlord.getId())).thenReturn(Optional.of(landlord));
        when(roomRepo.save(any(ChatRoom.class))).thenAnswer(i -> i.getArgument(0));

        ChatRoom created = service.createRoom(tenant.getId(), landlord.getId());
        assertEquals(tenant, created.getTenant());
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

    // findRoom
    @Test
    void findRoom_existing_returnsRoom() {
        when(roomRepo.findByTenant_IdAndLandlord_Id(tenant.getId(), landlord.getId()))
                .thenReturn(Optional.of(room));
        Optional<ChatRoom> found = service.findRoom(tenant.getId(), landlord.getId());
        assertTrue(found.isPresent());
        assertEquals(room, found.get());
    }

    @Test
    void findRoom_nonExisting_returnsEmpty() {
        when(roomRepo.findByTenant_IdAndLandlord_Id(tenant.getId(), landlord.getId()))
                .thenReturn(Optional.empty());
        Optional<ChatRoom> found = service.findRoom(tenant.getId(), landlord.getId());
        assertTrue(found.isEmpty());
    }

    // getOrCreateRoom
    @Test
    void getOrCreateRoom_existing_returnsExisting() {
        when(roomRepo.findByTenant_IdAndLandlord_Id(tenant.getId(), landlord.getId()))
                .thenReturn(Optional.of(room));
        ChatRoom result = service.getOrCreateRoom(tenant.getId(), landlord.getId());
        assertEquals(room, result);
        verify(roomRepo, never()).save(any());
    }

    @Test
    void getOrCreateRoom_nonExisting_createsAndSaves() {
        ChatRoom newRoom = ChatRoom.builder()
                .id(101L).tenant(tenant).landlord(landlord).build();
        when(roomRepo.findByTenant_IdAndLandlord_Id(tenant.getId(), landlord.getId()))
                .thenReturn(Optional.empty());
        when(userRepo.findById(tenant.getId())).thenReturn(Optional.of(tenant));
        when(userRepo.findById(landlord.getId())).thenReturn(Optional.of(landlord));
        when(roomRepo.save(any(ChatRoom.class))).thenReturn(newRoom);

        ChatRoom result = service.getOrCreateRoom(tenant.getId(), landlord.getId());
        assertEquals(newRoom, result);
        verify(roomRepo).save(any(ChatRoom.class));
    }

    // listRooms
    @Test
    void listRooms_delegatesToRepo() {
        when(roomRepo.findAll(any(Sort.class))).thenReturn(List.of(room));
        List<ChatRoom> list = service.listRooms();
        assertEquals(1, list.size());
        verify(roomRepo).findAll(any(Sort.class));
    }

    // listRoomsForUser
    @Test
    void listRoomsForUser_delegatesToRepo() {
        when(roomRepo.findByTenant_IdOrLandlord_Id(eq(tenant.getId()), eq(tenant.getId()), any(Sort.class)))
                .thenReturn(List.of(room));
        List<ChatRoom> userRooms = service.listRoomsForUser(tenant.getId());
        assertEquals(1, userRooms.size());
    }

    // saveMessage
    @Test
    void saveMessage_existing_savesWithRoomAndSender() {
        ChatMessage msg = ChatMessage.builder().type(ChatMessage.MessageType.CHAT).content("hi").build();
        when(roomRepo.findById(room.getId())).thenReturn(Optional.of(room));
        when(userRepo.findByEmail(sender.getEmail())).thenReturn(Optional.of(sender));
        when(msgRepo.save(any(ChatMessage.class))).thenAnswer(i -> i.getArgument(0));

        CompletableFuture<ChatMessage> future = service.saveMessage(room.getId(), sender.getEmail(), msg);
        ChatMessage saved = future.join();
        assertEquals(room, saved.getRoom());
        assertEquals(sender, saved.getSender());
    }

    @Test
    void saveMessage_unknownRoom_throws() {
        when(roomRepo.findById(room.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.saveMessage(room.getId(), sender.getEmail(), new ChatMessage()));
    }

    @Test
    void saveMessage_unknownSender_throws() {
        when(roomRepo.findById(room.getId())).thenReturn(Optional.of(room));
        when(userRepo.findByEmail(sender.getEmail())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.saveMessage(room.getId(), sender.getEmail(), new ChatMessage()));
    }

    // listMessages
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
        List<ChatMessage> msgs = service.listMessages(room.getId());
        assertNotNull(msgs);
    }

    // updateMessage
    @Test
    void updateMessage_messageNotFound_throws() {
        when(msgRepo.findById(5L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.updateMessage(room.getId(), 5L, new ChatMessage()));
    }

    @Test
    void updateMessage_wrongRoom_throws() {
        ChatRoom other = ChatRoom.builder().id(999L).build();
        ChatMessage existing = ChatMessage.builder().id(5L).room(other).build();
        when(msgRepo.findById(5L)).thenReturn(Optional.of(existing));
        assertThrows(EntityNotFoundException.class,
                () -> service.updateMessage(room.getId(), 5L, new ChatMessage()));
    }

    @Test
    void updateMessage_existing_updatesContent() {
        ChatMessage existing = ChatMessage.builder().id(5L).room(room).content("old").build();
        ChatMessage patch = new ChatMessage(); patch.setContent("new");
        when(msgRepo.findById(5L)).thenReturn(Optional.of(existing));
        when(msgRepo.save(any(ChatMessage.class))).thenAnswer(i -> i.getArgument(0));
        ChatMessage result = service.updateMessage(room.getId(), 5L, patch);
        assertEquals("new", result.getContent());
    }

    // deleteMessage
    @Test
    void deleteMessage_messageNotFound_throws() {
        when(msgRepo.findById(6L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.deleteMessage(room.getId(), 6L));
    }

    @Test
    void deleteMessage_wrongRoom_throws() {
        ChatRoom other = ChatRoom.builder().id(777L).build();
        ChatMessage existing = ChatMessage.builder().id(6L).room(other).build();
        when(msgRepo.findById(6L)).thenReturn(Optional.of(existing));
        assertThrows(EntityNotFoundException.class,
                () -> service.deleteMessage(room.getId(), 6L));
    }

    @Test
    void deleteMessage_existing_deletes() {
        ChatMessage existing = ChatMessage.builder().id(6L).room(room).build();
        when(msgRepo.findById(6L)).thenReturn(Optional.of(existing));
        doNothing().when(msgRepo).delete(existing);
        service.deleteMessage(room.getId(), 6L);
        verify(msgRepo).delete(existing);
    }
}