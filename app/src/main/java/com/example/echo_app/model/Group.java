package com.example.echo_app.model;

import java.util.List;

/**
 * Group.java
 *
 * Data model representing a group chat in the EchoApp.
 *
 * This POJO represents a group entity stored in Firestore. A group is a
 * dedicated entity for group chats with extended functionality compared to
 * regular chats:
 *
 * - Group metadata (name, description, image)
 * - Member management (list of members)
 * - Admin privileges
 * - Creation and update timestamps
 *
 * Each group has:
 * - Unique identifier (id - document ID)
 * - Display name and description
 * - Member list with admin designation
 * - Group profile image
 * - Timestamps for creation and updates
 *
 * Relationship to Chat:
 * - A Chat with isGroup=true corresponds to a Group
 * - Group.id matches Chat.id for consistency
 *
 * Used for: Group management, member display, group settings
 * Stored in Firestore collection: "groups"
 * Document ID: group.id
 * Linked to Firestore collection: "chats/{chatId}" where Chat.isGroup=true
 *
 * @see Chat
 * @see Membership
 * @see User
 */
public class Group {

    /**
     * Unique identifier for the group (Firestore document ID)
     * Should match the Chat.id for the corresponding group chat
     */
    public String id;

    /**
     * Display name of the group
     * Visible to all group members
     */
    public String name;

    /**
     * Description of the group
     * Optional information about the group's purpose
     */
    public String description;

    /**
     * User ID of the group administrator/creator
     * Has special privileges like adding/removing members
     */
    public String adminId;

    /**
     * List of user IDs who are members of this group
     * Includes the admin user
     */
    public List<String> memberIds;

    /**
     * URL to group's profile picture (stored in Firebase Storage)
     * Optional - can be null if no image is set
     */
    public String groupImageUrl;

    /**
     * Timestamp when the group was created
     */
    public long createdAt;

    /**
     * Timestamp when the group was last updated
     * Updated when: members added/removed, description changed, image changed, etc.
     */
    public long updatedAt;

    /**
     * Default constructor required for Firestore deserialization
     * Do not use directly - use constructor with parameters instead
     */
    public Group() {
    }

    /**
     * Constructor for creating a new group
     *
     * @param id Unique group identifier
     * @param name Group display name
     * @param adminId User ID of the group administrator
     * @param memberIds List of member user IDs
     */
    public Group(String id, String name, String adminId, List<String> memberIds) {
        this.id = id;
        this.name = name;
        this.adminId = adminId;
        this.memberIds = memberIds;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public String getGroupImageUrl() {
        return groupImageUrl;
    }

    public void setGroupImageUrl(String groupImageUrl) {
        this.groupImageUrl = groupImageUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Check if a user is an admin of this group
     *
     * @param userId ID of the user to check
     * @return true if user is the group admin
     */
    public boolean isAdmin(String userId) {
        return adminId != null && adminId.equals(userId);
    }

    /**
     * Check if a user is a member of this group
     *
     * @param userId ID of the user to check
     * @return true if user is a member
     */
    public boolean isMember(String userId) {
        return memberIds != null && memberIds.contains(userId);
    }

    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", adminId='" + adminId + '\'' +
                ", memberCount=" + (memberIds != null ? memberIds.size() : 0) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

