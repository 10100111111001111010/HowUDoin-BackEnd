package edu.sabanciuniv.howudoin.users;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<UserModel, String>
{

    /**
     * Find a user by their email address.
     *
     * @param email The email of the user.
     * @return The UserModel if found, otherwise null.
     */
    UserModel findByEmail(String email);

    /**
     * Find all users with a verified email.
     *
     * @return A list of users whose emailVerified field is true.
     */
    List<UserModel> findByEmailVerifiedTrue();

    /**
     * Find all users with a specific first name.
     *
     * @param firstName The first name to search for.
     * @return A list of users with the given first name.
     */
    List<UserModel> findByFirstName(String firstName);

    /**
     * Find all users whose last name contains a specific substring (case-insensitive).
     *
     * @param lastName The substring to search for in the last name.
     * @return A list of users with matching last names.
     */
    List<UserModel> findByLastNameContainingIgnoreCase(String lastName);
}
