package edu.sabanciuniv.howudoin.users;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the system with embedded friend connections.
 * This model consolidates user profile information and friendship management
 * to maintain data consistency and prevent race conditions.
 */
@Document(collection = "users")
@Data
public class UserModel {
    @Id
    private String id;

    // User authentication and verification fields
    private String email;
    private String emailVerificationToken;
    private boolean emailVerified;

    // Password is excluded from toString for security
    @ToString.Exclude
    private String password;

    // Basic user profile information
    private String firstName;
    private String lastName;

    // List of friend connections with their current status
    @Field("friend_connections")
    private List<FriendConnection> friendConnections = new ArrayList<>();

    /**
     * Represents a connection between two users and tracks its status.
     * This embedded document allows atomic updates to friendship status
     * and maintains a complete history of the connection.
     */
    @Data
    public static class FriendConnection {
        // The ID of the connected user
        private String friendId;

        // Current status of the connection
        private FriendStatus status;

        // Timestamp when the connection was first created
        private LocalDateTime createdAt;

        // Timestamp of the last status update
        private LocalDateTime updatedAt;

        /**
         * Creates a new friend connection with PENDING status.
         */
        public FriendConnection(String friendId) {
            this.friendId = friendId;
            this.status = FriendStatus.PENDING;
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Defines the possible states of a friend connection.
     */
    public enum FriendStatus {
        PENDING,    // Initial state when request is sent
        ACCEPTED,   // Both users have accepted the friendship
        REJECTED,   // Request was rejected
        BLOCKED     // One user has blocked the other
    }

    /**
     * Adds a new friend connection with PENDING status.
     */
    public void addFriendConnection(String friendId) {
        if (friendConnections == null) {
            friendConnections = new ArrayList<>();
        }
        friendConnections.add(new FriendConnection(friendId));
    }

    /**
     * Updates the status of an existing friend connection.
     */
    public void updateFriendStatus(String friendId, FriendStatus newStatus) {
        friendConnections.stream()
                .filter(friendConnection -> friendConnection.getFriendId().equals(friendId))
                .findFirst()
                .ifPresent(friendConnection -> {
                    friendConnection.setStatus(newStatus);
                    friendConnection.setUpdatedAt(LocalDateTime.now());
                });
    }

    /**
     * Checks if a connection exists with the specified user.
     */
    public boolean hasFriendConnection(String friendId) {
        return friendConnections.stream()
                .anyMatch(friendConnection -> friendConnection.getFriendId().equals(friendId));
    }

    /**
     * Gets the current status of a friend connection.
     */
    public FriendStatus getFriendStatus(String friendId) {
        return friendConnections.stream()
                .filter(friendConnection -> friendConnection.getFriendId().equals(friendId))
                .map(FriendConnection::getStatus)
                .findFirst()
                .orElse(null);
    }
}