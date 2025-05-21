// src/test/java/id/ac/ui/cs/advprog/papikos/chat/controller/ChatRoomStompControllerTest.java
package id.ac.ui.cs.advprog.papikos.chat.controller;

import id.ac.ui.cs.advprog.papikos.chat.dto.CreateMessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        // Prepare the request DTO
        CreateMessageRequest req = new CreateMessageRequest();
        req.setSenderId(2L);
        req.setType(ChatMessage.MessageType.CHAT);
        req.setContent("hello");

        // Prepare a ChatMessage that the mock service will return
        ChatMessage returned = ChatMessage.builder()
                .type(req.getType())
                .content(req.getContent())
                .build();

        // Stub the service call with any ChatMessage argument
        when(service.saveMessage(eq(2L), eq(2L), any(ChatMessage.class)))
                .thenReturn(returned);

        // Invoke the controller
        ChatMessage out = controller.sendMessage(2L, req);

        // Verify result and interaction
        assertSame(returned, out, "Controller should return exactly what the service returns");
        verify(service).saveMessage(eq(2L), eq(2L), any(ChatMessage.class));
    }

    @Test
    void addUser_marksJoinType() {
        CreateMessageRequest req = new CreateMessageRequest();
        req.setSenderId(2L);
        req.setContent("joined");

        ChatMessage out = controller.addUser(2L, req);

        assertEquals(ChatMessage.MessageType.JOIN, out.getType(), "Type should be JOIN");
        assertEquals("joined", out.getContent(), "Content should be preserved");
    }
}
