package edu.sabanciuniv.howudoin.friends;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends MongoRepository<FriendRequestModel, String>
{
    Optional<FriendRequestModel> findBySenderIdAndReceiverId(String senderId, String receiverId);
    List<FriendRequestModel> findByReceiverIdAndStatus(String receiverId, FriendRequestModel.RequestStatus status);
    List<FriendRequestModel> findBySenderId(String senderId);
}