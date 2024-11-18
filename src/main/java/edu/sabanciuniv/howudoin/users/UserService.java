package edu.sabanciuniv.howudoin.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService
{
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new user.
     * Validates if the email is taken before saving.
     */
    public UserModel createUser(UserModel userModel)
    {
        if (userRepository.findByEmail(userModel.getEmail()) != null)
        {
            throw new IllegalArgumentException("A user with this email already exists.");
        }
        userModel.setFriendIds(new ArrayList<>()); // Initialize empty friends list
        return userRepository.save(userModel);
    }

    /**
     * User getter by ID.
     */
    public UserModel getUserById(String id)
    {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    /**
     * User getter by email.
     */
    public UserModel getUserByEmail(String email)
    {
        UserModel user = userRepository.findByEmail(email);
        if (user == null)
        {
            throw new IllegalArgumentException("User not found with email: " + email);
        }
        return user;
    }

    public UserModel findByEmailVerificationToken(String token)
    {
        return userRepository.findByEmailVerificationToken(token);
    }

    /**
     * Update a user's information.
     */
    public UserModel updateUser(String id, UserModel updatedUser)
    {
        UserModel existingUser = getUserById(id);

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setEmailVerified(updatedUser.isEmailVerified());

        return userRepository.save(existingUser);
    }

    /**
     * Delete a user by ID.
     */
    public void deleteUser(String id)
    {
        if (!userRepository.existsById(id))
        {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }
        // Remove user from all friends lists before deletion
        UserModel user = getUserById(id);
        if (user.getFriendIds() != null && !user.getFriendIds().isEmpty())
        {
            for (String friendId : user.getFriendIds())
            {
                removeFriendship(id, friendId);
            }
        }
        userRepository.deleteById(id);
    }

    /**
     * Retrieve all users with a verified email.
     */
    public List<UserModel> getVerifiedUsers()
    {
        return userRepository.findByEmailVerifiedTrue();
    }

    /**
     * Checks if the email already exists.
     */
    public boolean existsByEmail(String email)
    {
        return userRepository.findByEmail(email) != null;
    }

    /**
     * Search for users by first name.
     */
    public List<UserModel> searchByFirstName(String firstName)
    {
        return userRepository.findByFirstName(firstName);
    }

    /**
     * Search for users by last name (case-insensitive).
     */
    public List<UserModel> searchByLastName(String lastName)
    {
        return userRepository.findByLastNameContainingIgnoreCase(lastName);
    }

    // Friend-related methods

    /**
     * Get all friends of a user.
     * @param userId The ID of the user
     * @return List of users who are friends with the specified user
     * @throws IllegalArgumentException if user not found
     */
    public List<UserModel> getUserFriends(String userId)
    {
        UserModel user = getUserById(userId);
        if (user.getFriendIds() == null || user.getFriendIds().isEmpty())
        {
            return new ArrayList<>();
        }
        return userRepository.findByIdIn(user.getFriendIds());
    }

    /**
     * Add a friend relationship between two users.
     * @param userId1 First user's ID
     * @param userId2 Second user's ID
     * @throws IllegalArgumentException if either user not found
     */
    public void addFriendship(String userId1, String userId2)
    {
        if (userId1.equals(userId2))
        {
            throw new IllegalArgumentException("A user cannot be friends with themselves");
        }

        UserModel user1 = getUserById(userId1);
        UserModel user2 = getUserById(userId2);

        // Initialize friend lists if null
        if (user1.getFriendIds() == null)
        {
            user1.setFriendIds(new ArrayList<>());
        }
        if (user2.getFriendIds() == null)
        {
            user2.setFriendIds(new ArrayList<>());
        }

        // Add the friendship in both directions if not already friends
        if (!user1.getFriendIds().contains(userId2))
        {
            user1.getFriendIds().add(userId2);
            userRepository.save(user1);
        }
        if (!user2.getFriendIds().contains(userId1))
        {
            user2.getFriendIds().add(userId1);
            userRepository.save(user2);
        }
    }

    /**
     * Remove a friend relationship between two users.
     * @param userId1 First user's ID
     * @param userId2 Second user's ID
     * @throws IllegalArgumentException if either user not found
     */
    public void removeFriendship(String userId1, String userId2)
    {
        UserModel user1 = getUserById(userId1);
        UserModel user2 = getUserById(userId2);

        // Remove the friendship in both directions
        if (user1.getFriendIds() != null)
        {
            user1.getFriendIds().remove(userId2);
            userRepository.save(user1);
        }
        if (user2.getFriendIds() != null)
        {
            user2.getFriendIds().remove(userId1);
            userRepository.save(user2);
        }
    }

    /**
     * Check if two users are friends.
     * @param userId1 First user's ID
     * @param userId2 Second user's ID
     * @return true if users are friends, false otherwise
     * @throws IllegalArgumentException if either user not found
     */
    public boolean areFriends(String userId1, String userId2)
    {
        UserModel user = getUserById(userId1);
        return user.getFriendIds() != null && user.getFriendIds().contains(userId2);
    }

    /**
     * Get friend suggestions for a user.
     * Returns verified users who are not currently friends with the specified user.
     * @param userId The ID of the user
     * @return List of suggested users
     * @throws IllegalArgumentException if user not found
     */
    public List<UserModel> getFriendSuggestions(String userId)
    {
        UserModel user = getUserById(userId);
        List<UserModel> suggestions = getVerifiedUsers();
        suggestions.removeIf(u ->
                u.getId().equals(userId) ||
                        (user.getFriendIds() != null && user.getFriendIds().contains(u.getId()))
        );
        return suggestions;
    }
}