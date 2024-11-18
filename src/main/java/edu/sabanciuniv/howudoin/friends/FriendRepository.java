package edu.sabanciuniv.howudoin.friends;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface FriendRepository extends MongoRepository<FriendRequestModel, String>
{

    // Find friend request by sender and receiver (to check if request already exists)
    Optional<FriendRequestModel> findBySenderIdAndReceiverId(String senderId, String receiverId);

    // Find all pending requests for a specific receiver
    List<FriendRequestModel> findByReceiverIdAndStatus(String receiverId, FriendRequestModel.RequestStatus status);

    // Find all requests sent by a user with specific status
    List<FriendRequestModel> findBySenderIdAndStatus(String senderId, FriendRequestModel.RequestStatus status);
}
