package edu.sabanciuniv.howudoin.users;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
