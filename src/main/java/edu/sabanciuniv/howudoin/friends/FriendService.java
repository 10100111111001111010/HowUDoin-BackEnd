package edu.sabanciuniv.howudoin.friends;

import edu.sabanciuniv.howudoin.users.UserModel;
import edu.sabanciuniv.howudoin.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    /**
     * Send a friend request
     * Checks for existing requests in both directions and verifies users aren't already friends
     * @throws RuntimeException if request already exists or users are already friends
     */
    public FriendRequestModel sendFriendRequest(String senderId, String receiverId) {
        // Check if a request already exists in either direction
        Optional<FriendRequestModel> existingRequest = friendRepository.findBySenderIdAndReceiverId(senderId, receiverId);
        Optional<FriendRequestModel> reverseRequest = friendRepository.findBySenderIdAndReceiverId(receiverId, senderId);

        if (existingRequest.isPresent()) {
            throw new RuntimeException("Friend request already exists");
        }

        if (reverseRequest.isPresent()) {
            throw new RuntimeException("You already have a pending request from this user");
        }

        // Verify users aren't already friends
        Optional<UserModel> sender = userRepository.findById(senderId);
        if (!sender.isPresent())
        {
            throw new RuntimeException("Sender user not found");
        }
        if (sender.get().getFriendIds().contains(receiverId)) {
            throw new RuntimeException("Users are already friends");
        }

        // Create new friend request
        FriendRequestModel newRequest = FriendRequestModel.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .status(FriendRequestModel.RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return friendRepository.save(newRequest);
    }

    /**
     * Get all pending requests for a user
     */
    public List<FriendRequestModel> getPendingRequests(String userId) {
        return friendRepository.findByReceiverIdAndStatus(
                userId,
                FriendRequestModel.RequestStatus.PENDING
        );
    }

    /**
     * Get all sent requests by a user
     */
    public List<FriendRequestModel> getSentRequests(String userId) {
        return friendRepository.findBySenderIdAndStatus(
                userId,
                FriendRequestModel.RequestStatus.PENDING
        );
    }

    /**
     * Accept friend request
     * @throws RuntimeException if request not found, not authorized, or not pending
     */
    public FriendRequestModel acceptFriendRequest(String requestId, String receiverId) {
        FriendRequestModel request = friendRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        if (!request.getReceiverId().equals(receiverId)) {
            throw new RuntimeException("Not authorized to accept this request");
        }

        if (!request.isPending()) {
            throw new RuntimeException("Request is no longer pending");
        }

        request.setStatus(FriendRequestModel.RequestStatus.ACCEPTED);
        request.setUpdatedAt(LocalDateTime.now());
        return friendRepository.save(request);
    }

    /**
     * Reject friend request
     * @throws RuntimeException if request not found, not authorized, or not pending
     */
    public FriendRequestModel rejectFriendRequest(String requestId, String receiverId) {
        FriendRequestModel request = friendRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        if (!request.getReceiverId().equals(receiverId)) {
            throw new RuntimeException("Not authorized to reject this request");
        }

        if (!request.isPending()) {
            throw new RuntimeException("Request is no longer pending");
        }

        request.setStatus(FriendRequestModel.RequestStatus.REJECTED);
        request.setUpdatedAt(LocalDateTime.now());
        return friendRepository.save(request);
    }

    /**
     * Get accepted friends list
     * Returns list of accepted friend requests
     */
    public List<FriendRequestModel> getAcceptedFriends(String userId) {
        return friendRepository.findBySenderIdAndStatus(
                userId,
                FriendRequestModel.RequestStatus.ACCEPTED
        );
    }

    /**
     * Get all friend requests with a specific status
     */
    public List<FriendRequestModel> getRequestsByStatus(String userId, FriendRequestModel.RequestStatus status) {
        return friendRepository.findBySenderIdAndStatus(userId, status);
    }

    /**
     * Cancel a sent friend request
     * Only the sender can cancel their pending request
     * @throws RuntimeException if request not found or not authorized
     */
    public void cancelFriendRequest(String requestId, String senderId) {
        FriendRequestModel request = friendRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        if (!request.getSenderId().equals(senderId)) {
            throw new RuntimeException("Not authorized to cancel this request");
        }

        if (!request.isPending()) {
            throw new RuntimeException("Request is no longer pending");
        }

        friendRepository.delete(request);
    }

    /**
     * Block a user
     * Changes the status of any existing request to BLOCKED or creates a new BLOCKED request
     */
    public FriendRequestModel blockUser(String userId, String blockedUserId) {
        // Check for existing requests in either direction
        Optional<FriendRequestModel> existingRequest = friendRepository.findBySenderIdAndReceiverId(userId, blockedUserId);
        Optional<FriendRequestModel> reverseRequest = friendRepository.findBySenderIdAndReceiverId(blockedUserId, userId);

        FriendRequestModel request;
        if (existingRequest.isPresent()) {
            request = existingRequest.get();
        } else if (reverseRequest.isPresent()) {
            request = reverseRequest.get();
        } else {
            request = FriendRequestModel.builder()
                    .senderId(userId)
                    .receiverId(blockedUserId)
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        request.setStatus(FriendRequestModel.RequestStatus.BLOCKED);
        request.setUpdatedAt(LocalDateTime.now());
        return friendRepository.save(request);
    }

    /**
     * Check if a user is blocked
     */
    public boolean isBlocked(String userId, String otherUserId) {
        Optional<FriendRequestModel> request = friendRepository.findBySenderIdAndReceiverId(userId, otherUserId);
        Optional<FriendRequestModel> reverseRequest = friendRepository.findBySenderIdAndReceiverId(otherUserId, userId);

        return (request.isPresent() && request.get().getStatus() == FriendRequestModel.RequestStatus.BLOCKED) ||
                (reverseRequest.isPresent() && reverseRequest.get().getStatus() == FriendRequestModel.RequestStatus.BLOCKED);
    }

    /**
     * Get blocked users
     */
    public List<FriendRequestModel> getBlockedUsers(String userId) {
        return friendRepository.findBySenderIdAndStatus(userId, FriendRequestModel.RequestStatus.BLOCKED);
    }
}