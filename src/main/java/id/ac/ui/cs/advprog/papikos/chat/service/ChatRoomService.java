// src/main/java/id/ac/ui/cs/advprog/papikos/chat/service/ChatRoomService.java
package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomRepository;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatMessageRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ChatRoomService {
    private final ChatRoomRepository roomRepo;
    private final ChatMessageRepository msgRepo;

    public ChatRoomService(ChatRoomRepository roomRepo,
                           ChatMessageRepository msgRepo) {
        this.roomRepo = roomRepo;
        this.msgRepo = msgRepo;
    }

    /**
     * Create a new chat room with the given name.
     */
    public ChatRoom createRoom(String name) {
        ChatRoom room = ChatRoom.builder()
                .name(name)
                .build();
        return roomRepo.save(room);
    }

    /**
     * List all chat rooms, ordered by creation time.
     */
    public List<ChatRoom> listRooms() {
        return roomRepo.findAll(Sort.by("createdAt"));
    }

    /**
     * Save a new message into the specified room.
     */
    public ChatMessage saveMessage(Long roomId, ChatMessage msg) {
        ChatRoom room = roomRepo.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found: " + roomId));
        msg.setRoom(room);
        return msgRepo.save(msg);
    }

    /**
     * List all messages in the specified room, ordered by timestamp.
     */
    public List<ChatMessage> listMessages(Long roomId) {
        // throws EntityNotFoundException if room doesn't exist
        roomRepo.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found: " + roomId));
        return msgRepo.findByRoomIdOrderByTimestampAsc(roomId);
    }

    /**
     * Update the content of an existing message in the specified room.
     */
    public ChatMessage updateMessage(Long roomId, Long messageId, ChatMessage updatedMsg) {
        ChatMessage existing = msgRepo.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found: " + messageId));
        if (!existing.getRoom().getId().equals(roomId)) {
            throw new EntityNotFoundException(
                    "Message " + messageId + " does not belong to room " + roomId);
        }
        existing.setContent(updatedMsg.getContent());
        return msgRepo.save(existing);
    }

    /**
     * Delete a message from the specified room.
     */
    public void deleteMessage(Long roomId, Long messageId) {
        ChatMessage existing = msgRepo.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found: " + messageId));
        if (!existing.getRoom().getId().equals(roomId)) {
            throw new EntityNotFoundException(
                    "Message " + messageId + " does not belong to room " + roomId);
        }
        msgRepo.delete(existing);
    }
}
