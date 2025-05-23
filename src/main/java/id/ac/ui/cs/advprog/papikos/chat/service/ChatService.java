// src/main/java/id/ac/ui/cs/advprog/papikos/chat/service/ChatService.java
package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatMessageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    private final ChatMessageRepository repo;
    private final UserRepository userRepo;

    public ChatService(ChatMessageRepository repo,
                       UserRepository userRepo) {
        this.repo     = repo;
        this.userRepo = userRepo;
    }

    /** Save a global chat message authored by a specific user */
    public ChatMessage saveMessage(Long senderId, ChatMessage message) {
        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found: " + senderId));
        message.setSender(sender);
        return repo.save(message);
    }

    /** List all global messages */
    public List<ChatMessage> getAllMessages() {
        return repo.findAll(Sort.by(Sort.Direction.ASC, "timestamp"));
    }
}
