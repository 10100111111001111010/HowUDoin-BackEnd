package edu.sabanciuniv.howudoin.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class handling user-related business logic including friend connections.
 * Manages user CRUD operations and friendship relationships.
 */
@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new user.
     * Validates if the email is already taken before saving.
     */
    public UserModel createUser(UserModel userModel) {
        if (userRepository.findByEmail(userModel.getEmail()) != null) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }
        // FriendConnections list is initialized in UserModel constructor
        return userRepository.save(userModel);
    }

    /**
     * Retrieves a user by their ID.
     */
    public UserModel getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    /**
     * Retrieves a user by their email address.
     */
    public UserModel getUserByEmail(String email) {
        UserModel user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }
        return user;
    }

    /**
     * Finds a user by their email verification token.
     */
    public UserModel findByEmailVerificationToken(String token) {
        return userRepository.findByEmailVerificationToken(token);
    }

    /**
     * Updates user information.
     * Maintains existing friend connections while updating other fields.
     */
    public UserModel updateUser(String id, UserModel updatedUser) {
        UserModel existingUser = getUserById(id);

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setEmailVerified(updatedUser.isEmailVerified());

        return userRepository.save(existingUser);
    }

    /**
     * Deletes a user and removes all their friend connections.
     */
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }

        // Remove user from all friend connections before deletion
        UserModel user = getUserById(id);
        if (!user.getFriendConnections().isEmpty()) {
            for (UserModel.FriendConnection friendConnection : user.getFriendConnections()) {
                removeFriendship(id, friendConnection.getFriendId());
            }
        }
        userRepository.deleteById(id);
    }

    /**
     * Retrieves all users with verified email addresses.
     */
    public List<UserModel> getVerifiedUsers() {
        return userRepository.findByEmailVerifiedTrue();
    }

    /**
     * Checks if a user exists with the given email.
     */
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }

    /**
     * Searches for users by first name.
     */
    public List<UserModel> searchByFirstName(String firstName) {
        return userRepository.findByFirstName(firstName);
    }

    /**
     * Searches for users by last name (case-insensitive).
     */
    public List<UserModel> searchByLastName(String lastName) {
        return userRepository.findByLastNameContainingIgnoreCase(lastName);
    }

    /**
     * Gets all accepted friends of a user.
     */
    public List<UserModel> getUserFriends(String userId) {
        UserModel user = getUserById(userId);
        List<String> acceptedFriendIds = user.getFriendConnections().stream()
                .filter(conn -> conn.getStatus() == UserModel.FriendStatus.ACCEPTED)
                .map(UserModel.FriendConnection::getFriendId)
                .toList();

        return userRepository.findByIdIn(acceptedFriendIds);
    }

    /**
     * Creates a bi-directional friendship between two users.
     */
    public void addFriendship(String userId1, String userId2) {
        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("A user cannot be friends with themselves");
        }

        UserModel user1 = getUserById(userId1);
        UserModel user2 = getUserById(userId2);

        // Add friendship connection for both users
        if (!user1.hasFriendConnection(userId2)) {
            user1.addFriendConnection(userId2);
            user1.updateFriendStatus(userId2, UserModel.FriendStatus.ACCEPTED);
            userRepository.save(user1);
        }

        if (!user2.hasFriendConnection(userId1)) {
            user2.addFriendConnection(userId1);
            user2.updateFriendStatus(userId1, UserModel.FriendStatus.ACCEPTED);
            userRepository.save(user2);
        }
    }

    /**
     * Removes a friendship connection between two users.
     */
    public void removeFriendship(String userId1, String userId2) {
        UserModel user1 = getUserById(userId1);
        UserModel user2 = getUserById(userId2);

        // Remove both users' friend connections
        user1.getFriendConnections().removeIf(conn -> conn.getFriendId().equals(userId2));
        user2.getFriendConnections().removeIf(conn -> conn.getFriendId().equals(userId1));

        userRepository.save(user1);
        userRepository.save(user2);
    }

    /**
     * Checks if two users are accepted friends.
     */
    public boolean areFriends(String userId1, String userId2) {
        UserModel user = getUserById(userId1);
        return user.getFriendConnections().stream()
                .anyMatch(conn ->
                        conn.getFriendId().equals(userId2) &&
                                conn.getStatus() == UserModel.FriendStatus.ACCEPTED);
    }

    /**
     * Gets friend suggestions for a user.
     * Returns verified users who are not currently connected.
     */
    public List<UserModel> getFriendSuggestions(String userId) {
        UserModel user = getUserById(userId);
        List<String> connectedIds = user.getFriendConnections().stream()
                .map(UserModel.FriendConnection::getFriendId)
                .toList();

        List<UserModel> suggestions = getVerifiedUsers();
        suggestions.removeIf(u ->
                u.getId().equals(userId) || connectedIds.contains(u.getId()));

        return suggestions;
    }
}