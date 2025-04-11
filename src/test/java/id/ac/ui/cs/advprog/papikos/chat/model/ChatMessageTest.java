package id.ac.ui.cs.advprog.papikos.chat.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChatMessageTest {

    @Test
    void testChatMessageConstructor() {
        // Arrange: Define a sample message using the constructor.
        ChatMessage.MessageType type = ChatMessage.MessageType.CHAT;
        String content = "Hello, World!";
        String sender = "Alice";

        // Act: Create the ChatMessage instance.
        ChatMessage message = new ChatMessage(type, content, sender);

        // Assert: Ensure the constructor correctly sets all the properties.
        assertNotNull(message, "ChatMessage should not be null");
        assertEquals(type, message.getType(), "Message type should match");
        assertEquals(content, message.getContent(), "Message content should match");
        assertEquals(sender, message.getSender(), "Message sender should match");
    }
}
