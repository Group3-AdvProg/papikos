// src/test/java/id/ac/ui/cs/advprog/papikos/chat/controller/ChatRoomRestControllerTest.java
package id.ac.ui.cs.advprog.papikos.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.filter.JwtFilter;
import id.ac.ui.cs.advprog.papikos.chat.dto.CreateRoomRequest;
import id.ac.ui.cs.advprog.papikos.chat.dto.CreateMessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.dto.UpdateMessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatRoomRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChatRoomRestControllerTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ChatRoomService service;
    @MockBean private JwtFilter jwtFilter;
    @MockBean private id.ac.ui.cs.advprog.papikos.auth.util.JwtUtil jwtUtil;

    @Test
    void createRoom_returnsCreatedRoom() throws Exception {
        CreateRoomRequest req = new CreateRoomRequest();
        req.setTenantId(1L);
        req.setLandlordId(2L);

        User tenant = new User(); tenant.setId(1L);
        User landlord = new User(); landlord.setId(2L);
        ChatRoom room = ChatRoom.builder()
                .id(10L)
                .tenant(tenant)
                .landlord(landlord)
                .build();

        when(service.createRoom(1L, 2L)).thenReturn(room);

        mvc.perform(post("/api/chat/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.tenant.id").value(1))
                .andExpect(jsonPath("$.landlord.id").value(2));
    }

    @Test
    void listRooms_returnsAllRooms() throws Exception {
        User tenant = new User(); tenant.setId(1L);
        User landlord = new User(); landlord.setId(2L);
        ChatRoom room = ChatRoom.builder()
                .id(10L)
                .tenant(tenant)
                .landlord(landlord)
                .build();
        when(service.listRooms()).thenReturn(List.of(room));

        mvc.perform(get("/api/chat/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].tenant.id").value(1))
                .andExpect(jsonPath("$[0].landlord.id").value(2));
    }

    @Test
    void listMessages_returnsRoomMessages() throws Exception {
        User sender = new User(); sender.setId(5L);
        ChatMessage m = ChatMessage.builder()
                .id(20L)
                .type(ChatMessage.MessageType.CHAT)
                .content("hey")
                .sender(sender)
                .timestamp(Instant.now())
                .build();
        when(service.listMessages(1L)).thenReturn(List.of(m));

        mvc.perform(get("/api/chat/rooms/1/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20))
                .andExpect(jsonPath("$[0].sender.id").value(5))
                .andExpect(jsonPath("$[0].content").value("hey"));
    }

    @Test
    void postMessage_createsMessage() throws Exception {
        CreateMessageRequest req = new CreateMessageRequest();
        req.setSenderId(5L);
        req.setType(ChatMessage.MessageType.CHAT);
        req.setContent("hi");

        User sender = new User(); sender.setId(5L);
        ChatMessage m = ChatMessage.builder()
                .id(15L)
                .type(req.getType())
                .content(req.getContent())
                .sender(sender)
                .timestamp(Instant.now())
                .build();
        when(service.saveMessage(eq(1L), eq(5L), any(ChatMessage.class))).thenReturn(m);

        mvc.perform(post("/api/chat/rooms/1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.sender.id").value(5))
                .andExpect(jsonPath("$.content").value("hi"));
    }

    @Test
    void updateMessage_returnsUpdatedMessage() throws Exception {
        UpdateMessageRequest req = new UpdateMessageRequest();
        req.setContent("updated content");

        ChatMessage updated = ChatMessage.builder()
                .id(25L)
                .type(ChatMessage.MessageType.CHAT)
                .content(req.getContent())
                .sender(new User())
                .timestamp(Instant.now())
                .build();
        when(service.updateMessage(eq(1L), eq(5L), any(ChatMessage.class)))
                .thenReturn(updated);

        mvc.perform(put("/api/chat/rooms/1/messages/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(25))
                .andExpect(jsonPath("$.content").value("updated content"));
    }

    @Test
    void deleteMessage_returnsNoContent() throws Exception {
        doNothing().when(service).deleteMessage(1L, 5L);

        mvc.perform(delete("/api/chat/rooms/1/messages/5"))
                .andExpect(status().isNoContent());
    }
}