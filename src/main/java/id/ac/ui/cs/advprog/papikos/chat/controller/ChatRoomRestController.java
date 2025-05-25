package id.ac.ui.cs.advprog.papikos.chat.controller;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.chat.dto.CreateMessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.dto.CreateRoomRequest;
import id.ac.ui.cs.advprog.papikos.chat.dto.UpdateMessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat/rooms")
public class ChatRoomRestController {

    private static final Logger logger = LoggerFactory.getLogger(ChatRoomRestController.class);

    private final ChatRoomService service;
    private final UserRepository  userRepo;

    public ChatRoomRestController(ChatRoomService service,
                                  UserRepository userRepo) {
        this.service  = service;
        this.userRepo = userRepo;
    }

    @PostMapping
    public ResponseEntity<ChatRoom> createOrGetRoom(Authentication auth,
                                                    @RequestBody CreateRoomRequest req) {
        logger.info("User [{}] requests room with landlord [{}]", auth.getName(), req.getLandlordEmail());
        User tenant = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> {
                    logger.warn("Tenant not found: {}", auth.getName());
                    return new EntityNotFoundException("Tenant not found");
                });
        User landlord = userRepo.findByEmail(req.getLandlordEmail())
                .orElseThrow(() -> {
                    logger.warn("Landlord not found: {}", req.getLandlordEmail());
                    return new EntityNotFoundException("Landlord not found");
                });

        Optional<ChatRoom> existing = service.findRoom(tenant.getId(), landlord.getId());
        if (existing.isPresent()) {
            logger.info("Existing room [{}] returned for users {} and {}", existing.get().getId(), tenant.getId(), landlord.getId());
            return ResponseEntity.ok(existing.get());
        }

        ChatRoom created = service.createRoom(tenant.getId(), landlord.getId());
        logger.info("Created new room [{}] for users {} and {}", created.getId(), tenant.getId(), landlord.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<ChatRoom> listRoomsForCurrentUser(Authentication auth) {
        logger.info("Listing rooms for user [{}]", auth.getName());
        Long userId = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", auth.getName());
                    return new EntityNotFoundException("User not found");
                })
                .getId();
        List<ChatRoom> rooms = service.listRoomsForUser(userId);
        logger.debug("Found {} rooms for user [{}]", rooms.size(), auth.getName());
        return rooms;
    }

    @GetMapping("/{roomId}/messages")
    public List<ChatMessage> listMessages(@PathVariable Long roomId) {
        logger.info("Listing messages for room [{}]", roomId);
        List<ChatMessage> msgs = service.listMessages(roomId);
        logger.debug("Found {} messages in room [{}]", msgs.size(), roomId);
        return msgs;
    }

    @PostMapping("/{roomId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessage postMessage(@PathVariable Long roomId,
                                   @RequestBody CreateMessageRequest req) {
        logger.info("Posting message to room [{}] from [{}]: {}", roomId, req.getSenderEmail(), req.getContent());
        ChatMessage msg = ChatMessage.builder()
                .type(req.getType())
                .content(req.getContent())
                .build();
        ChatMessage saved = service.saveMessage(roomId, req.getSenderEmail(), msg).join();
        logger.info("Saved message [{}] in room [{}]", saved.getId(), roomId);
        return saved;
    }

    @PutMapping("/{roomId}/messages/{messageId}")
    public ChatMessage updateMessage(@PathVariable Long roomId,
                                     @PathVariable Long messageId,
                                     @RequestBody UpdateMessageRequest req) {
        logger.info("Updating message [{}] in room [{}] with new content: {}", messageId, roomId, req.getContent());
        ChatMessage patch = new ChatMessage();
        patch.setContent(req.getContent());
        ChatMessage updated = service.updateMessage(roomId, messageId, patch);
        logger.info("Updated message [{}] in room [{}]", updated.getId(), roomId);
        return updated;
    }

    @DeleteMapping("/{roomId}/messages/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable Long roomId,
                              @PathVariable Long messageId) {
        logger.info("Deleting message [{}] from room [{}]", messageId, roomId);
        service.deleteMessage(roomId, messageId);
        logger.info("Deleted message [{}] from room [{}]", messageId, roomId);
    }
}
