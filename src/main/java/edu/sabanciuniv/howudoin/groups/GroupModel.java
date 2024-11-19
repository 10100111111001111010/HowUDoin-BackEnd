package edu.sabanciuniv.howudoin.groups;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "groups")
public class GroupModel
{
    @Id
    private String id;

    @Field("group_name")
    private String name;

    @Field("creator_id")
    private String creatorId;

    @Field("admin_ids")
    private Set<String> adminIds = new HashSet<>();

    @Field("member_ids")
    private Set<String> memberIds = new HashSet<>();

    @Field("messages")
    private List<GroupMessage> messages = new ArrayList<>();

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    /**
    Embedded GroupMessage class
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupMessage
    {
        @Id
        private String id;
        private String senderId;
        private String content;
        private LocalDateTime timestamp;
        private MessageStatus status;

        public enum MessageStatus {
            SENT,
            DELIVERED,
            READ
        }
    }

    public void addMessage(String senderId, String content) {
        GroupMessage message = new GroupMessage();
        message.setId(java.util.UUID.randomUUID().toString());
        message.setSenderId(senderId);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setStatus(GroupMessage.MessageStatus.SENT);
        messages.add(message);
    }

    public void addMember(String userId)
    {
        memberIds.add(userId);
    }

    public void removeMember(String userId)
    {
        memberIds.remove(userId);
        adminIds.remove(userId);
    }

    public void addAdmin(String userId)
    {
        adminIds.add(userId);
        memberIds.add(userId);
    }

    public boolean isMember(String userId)
    {
        return memberIds.contains(userId);
    }

    public boolean isAdmin(String userId)
    {
        return adminIds.contains(userId);
    }

    public void prePersist()
    {
        if (createdAt == null)
        {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }
}