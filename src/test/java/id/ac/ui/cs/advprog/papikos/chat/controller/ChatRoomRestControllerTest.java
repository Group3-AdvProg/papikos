package id.ac.ui.cs.advprog.papikos.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.filter.JwtFilter;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.auth.util.JwtUtil;
import id.ac.ui.cs.advprog.papikos.chat.dto.CreateMessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.dto.CreateRoomRequest;
import id.ac.ui.cs.advprog.papikos.chat.dto.UpdateMessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MVC slice tests for {@link ChatRoomRestController}.
 * <p>
 * An inner {@link RestControllerAdvice} maps {@link EntityNotFoundException}
 * to 404 so we can cover the failure branches as well.
 */
@WebMvcTest(ChatRoomRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ChatRoomRestControllerTest.NotFoundAdvice.class)
class ChatRoomRestControllerTest {

    @RestControllerAdvice
    static class NotFoundAdvice {
        @ExceptionHandler(EntityNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        void handle() { /* no body needed */ }
    }

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;

    @MockBean private ChatRoomService service;
    @MockBean private UserRepository  userRepo;
    @MockBean private JwtFilter       jwtFilter; // context placeholders
    @MockBean private JwtUtil         jwtUtil;   // ─┘

    private final String TENANT   = "tenant@example.com";
    private final String LANDLORD = "landlord@example.com";

    private User tenant;
    private User landlord;

    @BeforeEach
    void setUp() {
        tenant   = new User(); tenant.setId(1L); tenant.setEmail(TENANT);
        landlord = new User(); landlord.setId(2L); landlord.setEmail(LANDLORD);

        // default stubbing – individual tests may override
        when(userRepo.findByEmail(TENANT)).thenReturn(Optional.of(tenant));
        when(userRepo.findByEmail(LANDLORD)).thenReturn(Optional.of(landlord));
    }

    /* ───────────────────────────────────────── create / get ──────────── */

    @Test
    void createRoom_new_returns201() throws Exception {
        when(service.findRoom(1L, 2L)).thenReturn(Optional.empty());

        ChatRoom created = ChatRoom.builder()
                .id(10L).tenant(tenant).landlord(landlord).build();
        when(service.createRoom(1L, 2L)).thenReturn(created);

        CreateRoomRequest req = new CreateRoomRequest();
        req.setLandlordEmail(LANDLORD);

        mvc.perform(post("/api/chat/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req))
                        .principal(new TestingAuthenticationToken(TENANT, null)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void createRoom_existing_returns200() throws Exception {
        ChatRoom room = ChatRoom.builder()
                .id(99L).tenant(tenant).landlord(landlord).build();
        when(service.findRoom(1L, 2L)).thenReturn(Optional.of(room));

        CreateRoomRequest req = new CreateRoomRequest();
        req.setLandlordEmail(LANDLORD);

        mvc.perform(post("/api/chat/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req))
                        .principal(new TestingAuthenticationToken(TENANT, null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99));
    }

    @Test
    void createRoom_tenantMissing_returns404() throws Exception {
        when(userRepo.findByEmail(TENANT)).thenReturn(Optional.empty());

        CreateRoomRequest req = new CreateRoomRequest();
        req.setLandlordEmail(LANDLORD);

        mvc.perform(post("/api/chat/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req))
                        .principal(new TestingAuthenticationToken(TENANT, null)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createRoom_landlordMissing_returns404() throws Exception {
        when(userRepo.findByEmail(LANDLORD)).thenReturn(Optional.empty());

        CreateRoomRequest req = new CreateRoomRequest();
        req.setLandlordEmail(LANDLORD);

        mvc.perform(post("/api/chat/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req))
                        .principal(new TestingAuthenticationToken(TENANT, null)))
                .andExpect(status().isNotFound());
    }

    /* ───────────────────────────────────────── list rooms / user nf ──── */

    @Test
    void listRooms_returnsUserRooms() throws Exception {
        ChatRoom r = ChatRoom.builder()
                .id(7L).tenant(tenant).landlord(landlord).build();
        when(service.listRoomsForUser(1L)).thenReturn(List.of(r));

        mvc.perform(get("/api/chat/rooms")
                        .principal(new TestingAuthenticationToken(TENANT, null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7));
    }

    @Test
    void listRooms_userMissing_returns404() throws Exception {
        when(userRepo.findByEmail(TENANT)).thenReturn(Optional.empty());

        mvc.perform(get("/api/chat/rooms")
                        .principal(new TestingAuthenticationToken(TENANT, null)))
                .andExpect(status().isNotFound());
    }

    /* ───────────────────────────────────────── messages CRUD ─────────── */

    @Test
    void listMessages_returnsRoomMessages() throws Exception {
        ChatMessage m = ChatMessage.builder()
                .id(20L).content("hey").type(ChatMessage.MessageType.CHAT)
                .sender(new User()).timestamp(Instant.now()).build();
        when(service.listMessages(5L)).thenReturn(List.of(m));

        mvc.perform(get("/api/chat/rooms/5/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20));
    }

    @Test
    void postMessage_createsMessage() throws Exception {
        CreateMessageRequest req = new CreateMessageRequest();
        req.setSenderEmail("someone@mail.com");
        req.setType(ChatMessage.MessageType.CHAT);
        req.setContent("hi");

        ChatMessage saved = ChatMessage.builder()
                .id(99L).type(req.getType()).content(req.getContent())
                .sender(new User()).timestamp(Instant.now()).build();

        when(service.saveMessage(eq(3L), eq("someone@mail.com"), any(ChatMessage.class)))
                .thenReturn(CompletableFuture.completedFuture(saved));

        mvc.perform(post("/api/chat/rooms/3/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(99));
    }

    @Test
    void updateMessage_returnsUpdated() throws Exception {
        UpdateMessageRequest req = new UpdateMessageRequest();
        req.setContent("upd");

        ChatMessage upd = ChatMessage.builder()
                .id(11L).content("upd").type(ChatMessage.MessageType.CHAT)
                .sender(new User()).build();
        when(service.updateMessage(eq(3L), eq(11L), any(ChatMessage.class)))
                .thenReturn(upd);

        mvc.perform(put("/api/chat/rooms/3/messages/11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("upd"));
    }

    @Test
    void deleteMessage_returns204() throws Exception {
        doNothing().when(service).deleteMessage(3L, 11L);

        mvc.perform(delete("/api/chat/rooms/3/messages/11"))
                .andExpect(status().isNoContent());
    }
}
