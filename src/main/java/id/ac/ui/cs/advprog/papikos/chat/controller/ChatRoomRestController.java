package id.ac.ui.cs.advprog.papikos.chat.controller;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat/rooms")
public class ChatRoomRestController {
    private final ChatRoomService service;

    public ChatRoomRestController(ChatRoomService service) {
        this.service = service;
    }

    // C: create a new room
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChatRoom createRoom(@RequestBody Map<String, String> body) {
        return service.createRoom(body.get("name"));
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

    // C: post a message to a room
    @PostMapping("/{roomId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessage postMessage(@PathVariable Long roomId,
                                   @RequestBody ChatMessage message) {
        return service.saveMessage(roomId, message);
    }

    // U: edit/update a message in a room
    @PutMapping("/{roomId}/messages/{messageId}")
    public ChatMessage updateMessage(@PathVariable Long roomId,
                                     @PathVariable Long messageId,
                                     @RequestBody ChatMessage message) {
        return service.updateMessage(roomId, messageId, message);
    }

    // D: delete a message from a room
    @DeleteMapping("/{roomId}/messages/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable Long roomId,
                              @PathVariable Long messageId) {
        service.deleteMessage(roomId, messageId);
    }
}
