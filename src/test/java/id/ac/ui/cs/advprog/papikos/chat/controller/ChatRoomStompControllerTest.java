package id.ac.ui.cs.advprog.papikos.chat.controller;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatRoomStompControllerTest {
    private ChatRoomService service;
    private ChatRoomStompController controller;

    @BeforeEach
    void setUp() {
        service = mock(ChatRoomService.class);
        controller = new ChatRoomStompController(service);
    }

    @Test
    void sendMessage_delegatesAndReturns() {
        ChatMessage msg = ChatMessage.builder()
                .type(ChatMessage.MessageType.CHAT)
                .content("hello")
                .sender("x")
                .timestamp(Instant.now())
                .build();
        when(service.saveMessage(2L, msg)).thenReturn(msg);

        ChatMessage out = controller.sendMessage(2L, msg);
        assertSame(msg, out);
        verify(service).saveMessage(2L, msg);
    }

    @Test
    void addUser_marksJoinType() {
        ChatMessage msg = ChatMessage.builder()
                .content("joined")
                .sender("y")
                .build();

        ChatMessage out = controller.addUser(2L, msg);
        assertEquals(ChatMessage.MessageType.JOIN, out.getType());
        assertSame(msg, out);
    }
}
