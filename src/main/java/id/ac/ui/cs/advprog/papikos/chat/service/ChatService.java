package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatMessageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository messageRepo;
    private final UserRepository userRepo;

    public ChatService(ChatMessageRepository messageRepo, UserRepository userRepo) {
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
    }

    public ChatMessage saveMessage(String senderEmail, ChatMessage msg) {
        User sender = userRepo.findByEmail(senderEmail)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + senderEmail + " not found"));

        msg.setSender(sender);
        return messageRepo.save(msg);
    }

    public List<ChatMessage> getAllMessages() {
        return messageRepo.findAll();
    }
}
