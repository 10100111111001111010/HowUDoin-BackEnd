package edu.sabanciuniv.howudoin.friends;

import edu.sabanciuniv.howudoin.security.DTO.ApiResponse;
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
    @PostMapping("/add/{receiverId}")
    public ResponseEntity<FriendRequestModel> sendFriendRequest(
            @PathVariable String receiverId,
            @RequestHeader("User-Id") String senderId) {
        try {
            userService.getUserById(senderId);
            userService.getUserById(receiverId);
            FriendRequestModel request = friendService.sendFriendRequest(senderId, receiverId);
            return new ResponseEntity<>(request, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Accept a friend request
     */
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<ApiResponse> acceptFriendRequest(
            @PathVariable String requestId,
            @RequestHeader("User-Id") String userId) {
        try {
            FriendRequestModel request = friendService.acceptFriendRequest(requestId, userId);
            userService.addFriendship(request.getSenderId(), request.getReceiverId());
            return ResponseEntity.ok(new ApiResponse(true, "Friend request accepted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * Get the list of current friends for a user
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
}