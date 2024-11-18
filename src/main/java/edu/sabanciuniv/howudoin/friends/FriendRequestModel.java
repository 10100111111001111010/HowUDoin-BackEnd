package edu.sabanciuniv.howudoin.friends;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "friendRequests")
public class FriendRequestModel {
    @Id
    private String id;

    @Field("sender_id")
    private String senderId;

    @Field("receiver_id")
    private String receiverId;

    @Field("status")
    private RequestStatus status;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    public enum RequestStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    public boolean isPending() {
        return status == RequestStatus.PENDING;
    }

    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }
}