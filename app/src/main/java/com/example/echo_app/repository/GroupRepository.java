package com.example.echo_app.repository;

import com.example.echo_app.model.Group;
import com.google.android.gms.tasks.Task;
import java.util.List;

/**
 * GroupRepository.java
 *
 * Repository interface for group-related data operations.
 *
 * Defines contracts for all group data operations with Firestore backend.
 * Implementation handles Firebase Firestore interactions for CRUD operations on Group entities
 * and group membership management.
 *
 * All methods are asynchronous and return Task<T> for non-blocking operations.
 * Task objects support listeners, continuations, and can be awaited using Tasks.await().
 *
 * Group Management:
 * - Groups are separate from Chats but linked via group chat functionality
 * - Groups have dedicated metadata (name, description, members, admin)
 * - Members are tracked in a separate memberships subcollection
 * - Only admins can modify group settings
 *
 * Data Structure:
 * - Group documents: groups/{groupId}
 * - Member subcollection: groups/{groupId}/members/{userId}
 * - Linked Chat: chats/{chatId} where isGroup=true and groupId matches
 *
 * @see Group
 * @see com.google.android.gms.tasks.Task
 */
public interface GroupRepository {

    /**
     * Retrieve a group by its unique ID.
     *
     * Fetches a single group document from Firestore by group ID.
     * Returns group metadata including name, description, members list, and admin info.
     *
     * @param groupId The unique identifier of the group
     * @return Task<Group> - A Task that completes with the Group object if found, or fails if not found
     *
     * Firestore Path: groups/{groupId}
     * Query Type: Document read (single document)
     * Performance: Very fast (direct document lookup)
     *
     * Use Cases:
     * - Load group information when entering group chat
     * - Display group settings and member list
     * - Verify group exists before operations
     */
    Task<Group> getGroupById(String groupId);

    /**
     * Retrieve all groups that a user is a member of.
     *
     * Fetches all group documents where the user is listed in memberIds.
     * Returns complete Group objects for all groups the user belongs to.
     * Used for populating user's group list in the UI.
     *
     * @param userId The unique identifier of the user
     * @return Task<List<Group>> - A Task that completes with a list of Group objects user belongs to
     *
     * Firestore Path: groups/ with where clause
     * Query Type: Collection query with filter
     * Filter: memberIds array contains userId
     * Performance: Requires index on memberIds field
     *
     * Optimization:
     * - Consider denormalizing with user_groups subcollection for faster queries
     * - Cache results locally
     * - Consider pagination if user is in many groups
     *
     * Use Cases:
     * - Populate user's group list on app startup
     * - Real-time listener for group updates
     * - Search/filter user's groups
     *
     * Note: Requires Firestore index on memberIds field
     */
    Task<List<Group>> getGroupsForUser(String userId);

    /**
     * Create a new group.
     *
     * Creates a new group document in Firestore.
     * Initializes group with creator as admin and only initial member.
     * Also creates the linked chat document with isGroup=true.
     *
     * @param group The Group object to create
     *             (should have: name, description, adminId, memberIds)
     * @return Task<Void> - A Task that completes when the group is created, or fails on error
     *
     * Firestore Path: groups/{group.id}
     * Operation: Set (create new)
     * Timestamps: createdAt and updatedAt set to current time
     *
     * Prerequisites:
     * - group.name must not be empty
     * - group.adminId must be valid user ID
     * - group.memberIds should include adminId
     * - group.id typically auto-generated (can be empty, Firestore will generate)
     *
     * Side Effects:
     * - Creates corresponding Chat document (isGroup=true, groupId=group.id)
     * - Initializes memberships subcollection or members array
     * - Creates membership documents for all initial members
     *
     * Use Case:
     * - User creates new group chat
     */
    Task<Void> createGroup(Group group);

    /**
     * Update an existing group's information.
     *
     * Modifies an existing group document in Firestore.
     * Typically used for updating name, description, or group image.
     * Only group admins should be able to perform this operation.
     *
     * @param group The Group object with updated information
     *             (id field identifies which group to update)
     * @return Task<Void> - A Task that completes when the group is updated, or fails if not found
     *
     * Firestore Path: groups/{group.id}
     * Operation: Update (merge, not overwrite)
     * Timestamps: updatedAt set to current time
     *
     * Prerequisites:
     * - group.id must reference existing group
     * - Caller must be group admin (enforced by Security Rules)
     *
     * Updatable Fields:
     * - name: Group display name
     * - description: Group description
     * - groupImageUrl: Group profile picture
     * - adminId: Transfer admin privileges (admin-only)
     *
     * Permissions:
     * - Only group admin can update
     * - Enforce via Security Rules
     */
    Task<Void> updateGroup(Group group);

    /**
     * Delete a group and all associated data.
     *
     * Removes a group document from Firestore.
     * Should also handle deletion of:
     * - Related Chat document (isGroup=true)
     * - Messages subcollection under the chat
     * - Memberships subcollection
     * Only group admins should be able to delete groups.
     *
     * @param groupId The unique identifier of the group to delete
     * @return Task<Void> - A Task that completes when the group is deleted, or fails if not found
     *
     * Firestore Path: groups/{groupId}
     * Operation: Delete document
     * Related Deletions: Chat, Messages, Memberships
     *
     * Prerequisites:
     * - groupId must reference existing group
     * - Caller must be group admin (enforced by Security Rules)
     *
     * Cascade Deletion:
     * - Delete groups/{groupId} document
     * - Delete groups/{groupId}/members/* (all membership docs)
     * - Delete corresponding chat document
     * - Delete chats/{chatId}/messages/* (all message docs)
     *
     * Note: Use Cloud Function for atomic cascading deletes
     *       to ensure data consistency
     */
    Task<Void> deleteGroup(String groupId);

    /**
     * Add a new member to a group.
     *
     * Adds a user to a group's member list and creates membership record.
     * Updates the group's memberIds array and creates membership subcollection entry.
     * Only group admins should be able to add members.
     *
     * @param groupId The unique identifier of the group
     * @param userId The unique identifier of the user to add
     * @param isAdmin Whether the new member should be an admin
     * @return Task<Void> - A Task that completes when member is added, or fails on error
     *
     * Firestore Operations:
     * - Update groups/{groupId}: Add userId to memberIds array
     * - Create groups/{groupId}/members/{userId}: New membership doc
     * - Update chats/{chatId}: Add userId to participantIds
     *
     * Prerequisites:
     * - groupId must reference existing group
     * - userId must reference valid user
     * - User must not already be in group
     * - Caller must be group admin (enforced by Security Rules)
     *
     * Side Effects:
     * - User gains access to group chat
     * - User can see message history
     * - User can receive messages
     * - Notifications sent to group members
     *
     * Use Cases:
     * - Admin invites user to group
     * - User accepts group invite
     */
    Task<Void> addMemberToGroup(String groupId, String userId, boolean isAdmin);

    /**
     * Remove a member from a group.
     *
     * Removes a user from a group's member list and deletes membership record.
     * Updates the group's memberIds array and removes membership subcollection entry.
     * Only group admins should be able to remove members (except self-removal).
     *
     * @param groupId The unique identifier of the group
     * @param userId The unique identifier of the user to remove
     * @return Task<Void> - A Task that completes when member is removed, or fails on error
     *
     * Firestore Operations:
     * - Update groups/{groupId}: Remove userId from memberIds array
     * - Delete groups/{groupId}/members/{userId}: Remove membership doc
     * - Update chats/{chatId}: Remove userId from participantIds
     *
     * Prerequisites:
     * - groupId must reference existing group
     * - userId must be member of group
     * - Caller must be admin OR userId equals caller (self-removal)
     * - Cannot remove if last admin (enforced by Security Rules)
     *
     * Side Effects:
     * - User loses access to group chat
     * - User cannot see new messages
     * - Previous messages remain in history
     * - Other members notified of removal
     *
     * Use Cases:
     * - Admin removes member from group
     * - User leaves group
     * - User account suspended or deleted
     */
    Task<Void> removeMemberFromGroup(String groupId, String userId);
}

