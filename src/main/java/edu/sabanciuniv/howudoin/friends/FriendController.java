package edu.sabanciuniv.howudoin.friends;

import edu.sabanciuniv.howudoin.users.UserModel;
import edu.sabanciuniv.howudoin.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;
    private final UserService userService;

    /**
     * Send a friend request to another user
     */
    @PostMapping("/request/{receiverId}")
    public ResponseEntity<FriendRequestModel> sendFriendRequest(
            @PathVariable String receiverId,
            @RequestHeader("User-Id") String senderId) {
        try {
            // Verify both users exist
            userService.getUserById(senderId);
            userService.getUserById(receiverId);

            FriendRequestModel request = friendService.sendFriendRequest(senderId, receiverId);
            return new ResponseEntity<>(request, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all pending friend requests for the current user
     */
    @GetMapping("/requests/pending")
    public ResponseEntity<List<FriendRequestModel>> getPendingRequests(
            @RequestHeader("User-Id") String userId) {
        try {
            List<FriendRequestModel> requests = friendService.getPendingRequests(userId);
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all sent friend requests by the current user
     */
    @GetMapping("/requests/sent")
    public ResponseEntity<List<FriendRequestModel>> getSentRequests(
            @RequestHeader("User-Id") String userId) {
        try {
            List<FriendRequestModel> requests = friendService.getSentRequests(userId);
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Accept a friend request
     */
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<FriendRequestModel> acceptFriendRequest(
            @PathVariable String requestId,
            @RequestHeader("User-Id") String userId) {
        try {
            FriendRequestModel request = friendService.acceptFriendRequest(requestId, userId);
            // Add friendship in UserService
            userService.addFriendship(request.getSenderId(), request.getReceiverId());
            return new ResponseEntity<>(request, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Reject a friend request
     */
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<FriendRequestModel> rejectFriendRequest(
            @PathVariable String requestId,
            @RequestHeader("User-Id") String userId) {
        try {
            FriendRequestModel request = friendService.rejectFriendRequest(requestId, userId);
            return new ResponseEntity<>(request, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all friends of the current user
     */
    @GetMapping
    public ResponseEntity<List<UserModel>> getFriends(@RequestHeader("User-Id") String userId) {
        try {
            List<UserModel> friends = userService.getUserFriends(userId);
            return new ResponseEntity<>(friends, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Remove a friend
     */
    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> removeFriend(
            @PathVariable String friendId,
            @RequestHeader("User-Id") String userId) {
        try {
            userService.removeFriendship(userId, friendId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}