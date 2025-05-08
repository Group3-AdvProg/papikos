package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatMessageRepository repository;

    @InjectMocks
    private ChatService service;

    @Test
    void saveMessage_callsRepositorySave() {
        ChatMessage msg = ChatMessage.builder()
                .type(ChatMessage.MessageType.CHAT)
                .content("Hello")
                .sender("Alice")
                .build();

        when(repository.save(msg)).thenReturn(msg);

        ChatMessage result = service.saveMessage(msg);

        verify(repository, times(1)).save(msg);
        assertEquals(msg, result, "Service should return exactly what the repository returns");
    }

    @Test
    void getAllMessages_callsRepositoryFindAllWithSort() {
        ChatMessage m1 = ChatMessage.builder()
                .type(ChatMessage.MessageType.CHAT)
                .content("One")
                .sender("A")
                .build();
        ChatMessage m2 = ChatMessage.builder()
                .type(ChatMessage.MessageType.CHAT)
                .content("Two")
                .sender("B")
                .build();
        List<ChatMessage> list = List.of(m1, m2);

        when(repository.findAll(any(Sort.class))).thenReturn(list);

        List<ChatMessage> result = service.getAllMessages();

        verify(repository, times(1)).findAll(any(Sort.class));
        assertEquals(list, result, "Service should return the list from repository");
    }
}
