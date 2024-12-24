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
     * Get all friends of a user
     */
    @GetMapping("/{id}/friends")
    public ResponseEntity<List<UserModel>> getUserFriends(@PathVariable String id)
    {
        try
        {
            List<UserModel> friends = userService.getUserFriends(id);
            return new ResponseEntity<>(friends, HttpStatus.OK);
        }
        catch (IllegalArgumentException exception)
        {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Add a friend relationship
     */
    @PostMapping("/{id1}/friends/{id2}")
    public ResponseEntity<Void> addFriend(@PathVariable String id1, @PathVariable String id2)
    {
        try
        {
            userService.addFriendship(id1, id2);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (IllegalArgumentException exception)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Remove a friend relationship
     */
    @DeleteMapping("/{id1}/friends/{id2}")
    public ResponseEntity<Void> removeFriend(@PathVariable String id1, @PathVariable String id2)
    {
        try
        {
            userService.removeFriendship(id1, id2);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (IllegalArgumentException exception)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get friend suggestions for a user
     */
    @GetMapping("/{id}/friend-suggestions")
    public ResponseEntity<List<UserModel>> getFriendSuggestions(@PathVariable String id)
    {
        try
        {
            List<UserModel> suggestions = userService.getFriendSuggestions(id);
            return new ResponseEntity<>(suggestions, HttpStatus.OK);
        }
        catch (IllegalArgumentException exception)
        {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Check if two users are friends
     */
    @GetMapping("/{id1}/friends/{id2}/status")
    public ResponseEntity<Boolean> checkFriendship(@PathVariable String id1, @PathVariable String id2)
    {
        try
        {
            boolean areFriends = userService.areFriends(id1, id2);
            return new ResponseEntity<>(areFriends, HttpStatus.OK);
        }
        catch (IllegalArgumentException exception)
        {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    /**
     * Fetches all users from the database.
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserModel>> getAllUsers() {
        try {
            List<UserModel> users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}