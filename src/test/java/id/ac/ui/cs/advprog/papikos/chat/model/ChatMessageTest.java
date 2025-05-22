package id.ac.ui.cs.advprog.papikos.chat.model;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ChatMessageTest {

    @Test
    void builder_and_getters_includeRoomAndSender() {
        // create sender user
        User sender = new User();
        sender.setId(3L);
        sender.setEmail("sender@example.com");
        sender.setPassword("password");
        sender.setRole("TENANT");

        // create tenant and landlord for room
        User tenant = new User();
        tenant.setId(1L);
        tenant.setEmail("tenant@example.com");
        tenant.setPassword("pass");
        tenant.setRole("TENANT");

        User landlord = new User();
        landlord.setId(2L);
        landlord.setEmail("landlord@example.com");
        landlord.setPassword("pass");
        landlord.setRole("LANDLORD");

        // build ChatRoom with tenant and landlord
        ChatRoom room = ChatRoom.builder()
                .id(7L)
                .tenant(tenant)
                .landlord(landlord)
                .build();

        Instant now = Instant.now();

        // build ChatMessage
        ChatMessage msg = ChatMessage.builder()
                .type(ChatMessage.MessageType.CHAT)
                .content("hello")
                .sender(sender)
                .timestamp(now)
                .room(room)
                .build();

        assertEquals(ChatMessage.MessageType.CHAT, msg.getType());
        assertEquals("hello", msg.getContent());
        assertSame(sender, msg.getSender());
        assertEquals(now, msg.getTimestamp());
        assertSame(room, msg.getRoom());
    }
}