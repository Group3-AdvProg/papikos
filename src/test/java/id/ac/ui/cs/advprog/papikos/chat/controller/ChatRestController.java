// src/test/java/id/ac/ui/cs/advprog/papikos/chat/controller/ChatRestControllerTest.java
package id.ac.ui.cs.advprog.papikos.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.filter.JwtFilter;
import id.ac.ui.cs.advprog.papikos.chat.dto.CreateMessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChatRestControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ChatService chatService;
    @MockBean private JwtFilter jwtFilter;
    @MockBean private id.ac.ui.cs.advprog.papikos.auth.util.JwtUtil jwtUtil;

    @Test
    void getMessages_returnsJsonArray() throws Exception {
        User sender = new User();
        sender.setId(10L);
        sender.setEmail("user@example.com");
        sender.setPassword("pass");
        sender.setRole("TENANT");

        ChatMessage msg = ChatMessage.builder()
                .id(1L)
                .type(ChatMessage.MessageType.CHAT)
                .content("Test")
                .sender(sender)
                .timestamp(Instant.now())
                .build();
        when(chatService.getAllMessages()).thenReturn(List.of(msg));

        mockMvc.perform(get("/api/chat/messages"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(msg))));
    }

    @Test
    void postMessage_createsAndReturnsMessage() throws Exception {
        CreateMessageRequest req = new CreateMessageRequest();
        req.setSenderId(10L);
        req.setType(ChatMessage.MessageType.CHAT);
        req.setContent("Hi");

        User sender = new User();
        sender.setId(10L);
        sender.setEmail("user@example.com");
        sender.setPassword("pass");
        sender.setRole("TENANT");

        ChatMessage saved = ChatMessage.builder()
                .id(5L)
                .type(req.getType())
                .content(req.getContent())
                .sender(sender)
                .timestamp(Instant.now())
                .build();

        when(chatService.saveMessage(eq(10L), any(ChatMessage.class))).thenReturn(saved);

        mockMvc.perform(post("/api/chat/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(saved)));
    }
}