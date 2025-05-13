package id.ac.ui.cs.advprog.papikos.chat.repository;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository repository;

    @Test
    void saveAndFindAll_roundTripsEntities() {
        ChatRoom a = ChatRoom.builder().name("A").build();
        ChatRoom b = ChatRoom.builder().name("B").build();
        repository.save(a);
        repository.save(b);

        List<ChatRoom> rooms = repository.findAll();
        assertThat(rooms)
                .extracting(ChatRoom::getName)
                .containsExactlyInAnyOrder("A", "B");
    }
}
