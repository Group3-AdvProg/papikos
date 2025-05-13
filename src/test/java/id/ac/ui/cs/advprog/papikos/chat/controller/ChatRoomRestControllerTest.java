package id.ac.ui.cs.advprog.papikos.chat.controller;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatRoomRestController.class)
class ChatRoomRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ChatRoomService service;

    @Test
    void createRoom_returnsCreatedRoom() throws Exception {
        ChatRoom room = new ChatRoom(1L, "TestRoom", Instant.now());
        when(service.createRoom("TestRoom")).thenReturn(room);

        mvc.perform(post("/api/chat/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TestRoom\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("TestRoom"));
    }

    @Test
    void listRooms_returnsAllRooms() throws Exception {
        ChatRoom a = new ChatRoom(1L, "A", Instant.now());
        when(service.listRooms()).thenReturn(Arrays.asList(a));

        mvc.perform(get("/api/chat/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("A"));
    }

    @Test
    void listMessages_returnsRoomMessages() throws Exception {
        ChatMessage m = ChatMessage.builder()
                .id(10L)
                .type(ChatMessage.MessageType.CHAT)
                .content("hey")
                .sender("u")
                .timestamp(Instant.now())
                .build();
        when(service.listMessages(1L)).thenReturn(Collections.singletonList(m));

        mvc.perform(get("/api/chat/rooms/1/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].content").value("hey"));
    }

    @Test
    void postMessage_createsMessage() throws Exception {
        ChatMessage m = ChatMessage.builder()
                .id(5L)
                .type(ChatMessage.MessageType.CHAT)
                .content("hi")
                .sender("u")
                .timestamp(Instant.now())
                .build();
        when(service.saveMessage(eq(1L), any(ChatMessage.class))).thenReturn(m);

        mvc.perform(post("/api/chat/rooms/1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"CHAT\",\"content\":\"hi\",\"sender\":\"u\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.content").value("hi"));
    }
}
