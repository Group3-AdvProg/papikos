package id.ac.ui.cs.advprog.papikos.chat.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ChatMessageTest {

    @Test
    void testChatMessageBuilder() {
        // Arrange
        ChatMessage.MessageType type    = ChatMessage.MessageType.CHAT;
        String                       content = "Hello, World!";
        String                       sender  = "Alice";

        // Act: build the ChatMessage
        ChatMessage message = ChatMessage.builder()
                .type(type)
                .content(content)
                .sender(sender)
                .build();

        // Assert: fields set, timestamp is null until persist
        assertNotNull(message,                   "ChatMessage should not be null");
        assertEquals(type, message.getType(),    "Message type should match");
        assertEquals(content, message.getContent(), "Message content should match");
        assertEquals(sender, message.getSender(),   "Message sender should match");
        assertNull(message.getTimestamp(),       "Timestamp should be null before JPA persisting");
    }

    @Test
    void testNoArgConstructorAndSetters() {
        // Arrange
        ChatMessage message = new ChatMessage();
        message.setType(ChatMessage.MessageType.LEAVE);
        message.setContent("Goodbye");
        message.setSender("Bob");
        Instant now = Instant.now();
        message.setTimestamp(now);

        // Assert
        assertEquals(ChatMessage.MessageType.LEAVE, message.getType());
        assertEquals("Goodbye", message.getContent());
        assertEquals("Bob", message.getSender());
        assertEquals(now, message.getTimestamp());
    }
}
