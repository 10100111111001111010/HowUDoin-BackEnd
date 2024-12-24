package edu.sabanciuniv.howudoin.friends;

import edu.sabanciuniv.howudoin.security.DTO.ApiResponse;
import edu.sabanciuniv.howudoin.users.UserModel;
import edu.sabanciuniv.howudoin.users.UserService;
import edu.sabanciuniv.howudoin.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController
{
    private final FriendService friendService;
    private final UserService userService;

    /**
     * Send a friend request to another user
     */
    @PostMapping("/add/{receiverId}")
    public ResponseEntity<FriendRequestModel> sendFriendRequest(
            @PathVariable String receiverId,
            Authentication authentication)
    {
        try
        {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String senderId = userDetails.getUserId();

            userService.getUserById(senderId);
            userService.getUserById(receiverId);

            FriendRequestModel request = friendService.sendFriendRequest(senderId, receiverId);
            return new ResponseEntity<>(request, HttpStatus.CREATED);
        }
        catch (RuntimeException exception)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Accept a friend request
     */
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<ApiResponse> acceptFriendRequest(
            @PathVariable String requestId,
            @RequestHeader("User-Id") String userId)
    {
        try
        {
            FriendRequestModel request = friendService.acceptFriendRequest(requestId, userId);
            userService.addFriendship(request.getSenderId(), request.getReceiverId());
            return ResponseEntity.ok(new ApiResponse(true, "Friend request accepted"));
        } catch (RuntimeException exception)
        {
            return ResponseEntity.badRequest().body(new ApiResponse(false, exception.getMessage()));
        }
    }

    /**
     * Get the list of current friends for a user
     */
    @GetMapping
    public ResponseEntity<List<UserModel>> getFriends(@RequestHeader("User-Id") String userId)
    {
        try
        {
            List<UserModel> friends = userService.getUserFriends(userId);
            return new ResponseEntity<>(friends, HttpStatus.OK);
        }
        catch (RuntimeException exception)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all pending friend requests for the current user
     */
    @GetMapping("/requests/pending")
    public ResponseEntity<List<FriendRequestModel>> getPendingRequests(
            @RequestHeader("User-Id") String userId)
    {
        try
        {
            List<FriendRequestModel> requests = friendService.getPendingRequests(userId);
            return new ResponseEntity<>(requests, HttpStatus.OK);
        }
        catch (RuntimeException exception)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<Map<String, String>> getFriendshipStatus(
            @PathVariable String userId,
            Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String currentUserId = userDetails.getUserId();

            boolean areFriends = friendService.areFriends(currentUserId, userId);

            String status = areFriends ? "ACCEPTED" : "NONE";

            Map<String, String> response = new HashMap<>();
            response.put("status", status);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}