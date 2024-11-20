package edu.sabanciuniv.howudoin.groups;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "groups")
public class GroupModel
{
    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("creator_id")
    private String creatorId;

    @Field("member_ids")
    private Set<String> memberIds = new HashSet<>();

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    public void addMember(String userId)
    {
        if (memberIds == null) {
            memberIds = new HashSet<>();
        }
        memberIds.add(userId);
    }

    public void removeMember(String userId)
    {
        if (memberIds != null) {
            memberIds.remove(userId);
        }
    }

    public boolean isMember(String userId)
    {
        return memberIds != null && memberIds.contains(userId);
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
