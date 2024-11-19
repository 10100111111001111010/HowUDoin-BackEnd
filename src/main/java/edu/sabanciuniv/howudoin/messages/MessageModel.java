package edu.sabanciuniv.howudoin.messages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class MessageModel
{
    @Id
    private String id;

    @NotBlank
    @Indexed
    @Field("sender_id")
    private String senderId;

    @Indexed
    @Field("receiver_id")
    private String receiverId;


    @NotBlank
    @Field("content")
    private String content;

    @NotNull
    @Field("status")
    private MessageStatus status;


    @NotNull
    @Indexed
    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    public enum MessageStatus
    {
        SENT,
        DELIVERED,
        READ
    }

    public void prePersist()
    {
        if (createdAt == null)
        {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();

        if (status == null)
        {
            status = MessageStatus.SENT;
        }

    }

    // Validate that sender and receiver are different
    public boolean isValid()
    {
        return !senderId.equals(receiverId);
    }
}