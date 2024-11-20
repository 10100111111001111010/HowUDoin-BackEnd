package edu.sabanciuniv.howudoin.groups;

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
@Document(collection = "group_messages")
public class GroupMessagesModel
{
    @Id
    private String id;

    @Field("group_id")
    private String groupId;

    @Field("sender_id")
    private String senderId;

    @Field("content")
    private String content;

    @Field("created_at")
    private LocalDateTime createdAt;

    public void prePersist()
    {
        if (createdAt == null)
        {
            createdAt = LocalDateTime.now();
        }
    }
}
