package edu.sabanciuniv.howudoin.messages;

import edu.sabanciuniv.howudoin.security.DTO.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/send/{receiverId}")
    public ResponseEntity<?> sendMessage(
            @PathVariable String receiverId,
            @RequestBody MessageModel messageModel,
            @RequestHeader("User-Id") String senderId) {
        try {
            MessageModel message = messageService.sendMessage(senderId, receiverId, messageModel.getContent());
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch (IllegalStateException | IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<?> getConversation(
            @PathVariable String userId,
            @RequestHeader("User-Id") String currentUserId,
            Pageable pageable) {
        try {
            Page<MessageModel> messages = messageService.getConversationHistory(currentUserId, userId, pageable);
            return ResponseEntity.ok(messages);
        } catch (IllegalStateException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllConversations(
            @RequestHeader("User-Id") String userId,
            Pageable pageable) {
        try {
            System.out.println("Received request for all messages. User ID: " + userId);
            System.out.println("Pageable settings: " + pageable);

            List<MessageModel> messages = messageService.getAllUserConversations(userId, pageable);

            System.out.println("Returning " + (messages != null ? messages.size() : 0) + " messages");

            if (messages == null || messages.isEmpty()) {
                System.out.println("No messages found for user: " + userId);
            }

            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            System.err.println("Error processing request: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }
}