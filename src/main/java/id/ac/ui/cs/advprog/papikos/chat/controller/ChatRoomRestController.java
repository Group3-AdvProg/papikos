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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat/rooms")
public class ChatRoomRestController {

    private final ChatRoomService service;
    private final UserRepository  userRepo;

    public ChatRoomRestController(ChatRoomService service,
                                  UserRepository userRepo) {
        this.service  = service;
        this.userRepo = userRepo;
    }

    /* ────────────────────────────────────────────────────────────────────
     *  Get-or-create 1:1 room (idempotent)
     *  ─► 201 CREATED  when a new room is saved
     *  ─► 200 OK       when the room already exists
     * ─────────────────────────────────────────────────────────────────── */
    @PostMapping
    public ResponseEntity<ChatRoom> createOrGetRoom(Authentication auth,
                                                    @RequestBody CreateRoomRequest req) {
        /* 1 — resolve users */
        User tenant   = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found"));

        User landlord = userRepo.findByEmail(req.getLandlordEmail())
                .orElseThrow(() -> new EntityNotFoundException("Landlord not found"));

        /* 2 — already there? */
        Optional<ChatRoom> existing =
                service.findRoom(tenant.getId(), landlord.getId());

        if (existing.isPresent()) {
            return ResponseEntity.ok(existing.get());           // 200
        }

        /* 3 — create new */
        ChatRoom created = service.createRoom(tenant.getId(), landlord.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201
    }

    /* ─────────────────────────────────────────────────────────────────── */

    @GetMapping
    public List<ChatRoom> listRoomsForCurrentUser(Authentication auth) {
        Long userId = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"))
                .getId();
        return service.listRoomsForUser(userId);
    }

    @GetMapping("/{roomId}/messages")
    public List<ChatMessage> listMessages(@PathVariable Long roomId) {
        return service.listMessages(roomId);
    }

    @PostMapping("/{roomId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessage postMessage(@PathVariable Long roomId,
                                   @RequestBody CreateMessageRequest req) {
        ChatMessage msg = ChatMessage.builder()
                .type(req.getType())
                .content(req.getContent())
                .build();
        return service.saveMessage(roomId, req.getSenderEmail(), msg).join();
    }

    @PutMapping("/{roomId}/messages/{messageId}")
    public ChatMessage updateMessage(@PathVariable Long roomId,
                                     @PathVariable Long messageId,
                                     @RequestBody UpdateMessageRequest req) {
        ChatMessage patch = new ChatMessage();
        patch.setContent(req.getContent());
        return service.updateMessage(roomId, messageId, patch);
    }

    @DeleteMapping("/{roomId}/messages/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable Long roomId,
                              @PathVariable Long messageId) {
        service.deleteMessage(roomId, messageId);
    }
}
