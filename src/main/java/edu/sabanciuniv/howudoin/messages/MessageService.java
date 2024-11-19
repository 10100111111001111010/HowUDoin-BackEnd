package edu.sabanciuniv.howudoin.messages;

import edu.sabanciuniv.howudoin.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService
{
    private final MessageRepository messageRepository;
    private final UserService userService;

    /**
     * Send a message from one user to another.
     * Validates that users are friends before allowing message sending.
     */
    @Transactional
    public MessageModel sendMessage(String senderId, String receiverId, String content)
    {
        // Validate users exist and are friends
        if (!userService.areFriends(senderId, receiverId))
        {
            throw new IllegalStateException("Whoops! Looks like you need to be friends with this user to message them.");
        }

        MessageModel message = new MessageModel();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setStatus(MessageModel.MessageStatus.SENT);
        message.prePersist();

        if (!message.isValid())
        {
            throw new IllegalArgumentException("Looks like you’re trying to send a message to yourself—try someone else!");
        }
        return messageRepository.save(message);
    }

    /**
     * Retrieve conversation history between two users.
     * Messages are paginated for performance.
     */
    public Page<MessageModel> getConversationHistory(String user1Id, String user2Id, Pageable pageable)
    {
        // Verify users exist and are friends
        if (!userService.areFriends(user1Id, user2Id))
        {
            throw new IllegalStateException("It looks like you don’t have a conversation history yet because you’re not friends!");
        }
        return messageRepository.findMessagesBetweenUsers(user1Id, user2Id, pageable);
    }

    /**
     * Mark a message as delivered when it reaches the recipient's device.
     */
    @Transactional
    public void markMessageAsDelivered(String messageId, String receiverId)
    {
        MessageModel message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        if (!message.getReceiverId().equals(receiverId))
        {
            throw new IllegalStateException("User not authorized to mark this message as delivered");
        }

        if (message.getStatus() == MessageModel.MessageStatus.SENT)
        {
            message.setStatus(MessageModel.MessageStatus.DELIVERED);
            message.setUpdatedAt(LocalDateTime.now());
            messageRepository.save(message);
        }
    }

    /**
     * Mark a message as read when the recipient opens it.
     */
    @Transactional
    public void markMessageAsRead(String messageId, String receiverId)
    {
        MessageModel message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        if (!message.getReceiverId().equals(receiverId))
        {
            throw new IllegalStateException("User not authorized to mark this message as read");
        }

        if (message.getStatus() != MessageModel.MessageStatus.READ)
        {
            message.setStatus(MessageModel.MessageStatus.READ);
            message.setUpdatedAt(LocalDateTime.now());
            messageRepository.save(message);
        }
    }

    /**
     * Get all unread messages for a user
     */
    public List<MessageModel> getUnreadMessages(String userId)
    {
        return messageRepository.findUnreadMessagesForUser(userId);
    }

    /**
     * Delete all messages between two users
     */
    @Transactional
    public void deleteConversation(String user1Id, String user2Id)
    {
        // Verify users exist and are friends
        if (!userService.areFriends(user1Id, user2Id)) {
            throw new IllegalStateException("There’s no conversation to delete because you’re not friends yet!");
        }
        messageRepository.deleteMessagesBetweenUsers(user1Id, user2Id);
    }

    /**
     * Get messages sent by a user
     */
    public Page<MessageModel> getSentMessages(String userId, Pageable pageable)
    {
        return messageRepository.findBySenderIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Get messages received by a user
     */
    public Page<MessageModel> getReceivedMessages(String userId, Pageable pageable)
    {
        return messageRepository.findByReceiverIdOrderByCreatedAtDesc(userId, pageable);
    }

}