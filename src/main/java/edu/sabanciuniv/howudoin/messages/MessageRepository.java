package edu.sabanciuniv.howudoin.messages;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<MessageModel, String> {

    // Find messages between two users (conversation history)
    @Query("{ $or: [ " +
            "{ 'senderId': ?0, 'receiverId': ?1 }, " +
            "{ 'senderId': ?1, 'receiverId': ?0 } " +
            "] }")
    Page<MessageModel> findMessagesBetweenUsers(String user1Id, String user2Id, Pageable pageable);

    // Find unread messages for a user
    @Query("{ 'receiverId': ?0, 'status': { $in: ['SENT', 'DELIVERED'] } }")
    List<MessageModel> findUnreadMessagesForUser(String userId);

    // Find messages sent by a user
    Page<MessageModel> findBySenderIdOrderByCreatedAtDesc(String senderId, Pageable pageable);

    // Find messages received by a user
    Page<MessageModel> findByReceiverIdOrderByCreatedAtDesc(String receiverId, Pageable pageable);

    // Find messages after a certain date
    List<MessageModel> findByCreatedAtAfter(LocalDateTime date);

    // Find latest message between two users
    @Query(value = "{ $or: [ " +
            "{ 'senderId': ?0, 'receiverId': ?1 }, " +
            "{ 'senderId': ?1, 'receiverId': ?0 } " +
            "] }",
            sort = "{ 'createdAt': -1 }")
    MessageModel findLatestMessageBetweenUsers(String user1Id, String user2Id);

    // Find all messages for a user (either as sender or receiver)
    @Query("{ $or: [ " +
            "{ 'senderId': ?0 }, " +
            "{ 'receiverId': ?0 } " +
            "] }")
    Page<MessageModel> findAllUserMessages(String userId, Pageable pageable);

    // Count unread messages for a user
    long countByReceiverIdAndStatusIn(String receiverId, List<MessageModel.MessageStatus> statuses);

    // Delete all messages between two users
    @Query(value = "{ $or: [ " +
            "{ 'senderId': ?0, 'receiverId': ?1 }, " +
            "{ 'senderId': ?1, 'receiverId': ?0 } " +
            "] }")
    void deleteMessagesBetweenUsers(String user1Id, String user2Id);
}