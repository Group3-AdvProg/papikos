// src/test/java/id/ac/ui/cs/advprog/papikos/chat/service/ChatServiceTest.java
package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatMessageRepository;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @Mock private ChatMessageRepository repository;
    @Mock private UserRepository userRepo;
    @InjectMocks private ChatService service;

    @Test
    void saveMessage_existingUser_saves() {
        User sender = new User(); sender.setId(50L);
        ChatMessage msg = ChatMessage.builder()
                .type(ChatMessage.MessageType.CHAT)
                .content("Hello")
                .build();

        when(userRepo.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(repository.save(any(ChatMessage.class))).thenAnswer(i -> i.getArgument(0));

        ChatMessage result = service.saveMessage(sender.getId(), msg);
        assertEquals(sender, result.getSender());
        verify(repository).save(msg);
    }

    @Test
    void saveMessage_unknownUser_throws() {
        when(userRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.saveMessage(99L, new ChatMessage()));
    }

    @Test
    void getAllMessages_callsRepositoryFindAllWithSort() {
        ChatMessage m1 = ChatMessage.builder().content("One").build();
        ChatMessage m2 = ChatMessage.builder().content("Two").build();
        List<ChatMessage> list = List.of(m1, m2);

        when(repository.findAll(any(Sort.class))).thenReturn(list);

        List<ChatMessage> result = service.getAllMessages();
        verify(repository).findAll(any(Sort.class));
        assertEquals(list, result);
    }
}
