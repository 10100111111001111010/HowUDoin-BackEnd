package edu.sabanciuniv.howudoin.friends;


import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;


@Data                   //Generates getters, setters, toString, equals, hashCode
@Builder                //Enables builder pattern for object creation
@NoArgsConstructor      //Generates no-args constructor
@AllArgsConstructor     //Generates all-args constructor
@Document(collection = "friendRequests")  // Specifies MongoDB collection name
public class FriendRequestModel
{

    @Id  // MongoDB document ID
    private String id;

    @Field("sender_id")  // MongoDB field name
    private String senderId;

    @Field("receiver_id")
    private String receiverId;

    @Field("status")
    private RequestStatus status;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    // Enum to represent the status of a friend request
    public enum RequestStatus
    {
        PENDING,     // Initial state when request is sent
        ACCEPTED,    // Request has been accepted by receiver
        REJECTED,    // Request has been rejected by receiver
        BLOCKED      // User blocked
    }

    public boolean isPending()
    {
        return status == RequestStatus.PENDING;
    }

    public boolean isAccepted()
    {
        return status == RequestStatus.ACCEPTED;
    }

    // Pre-persist method to set timestamps
    public void prePersist()
    {
        if (createdAt == null)
        {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }
}