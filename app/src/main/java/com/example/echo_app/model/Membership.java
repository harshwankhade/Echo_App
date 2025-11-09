package com.example.echo_app.model;

/**
 * Membership.java
 *
 * Data model representing a membership relationship between a user and a group.
 *
 * This POJO represents a membership entity stored in Firestore. It tracks:
 * - Which user is a member of which group
 * - Admin status within the group
 * - When the user joined the group
 *
 * Membership provides a many-to-many relationship between User and Group:
 * - One user can be member of multiple groups
 * - One group can have multiple members
 *
 * This entity can be:
 * 1. Stored in a dedicated "memberships" collection
 * 2. Or represented as fields in Group.memberIds
 *
 * This model is useful for:
 * - Querying all groups a user belongs to
 * - Tracking join dates
 * - Managing admin roles
 * - Storing membership metadata
 *
 * Used for: Group member management, permissions, user's group list
 * Stored in Firestore collection: "memberships"
 * Or stored as subcollection: "groups/{groupId}/members"
 * Document ID: membership.id
 *
 * @see User
 * @see Group
 * @see Chat
 */
public class Membership {

    /**
     * Unique identifier for the membership (Firestore document ID)
     * Can be: "{userId}_{groupId}" or auto-generated
     */
    public String id;

    /**
     * User ID of the group member
     * References the User document
     */
    public String userId;

    /**
     * Group ID that the user belongs to
     * References the Group document
     */
    public String groupId;

    /**
     * Admin status of the user in this group
     * true = user is an admin, false = regular member
     */
    public boolean isAdmin;

    /**
     * Timestamp when the user joined the group
     */
    public long joinedAt;

    /**
     * Default constructor required for Firestore deserialization
     * Do not use directly - use constructor with parameters instead
     */
    public Membership() {
    }

    /**
     * Constructor for creating a new membership
     *
     * @param id Membership unique identifier
     * @param userId ID of the user
     * @param groupId ID of the group
     */
    public Membership(String id, String userId, String groupId) {
        this.id = id;
        this.userId = userId;
        this.groupId = groupId;
        this.isAdmin = false;
        this.joinedAt = System.currentTimeMillis();
    }

    /**
     * Constructor for creating a membership with admin status
     *
     * @param id Membership unique identifier
     * @param userId ID of the user
     * @param groupId ID of the group
     * @param isAdmin Whether user is an admin
     */
    public Membership(String id, String userId, String groupId, boolean isAdmin) {
        this.id = id;
        this.userId = userId;
        this.groupId = groupId;
        this.isAdmin = isAdmin;
        this.joinedAt = System.currentTimeMillis();
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public long getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(long joinedAt) {
        this.joinedAt = joinedAt;
    }

    @Override
    public String toString() {
        return "Membership{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", isAdmin=" + isAdmin +
                ", joinedAt=" + joinedAt +
                '}';
    }
}

