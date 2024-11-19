package edu.sabanciuniv.howudoin.messages;

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
@Document(collection = "messages")
public class MessageModel
{
    @Id
    private String id;

    @Field("sender_id")
    private String senderId;

    @Field("receiver_id")
    private String receiverId;

    @Field("content")
    private String content;

    @Field("status")
    private ReadReceipts status;

    @Field("created_at")
    private LocalDateTime createdAt;

    public enum ReadReceipts
    {
        SENT,
        DELIVERED,
        READ,
    }

    public void prePersist()
    {
        if (createdAt == null)
        {
            createdAt = LocalDateTime.now();
        }
        if (status == null)
        {
            status = ReadReceipts.SENT;
        }
    }
}