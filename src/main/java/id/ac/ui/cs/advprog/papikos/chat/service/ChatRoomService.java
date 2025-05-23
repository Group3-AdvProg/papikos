// src/main/java/id/ac/ui/cs/advprog/papikos/chat/service/ChatRoomService.java
package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatMessageRepository;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    /* ---------- save a message (lookup sender by e-mail) ---------- */
    @Async
    public CompletableFuture<ChatMessage> saveMessage(Long roomId,
                                                      String senderEmail,
                                                      ChatMessage msg) {

        ChatRoom room = roomRepo.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room " + roomId + " not found"));

        User sender = userRepo.findByEmail(senderEmail)
                .orElseThrow(() -> new EntityNotFoundException("Sender " + senderEmail + " not found"));

        msg.setRoom(room);
        msg.setSender(sender);
        return CompletableFuture.completedFuture(msgRepo.save(msg));
    }

    /* ---------- create a room (auto-generate name) ---------- */
    public ChatRoom createRoom(Long tenantId, Long landlordId) {

        User tenant = userRepo.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant " + tenantId + " not found"));

        User landlord = userRepo.findById(landlordId)
                .orElseThrow(() -> new EntityNotFoundException("Landlord " + landlordId + " not found"));

        String label = "Tenant " + tenant.getId() + " ↔ Landlord " + landlord.getId();

        ChatRoom room = ChatRoom.builder()
                .name(label)            // <— satisfies NOT-NULL column "name"
                .tenant(tenant)
                .landlord(landlord)
                .build();

        return roomRepo.save(room);
    }

    /* ---------- list rooms ---------- */
    public List<ChatRoom> listRooms() {
        return roomRepo.findAll(Sort.by("createdAt"));
    }

    /* ---------- list messages in a room ---------- */
    public List<ChatMessage> listMessages(Long roomId) {
        roomRepo.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room " + roomId + " not found"));
        return msgRepo.findByRoomIdOrderByTimestampAsc(roomId);
    }

    /* ---------- update a message ---------- */
    public ChatMessage updateMessage(Long roomId, Long msgId, ChatMessage updated) {
        ChatMessage existing = msgRepo.findById(msgId)
                .orElseThrow(() -> new EntityNotFoundException("Message " + msgId + " not found"));
        if (!existing.getRoom().getId().equals(roomId))
            throw new EntityNotFoundException("Message " + msgId + " not in room " + roomId);

        existing.setContent(updated.getContent());
        return msgRepo.save(existing);
    }

    /* ---------- delete a message ---------- */
    public void deleteMessage(Long roomId, Long msgId) {
        ChatMessage existing = msgRepo.findById(msgId)
                .orElseThrow(() -> new EntityNotFoundException("Message " + msgId + " not found"));
        if (!existing.getRoom().getId().equals(roomId))
            throw new EntityNotFoundException("Message " + msgId + " not in room " + roomId);

        msgRepo.delete(existing);
    }
}
