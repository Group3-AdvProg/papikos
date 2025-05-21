// src/main/java/id/ac/ui/cs/advprog/papikos/chat/service/ChatRoomService.java
package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomRepository;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatMessageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomService {
    private final ChatRoomRepository roomRepo;
    private final ChatMessageRepository msgRepo;
    private final UserRepository userRepo;

    public ChatRoomService(ChatRoomRepository roomRepo,
                           ChatMessageRepository msgRepo,
                           UserRepository userRepo) {
        this.roomRepo = roomRepo;
        this.msgRepo  = msgRepo;
        this.userRepo = userRepo;
    }

    /** Create a room between a tenant and a landlord */
    public ChatRoom createRoom(Long tenantId, Long landlordId) {
        User tenant   = userRepo.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found: " + tenantId));
        User landlord = userRepo.findById(landlordId)
                .orElseThrow(() -> new EntityNotFoundException("Landlord not found: " + landlordId));

        ChatRoom room = ChatRoom.builder()
                .tenant(tenant)
                .landlord(landlord)
                .build();
        return roomRepo.save(room);
    }

    /** List all chat rooms */
    public List<ChatRoom> listRooms() {
        return roomRepo.findAll(Sort.by("createdAt"));
    }

    /** Send a message in a room, authored by a real user */
    public ChatMessage saveMessage(Long roomId, Long senderId, ChatMessage msg) {
        ChatRoom room = roomRepo.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found: " + roomId));
        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found: " + senderId));

        msg.setRoom(room);
        msg.setSender(sender);
        return msgRepo.save(msg);
    }

    /** List all messages in a room */
    public List<ChatMessage> listMessages(Long roomId) {
        roomRepo.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found: " + roomId));
        return msgRepo.findByRoomIdOrderByTimestampAsc(roomId);
    }

    /** Update message content (only if it belongs to that room) */
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

    /** Delete a message */
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
