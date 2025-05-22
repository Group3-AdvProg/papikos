// src/main/java/id/ac/ui/cs/advprog/papikos/chat/controller/ChatRoomRestController.java
package id.ac.ui.cs.advprog.papikos.chat.controller;

import id.ac.ui.cs.advprog.papikos.chat.dto.CreateRoomRequest;
import id.ac.ui.cs.advprog.papikos.chat.dto.CreateMessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.dto.UpdateMessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
public class ChatRoomRestController {
    private final ChatRoomService service;

    public ChatRoomRestController(ChatRoomService service) {
        this.service = service;
    }

    // C: create a new room between a tenant and landlord
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChatRoom createRoom(@RequestBody CreateRoomRequest req) {
        return service.createRoom(req.getTenantId(), req.getLandlordId());
    }

    // R: list rooms
    @GetMapping
    public List<ChatRoom> listRooms() {
        return service.listRooms();
    }

    // R: list messages in a room
    @GetMapping("/{roomId}/messages")
    public List<ChatMessage> listMessages(@PathVariable Long roomId) {
        return service.listMessages(roomId);
    }

    // C: post a message to a room, authored by a real user (blocks on the async future)
    @PostMapping("/{roomId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessage postMessage(
            @PathVariable Long roomId,
            @RequestBody CreateMessageRequest req) {

        ChatMessage msg = ChatMessage.builder()
                .type(req.getType())
                .content(req.getContent())
                .build();
        // block until the @Async saveMessage completes
        return service.saveMessage(roomId, req.getSenderId(), msg).join();
    }

    // U: edit/update a message in a room
    @PutMapping("/{roomId}/messages/{messageId}")
    public ChatMessage updateMessage(
            @PathVariable Long roomId,
            @PathVariable Long messageId,
            @RequestBody UpdateMessageRequest req) {

        ChatMessage updated = new ChatMessage();
        updated.setContent(req.getContent());
        return service.updateMessage(roomId, messageId, updated);
    }

    // D: delete a message from a room
    @DeleteMapping("/{roomId}/messages/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(
            @PathVariable Long roomId,
            @PathVariable Long messageId) {
        service.deleteMessage(roomId, messageId);
    }
}
