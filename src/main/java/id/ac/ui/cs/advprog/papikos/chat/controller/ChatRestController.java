package id.ac.ui.cs.advprog.papikos.chat.controller;

import id.ac.ui.cs.advprog.papikos.chat.dto.CreateMessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private static final Logger logger = LoggerFactory.getLogger(ChatRestController.class);
    private final ChatService chatService;

    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/messages")
    public List<ChatMessage> getMessages() {
        logger.info("Fetching all chat messages");
        List<ChatMessage> messages = chatService.getAllMessages();
        logger.debug("Fetched {} messages", messages.size());
        return messages;
    }

    @PostMapping("/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessage postMessage(@RequestBody CreateMessageRequest req) {
        logger.info("Received new message from {}: {}", req.getSenderEmail(), req.getContent());
        ChatMessage toSave = ChatMessage.builder()
                .type(req.getType())
                .content(req.getContent())
                .build();

        ChatMessage saved = chatService.saveMessage(req.getSenderEmail(), toSave);
        logger.info("Saved message [id={}] for sender {}", saved.getId(), req.getSenderEmail());
        return saved;
    }
}
