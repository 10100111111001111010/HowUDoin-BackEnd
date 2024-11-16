package edu.sabanciuniv.howudoin.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}
