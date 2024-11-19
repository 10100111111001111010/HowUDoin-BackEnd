package edu.sabanciuniv.howudoin.messages;

import edu.sabanciuniv.howudoin.security.DTO.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController
{
    private final MessageService messageService;

    @PostMapping("/send/{receiverId}")
    public ResponseEntity<?> sendMessage(
            @PathVariable String receiverId,
            @RequestBody String content,
            @RequestHeader("User-Id") String senderId)
    {
        try
        {
            MessageModel message = messageService.sendMessage(senderId, receiverId, content);
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        }
        catch (IllegalStateException | IllegalArgumentException exception)
        {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<?> getConversation(
            @PathVariable String userId,
            @RequestHeader("User-Id") String currentUserId,
            Pageable pageable)
    {
        try
        {
            Page<MessageModel> messages = messageService.getConversationHistory(currentUserId, userId, pageable);
            return ResponseEntity.ok(messages);
        }
        catch (IllegalStateException exception)
        {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }
}