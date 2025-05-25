// src/test/java/id/ac/ui/cs/advprog/papikos/chat/controller/ChatRoomStompControllerTest.java
package id.ac.ui.cs.advprog.papikos.chat.controller;

import id.ac.ui.cs.advprog.papikos.chat.dto.CreateMessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        // Arrange
        CreateMessageRequest req = new CreateMessageRequest();
        req.setSenderEmail("user@example.com");
        req.setType(ChatMessage.MessageType.CHAT);
        req.setContent("hello");

        ChatMessage returned = ChatMessage.builder()
                .type(req.getType())
                .content(req.getContent())
                .build();

        // Stub the service to return a completed future
        doReturn(CompletableFuture.completedFuture(returned))
                .when(service).saveMessage(eq(2L), eq("user@example.com"), any(ChatMessage.class));

        // Act
        ChatMessage out = controller.sendMessage(2L, req);

        // Assert
        assertSame(returned, out, "Controller should return exactly what the service returns");
        verify(service).saveMessage(eq(2L), eq("user@example.com"), any(ChatMessage.class));
    }

    @Test
    void addUser_marksJoinType() {
        // Arrange
        CreateMessageRequest req = new CreateMessageRequest();
        req.setContent("joined");

        // Act
        ChatMessage out = controller.addUser(2L, req);

        // Assert
        assertEquals(ChatMessage.MessageType.JOIN, out.getType());
        assertEquals("joined", out.getContent());
    }
}
