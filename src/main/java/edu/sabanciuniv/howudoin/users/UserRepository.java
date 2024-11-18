package edu.sabanciuniv.howudoin.users;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserModel, String>
{
    /**
     * Find a user by their email address.
     * Useful for user registration
     */
    UserModel findByEmail(String email);

    /**
     * Find a user by their email verification token.
     * Solves the disabled user problem while logging in
     */
    UserModel findByEmailVerificationToken(String token);

    /**
     * Find all users with a verified email.
     */
    List<UserModel> findByEmailVerifiedTrue();

    /**
     * Find all users with a specific first name.
     * Useful for friend requests
     */
    List<UserModel> findByFirstName(String firstName);

    /**
     * Find all users whose last name contains a specific substring (case-insensitive).
     * Useful for friend requests
     */
    List<UserModel> findByLastNameContainingIgnoreCase(String lastName);

    // Friend-related queries

    /**
     * Find multiple users by their IDs.
     * Efficient way to fetch multiple friends at once
     */
    List<UserModel> findByIdIn(List<String> userIds);

    /**
     * Find all users who are friends with the given userId.
     * Used to retrieve a user's friend list
     */
    @Query("{ 'friend_ids': ?0 }")
    List<UserModel> findByFriendIdsContaining(String userId);

    /**
     * Find all users who are not friends with the given userId.
     * Useful for friend suggestions
     */
    @Query("{ 'friend_ids': { $ne: ?0 } }")
    List<UserModel> findByFriendIdsNotContaining(String userId);

    /**
     * Find mutual friends between two users.
     */
    @Query("{ 'friend_ids': { $all: [?0, ?1] } }")
    List<UserModel> findMutualFriends(String userId1, String userId2);

    /**
     * Count the number of friends for a user.
     */
    @Query(value = "{ '_id': ?0 }", count = true)
    long countFriendIdsById(String userId);
}