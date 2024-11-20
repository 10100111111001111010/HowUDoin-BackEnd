package edu.sabanciuniv.howudoin.groups;

import edu.sabanciuniv.howudoin.users.UserModel;
import edu.sabanciuniv.howudoin.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMessagesRepository groupMessageRepository;
    private final UserService userService;

    /**
     * Creates a new group with the given name and members
     */
    @Transactional
    public GroupModel createGroup(String name, String creatorId, Set<String> initialMemberIds) {
        // Verify creator exists
        userService.getUserById(creatorId);

        // Verify all members exist
        for (String memberId : initialMemberIds) {
            userService.getUserById(memberId);
        }

        GroupModel group = new GroupModel();
        group.setName(name);
        group.setCreatorId(creatorId);
        group.setMemberIds(initialMemberIds);
        group.addMember(creatorId); // Ensure creator is a member
        group.prePersist();

        return groupRepository.save(group);
    }

    /**
     * Adds a new member to an existing group
     */
    @Transactional
    public GroupModel addMemberToGroup(String groupId, String userId) {
        GroupModel group = getGroupById(groupId);

        // Verify user exists
        userService.getUserById(userId);

        if (group.isMember(userId)) {
            throw new IllegalStateException("User is already a member of this group");
        }

        group.addMember(userId);
        return groupRepository.save(group);
    }

    /**
     * Gets a group and verifies the requesting user is a member
     */
    public GroupModel getGroupWithMemberCheck(String groupId, String userId) {
        GroupModel group = getGroupById(groupId);

        if (!group.isMember(userId)) {
            throw new IllegalStateException("User is not a member of this group");
        }

        return group;
    }

    /**
     * Sends a message to all members of the specified group
     */
    @Transactional
    public GroupMessagesModel sendGroupMessage(String groupId, String senderId, String content) {
        GroupModel group = getGroupById(groupId);

        if (!group.isMember(senderId)) {
            throw new IllegalStateException("Only group members can send messages");
        }

        GroupMessagesModel message = new GroupMessagesModel();
        message.setGroupId(groupId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.prePersist();

        return groupMessageRepository.save(message);
    }

    /**
     * Retrieves the message history for the specified group
     */
    public Page<GroupMessagesModel> getGroupMessages(String groupId, String userId, Pageable pageable) {
        GroupModel group = getGroupById(groupId);

        if (!group.isMember(userId)) {
            throw new IllegalStateException("Only group members can view messages");
        }

        return groupMessageRepository.findByGroupIdOrderByCreatedAtDesc(groupId, pageable);
    }

    /**
     * Retrieves the list of members for the specified group
     */
    public List<UserModel> getGroupMembers(String groupId, String requestingUserId) {
        GroupModel group = getGroupById(groupId);

        if (!group.isMember(requestingUserId)) {
            throw new IllegalStateException("Only group members can view member list");
        }

        List<UserModel> members = new ArrayList<>();
        for (String memberId : group.getMemberIds()) {
            members.add(userService.getUserById(memberId));
        }
        return members;
    }

    /**
     * Gets all groups for a user
     */
    public List<GroupModel> getUserGroups(String userId) {
        // Verify user exists
        userService.getUserById(userId);
        return groupRepository.findByMemberIdsContaining(userId);
    }

    /**
     * Helper method to get group by ID
     */
    private GroupModel getGroupById(String groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + groupId));
    }

    /**
     * Removes a member from the group
     */
    @Transactional
    public GroupModel removeMemberFromGroup(String groupId, String userId) {
        GroupModel group = getGroupById(groupId);

        if (!group.isMember(userId)) {
            throw new IllegalStateException("User is not a member of this group");
        }

        // Don't allow removing the last member
        if (group.getMemberIds().size() <= 1) {
            throw new IllegalStateException("Cannot remove the last member of the group");
        }

        group.removeMember(userId);
        return groupRepository.save(group);
    }

    /**
     * Check if a user is a member of a group
     */
    public boolean isGroupMember(String groupId, String userId) {
        GroupModel group = getGroupById(groupId);
        return group.isMember(userId);
    }

    /**
     * Get the total number of messages in a group
     */
    public long getGroupMessageCount(String groupId, String userId) {
        // Verify user is member
        getGroupWithMemberCheck(groupId, userId);
        return groupMessageRepository.countByGroupId(groupId);
    }

    /**
     * Delete a group (only creator can do this)
     */
    @Transactional
    public void deleteGroup(String groupId, String userId) {
        GroupModel group = getGroupById(groupId);

        if (!group.getCreatorId().equals(userId)) {
            throw new IllegalStateException("Only the group creator can delete the group");
        }

        // Delete all group messages first
        groupMessageRepository.deleteByGroupId(groupId);
        // Delete the group
        groupRepository.delete(group);
    }
}