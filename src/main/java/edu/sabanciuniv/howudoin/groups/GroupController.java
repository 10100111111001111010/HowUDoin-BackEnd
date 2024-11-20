package edu.sabanciuniv.howudoin.groups;

import edu.sabanciuniv.howudoin.groups.DTO.AddMemberRequest;
import edu.sabanciuniv.howudoin.groups.DTO.CreateGroupRequest;
import edu.sabanciuniv.howudoin.groups.DTO.SendMessageRequest;
import edu.sabanciuniv.howudoin.security.DTO.ApiResponse;
import edu.sabanciuniv.howudoin.users.UserModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing group-related operations including:
 * - Group creation
 * - Member management
 * - Group messaging
 */
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    /**
     * Creates a new group with given name and members
     */
    @PostMapping("/create")
    public ResponseEntity<?> createGroup(
            @Valid @RequestBody CreateGroupRequest request,
            @RequestHeader("User-Id") String creatorId)
    {
        try
        {
            GroupModel group = groupService.createGroup(
                    request.getName(),
                    creatorId,
                    request.getMemberIds()
            );
            return ResponseEntity.ok(group);
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * Adds a new member to an existing group
     */
    @PostMapping("/{groupId}/add-member")
    public ResponseEntity<?> addMemberToGroup(
            @PathVariable String groupId,
            @Valid @RequestBody AddMemberRequest request)
    {
        try {
            GroupModel group = groupService.addMemberToGroup(
                    groupId,
                    request.getUserId()
            );
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * Sends a message to all members of the specified group
     */
    @PostMapping("/{groupId}/send")
    public ResponseEntity<?> sendGroupMessage(
            @PathVariable String groupId,
            @Valid @RequestBody SendMessageRequest request,
            @RequestHeader("User-Id") String senderId) {
        try {
            GroupMessagesModel message = groupService.sendGroupMessage(
                    groupId,
                    senderId,
                    request.getContent()
            );
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * Retrieves the message history for the specified group
     */
    @GetMapping("/{groupId}/messages")
    public ResponseEntity<?> getGroupMessages(
            @PathVariable String groupId,
            @RequestHeader("User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<GroupMessagesModel> messages = groupService.getGroupMessages(
                    groupId,
                    userId,
                    PageRequest.of(page, size)
            );
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * Retrieves the list of members for the specified group
     */
    @GetMapping("/{groupId}/members")
    public ResponseEntity<?> getGroupMembers(
            @PathVariable String groupId,
            @RequestHeader("User-Id") String userId) {
        try {
            List<UserModel> members = groupService.getGroupMembers(groupId, userId);
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * Retrieves details of a specific group
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroup(
            @PathVariable String groupId,
            @RequestHeader("User-Id") String userId) {
        try {
            GroupModel group = groupService.getGroupWithMemberCheck(groupId, userId);
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * Gets all groups for the current user
     */
    @GetMapping("/my-groups")
    public ResponseEntity<?> getMyGroups(@RequestHeader("User-Id") String userId) {
        try {
            List<GroupModel> groups = groupService.getUserGroups(userId);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}