package edu.sabanciuniv.howudoin.messages;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<MessageModel, String>
{

    // Find messages between two users (conversation history)
    @Query("{ $or: [ " +
            "{ 'sender_id': ?0, 'receiver_id': ?1 }, " +
            "{ 'sender_id': ?1, 'receiver_id': ?0 } " +
            "] }")
    Page<MessageModel> findMessagesBetweenUsers(String user1Id, String user2Id, Pageable pageable);

    // Find unread messages for a user
    @Query("{ 'receiver_id': ?0, 'status': { $in: ['SENT', 'DELIVERED'] } }")
    List<MessageModel> findUnreadMessagesForUser(String userId);

    // Find messages sent by a user
    Page<MessageModel> findBySenderIdOrderByCreatedAtDesc(String senderId, Pageable pageable);

    // Find messages received by a user
    Page<MessageModel> findByReceiverIdOrderByCreatedAtDesc(String receiverId, Pageable pageable);

    // Find messages after a certain date
    List<MessageModel> findByCreatedAtAfter(LocalDateTime date);

    // Find latest message between two users
    @Query(value = "{ $or: [ " +
            "{ 'sender_id': ?0, 'receiver_id': ?1 }, " +
            "{ 'sender_id': ?1, 'receiver_id': ?0 } " +
            "] }",
            sort = "{ 'created_at': -1 }")
    MessageModel findLatestMessageBetweenUsers(String user1Id, String user2Id);

    // Count unread messages for a user
    long countByReceiverIdAndStatusIn(String receiverId, List<MessageModel.MessageStatus> statuses);

    // Find all messages by status for a receiver
    List<MessageModel> findByReceiverIdAndStatus(String receiverId, MessageModel.MessageStatus status);

    // Update message status
    @Query("{ '_id': ?0 }")
    void updateMessageStatus(String messageId, MessageModel.MessageStatus newStatus);

    // Delete all messages between two users
    @Query(value = "{ $or: [ " +
            "{ 'sender_id': ?0, 'receiver_id': ?1 }, " +
            "{ 'sender_id': ?1, 'receiver_id': ?0 } " +
            "] }")
    void deleteMessagesBetweenUsers(String user1Id, String user2Id);
}