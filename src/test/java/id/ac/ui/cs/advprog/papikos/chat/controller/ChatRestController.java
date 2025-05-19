package id.ac.ui.cs.advprog.papikos.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatRestController.class)
class ChatRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getMessages_returnsJsonArray() throws Exception {
        ChatMessage msg = ChatMessage.builder()
                .id(1L)
                .type(ChatMessage.MessageType.CHAT)
                .content("Test")
                .sender("User")
                .build();

        when(chatService.getAllMessages()).thenReturn(List.of(msg));

        mockMvc.perform(get("/api/chat/messages"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(List.of(msg))));
    }

    @Test
    void postMessage_createsAndReturnsMessage() throws Exception {
        ChatMessage input = ChatMessage.builder()
                .type(ChatMessage.MessageType.CHAT)
                .content("Hi")
                .sender("Bob")
                .build();

        ChatMessage saved = ChatMessage.builder()
                .id(5L)
                .type(input.getType())
                .content(input.getContent())
                .sender(input.getSender())
                .timestamp(java.time.Instant.now())
                .build();

        when(chatService.saveMessage(any(ChatMessage.class))).thenReturn(saved);

        mockMvc.perform(post("/api/chat/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(saved)));
    }
}
