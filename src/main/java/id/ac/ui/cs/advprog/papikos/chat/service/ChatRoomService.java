package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatMessageRepository;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class ChatRoomService {

    private final ChatRoomRepository roomRepo;
    private final ChatMessageRepository msgRepo;
    private final UserRepository userRepo;

    // --- simple in‐memory caches for room & user lookups ---
    private final ConcurrentMap<Long, ChatRoom> roomCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<String,    User>     userCache = new ConcurrentHashMap<>();

    public ChatRoomService(ChatRoomRepository roomRepo,
                           ChatMessageRepository msgRepo,
                           UserRepository userRepo) {
        this.roomRepo = roomRepo;
        this.msgRepo  = msgRepo;
        this.userRepo = userRepo;
    }

    /* ───────────────── messages ─────────────────────────────────────── */

    /**
     * Save a message into the given room by the given sender.
     * Returns a completed future (no @Async thread‐hop).
     */
    public CompletableFuture<ChatMessage> saveMessage(Long roomId,
                                                      String senderEmail,
                                                      ChatMessage msg) {
        // fetch (or cache) the ChatRoom
        ChatRoom room = roomCache.computeIfAbsent(roomId, id ->
                roomRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Room not found"))
        );

        // fetch (or cache) the User
        User sender = userCache.computeIfAbsent(senderEmail, email ->
                userRepo.findByEmail(email)
                        .orElseThrow(() -> new EntityNotFoundException("Sender not found"))
        );

        msg.setRoom(room);
        msg.setSender(sender);
        ChatMessage saved = msgRepo.save(msg);
        return CompletableFuture.completedFuture(saved);
    }

    /* ───────────────── rooms (create / find / list) ─────────────────── */

    /** Pure create (used internally). */
    public ChatRoom createRoom(Long tenantId, Long landlordId) {
        User tenant   = userRepo.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found"));
        User landlord = userRepo.findById(landlordId)
                .orElseThrow(() -> new EntityNotFoundException("Landlord not found"));

        String label = "Tenant " + tenant.getId() + " ↔ Landlord " + landlord.getId();
        ChatRoom room = ChatRoom.builder()
                .name(label)
                .tenant(tenant)
                .landlord(landlord)
                .build();

        return roomRepo.save(room);
    }

    /** Return existing room between the two users, if any. */
    public Optional<ChatRoom> findRoom(Long tenantId, Long landlordId) {
        return roomRepo.findByTenant_IdAndLandlord_Id(tenantId, landlordId);
    }

    /** Idempotent helper used by tests or other callers. */
    public ChatRoom getOrCreateRoom(Long tenantId, Long landlordId) {
        return findRoom(tenantId, landlordId)
                .orElseGet(() -> createRoom(tenantId, landlordId));
    }

    /** List all rooms (sorted by creation time). */
    public List<ChatRoom> listRooms() {
        return roomRepo.findAll(Sort.by("createdAt"));
    }

    /** List all rooms for a given user (tenant or landlord). */
    public List<ChatRoom> listRoomsForUser(Long userId) {
        return roomRepo.findByTenant_IdOrLandlord_Id(
                userId, userId, Sort.by("createdAt")
        );
    }

    /* ───────────────── messages (history / update / delete) ─────────── */

    public List<ChatMessage> listMessages(Long roomId) {
        roomRepo.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));
        return msgRepo.findByRoomIdOrderByTimestampAsc(roomId);
    }

    public ChatMessage updateMessage(Long roomId, Long msgId, ChatMessage updated) {
        ChatMessage existing = msgRepo.findById(msgId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));

        if (!existing.getRoom().getId().equals(roomId)) {
            throw new EntityNotFoundException("Message not in this room");
        }
        existing.setContent(updated.getContent());
        return msgRepo.save(existing);
    }

    public void deleteMessage(Long roomId, Long msgId) {
        ChatMessage existing = msgRepo.findById(msgId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));

        if (!existing.getRoom().getId().equals(roomId)) {
            throw new EntityNotFoundException("Message not in this room");
        }
        msgRepo.delete(existing);
    }
}
