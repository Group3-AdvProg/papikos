package id.ac.ui.cs.advprog.papikos.chat.repository;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ChatMessageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChatMessageRepository repository;

    @Test
    void save_setsIdAndTimestamp_andFindByIdWorks() {
        ChatMessage msg = ChatMessage.builder()
                .type(ChatMessage.MessageType.JOIN)
                .content("User joined")
                .sender("Carol")
                .build();

        // Save via repository (will trigger @PrePersist on the entity)
        ChatMessage saved = repository.save(msg);

        assertNotNull(saved.getId(), "ID should be generated");
        assertNotNull(saved.getTimestamp(), "Timestamp should be set by @PrePersist");

        // Clear persistence context and fetch again
        entityManager.clear();

        Optional<ChatMessage> fetched = repository.findById(saved.getId());
        assertTrue(fetched.isPresent(), "Should retrieve by ID");
        assertEquals("Carol", fetched.get().getSender(), "Sender must match");
    }
}
