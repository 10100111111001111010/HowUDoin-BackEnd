package edu.sabanciuniv.howudoin.friends;

import edu.sabanciuniv.howudoin.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService
{
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    /**
     * Sends a friend request from one user to another
     */
    public FriendRequestModel sendFriendRequest(String senderId, String receiverId)
    {
        // Check if users exist
        if (!userRepository.existsById(senderId) || !userRepository.existsById(receiverId))
        {
            throw new RuntimeException("One or both users not found");
        }

        // Check if sender and receiver are the same
        if (senderId.equals(receiverId))
        {
            throw new RuntimeException("Cannot send friend request to yourself");
        }

        // Check if request already exists
        Optional<FriendRequestModel> existingRequest = friendRepository
                .findBySenderIdAndReceiverId(senderId, receiverId);

        if (existingRequest.isPresent())
        {
            FriendRequestModel request = existingRequest.get();
            if (request.getStatus() == FriendRequestModel.RequestStatus.PENDING)
            {
                throw new RuntimeException("Friend request pending");
            }
            if (request.getStatus() == FriendRequestModel.RequestStatus.ACCEPTED)
            {
                throw new RuntimeException("Users are already friends");
            }
        }

        // Create new friend request
        FriendRequestModel newRequest = new FriendRequestModel();
        newRequest.setSenderId(senderId);
        newRequest.setReceiverId(receiverId);
        newRequest.setStatus(FriendRequestModel.RequestStatus.PENDING);
        newRequest.prePersist();

        return friendRepository.save(newRequest);
    }

    /**
     * Gets all pending friend requests for a user
     */
    public List<FriendRequestModel> getPendingRequests(String userId)
    {
        if (!userRepository.existsById(userId))
        {
            throw new RuntimeException("User not found");
        }

        List<FriendRequestModel> pendingRequests = new ArrayList<FriendRequestModel>();

        List<FriendRequestModel> requestModel = friendRepository.findBySenderId(userId);

        for (FriendRequestModel request : requestModel) {
            if (request.isPending()) {
                pendingRequests.add(request);
            }
        }

        return pendingRequests;
//        return friendRepository.findByReceiverIdAndStatus(userId, FriendRequestModel.RequestStatus.PENDING);
    }

    /**
     * Accepts a friend request
     */
    public FriendRequestModel acceptFriendRequest(String requestId, String userId)
    {
        FriendRequestModel request = friendRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        // Verify receiver is accepting
        if (!request.getReceiverId().equals(userId))
        {
            throw new RuntimeException("Not authorized to accept this request");
        }

        // Verify request is pending
        if (request.getStatus() != FriendRequestModel.RequestStatus.PENDING)
        {
            throw new RuntimeException("Request is not pending");
        }
        // Accept request
        request.setStatus(FriendRequestModel.RequestStatus.ACCEPTED);
        request.setUpdatedAt(LocalDateTime.now());

        // Save and return updated request
        return friendRepository.save(request);
    }

    /**
     * Gets friendship status between two users
     */
    public boolean areFriends(String user1Id, String user2Id)
    {
        Optional<FriendRequestModel> request = friendRepository.findBySenderIdAndReceiverId(user1Id, user2Id);

        if (request.isPresent())
        {
            return request.get().getStatus() == FriendRequestModel.RequestStatus.ACCEPTED;
        }

        request = friendRepository.findBySenderIdAndReceiverId(user2Id, user1Id);
        return request.map(req -> req.getStatus() == FriendRequestModel.RequestStatus.ACCEPTED)
                .orElse(false);
    }
}