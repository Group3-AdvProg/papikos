package id.ac.ui.cs.advprog.papikos.chat.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ChatMessageTest {

    @Test
    void builder_and_getters_includeRoom() {
        ChatRoom room = ChatRoom.builder().id(7L).name("Room7").build();
        Instant now = Instant.now();

        ChatMessage msg = ChatMessage.builder()
                .type(ChatMessage.MessageType.CHAT)
                .content("hello")
                .sender("u")
                .timestamp(now)
                .room(room)
                .build();

        assertEquals(ChatMessage.MessageType.CHAT, msg.getType());
        assertEquals("hello", msg.getContent());
        assertEquals("u", msg.getSender());
        assertEquals(now, msg.getTimestamp());
        assertEquals(room, msg.getRoom());
    }
}
