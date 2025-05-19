package id.ac.ui.cs.advprog.papikos.chat.repository;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ChatMessageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChatMessageRepository repository;

    @Test
    void save_setsIdRoomAndTimestamp_andFindByIdWorks() {
        // 1. Create and persist a ChatRoom
        ChatRoom room = ChatRoom.builder()
                .name("TestRoom")
                .build();
        room = entityManager.persistAndFlush(room);

        // 2. Build a ChatMessage linked to that room
        ChatMessage msg = ChatMessage.builder()
                .type(ChatMessage.MessageType.JOIN)
                .content("User joined")
                .sender("Carol")
                .room(room)              // ← critical: set a non-null, managed room
                // no need to set timestamp; @PrePersist will handle it
                .build();

        // 3. Save via repository (triggers @PrePersist)
        ChatMessage saved = repository.save(msg);

        // 4. Verify id, room, and timestamp are set
        assertNotNull(saved.getId(),       "ID should be generated");
        assertNotNull(saved.getRoom(),     "Room should be set and persisted");
        assertEquals(room.getId(), saved.getRoom().getId(), "Room ID must match");
        assertNotNull(saved.getTimestamp(),"Timestamp should be set by @PrePersist");

        // 5. Clear and fetch from DB to ensure it round-trips
        entityManager.clear();
        Optional<ChatMessage> fetched = repository.findById(saved.getId());
        assertTrue(fetched.isPresent(), "Should retrieve by ID");
        assertEquals("Carol", fetched.get().getSender(), "Sender must match");
    }
}
