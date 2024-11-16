package edu.sabanciuniv.howudoin.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController
{
    private final UserService userService;

    @Autowired
    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    /**
     * Creates a new user.
     */
    @PostMapping
    public ResponseEntity<UserModel> createUser(@RequestBody UserModel userModel)
    {
        try
        {
            UserModel createdUser = userService.createUser(userModel);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        }
        catch (IllegalArgumentException exception)
        {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * User getter by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserModel> getUserById(@PathVariable String id)
    {
        try
        {
            UserModel user = userService.getUserById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        catch (IllegalArgumentException exception)
        {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * User getter by email.
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserModel> getUserByEmail(@PathVariable String email)
    {
        try
        {
            UserModel user = userService.getUserByEmail(email);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        catch (IllegalArgumentException exception)
        {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Update a user's information.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserModel> updateUser(@PathVariable String id, @RequestBody UserModel updatedUser)
    {
        try
        {
            UserModel user = userService.updateUser(id, updatedUser);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        catch (IllegalArgumentException exception)
        {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Delete a user by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id)
    {
        try
        {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        catch (IllegalArgumentException exception)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all users with a verified email.
     */
    @GetMapping("/verified")
    public ResponseEntity<List<UserModel>> getVerifiedUsers()
    {
        List<UserModel> verifiedUsers = userService.getVerifiedUsers();
        return new ResponseEntity<>(verifiedUsers, HttpStatus.OK);
    }

    /**
     * Search for users by first name.
     */
    @GetMapping("/search/first-name")
    public ResponseEntity<List<UserModel>> searchByFirstName(@RequestParam String firstName)
    {
        List<UserModel> users = userService.searchByFirstName(firstName);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Search for users by last name (case-insensitive).
     */
    @GetMapping("/search/last-name")
    public ResponseEntity<List<UserModel>> searchByLastName(@RequestParam String lastName)
    {
        List<UserModel> users = userService.searchByLastName(lastName);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
