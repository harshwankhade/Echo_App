package com.example.echo_app.model;

import com.google.firebase.Timestamp;

/**
 * User.java
 *
 * Data model representing a user in the EchoApp.
 *
 * This POJO represents a user entity stored in Firestore. Each user has:
 * - Unique identifier (id - document ID)
 * - Profile information (displayName, email, profileImageUrl)
 * - Online status tracking (isOnline, lastSeen)
 * - Timestamps for creation and updates
 *
 * Used for: User authentication, profile display, contact lists, etc.
 * Stored in Firestore collection: "users"
 * Document ID: user.id
 *
 * @see Message
 * @see Chat
 * @see Group
 */
public class User {

    /**
     * Unique identifier for the user (Firestore document ID)
     */
    public String id;

    /**
     * Display name of the user (visible in app)
     */
    public String displayName;

    /**
     * Email address of the user (unique, used for authentication)
     */
    public String email;

    /**
     * URL to user's profile picture (stored in Firebase Storage)
     */
    public String profileImageUrl;

    /**
     * Online status of the user
     * true = currently online, false = offline
     */
    public boolean isOnline;

    /**
     * Timestamp of user's last activity
     * Used to show "last seen" information
     */
    public long lastSeen;

    /**
     * Timestamp when the user account was created
     */
    public long createdAt;

    /**
     * Timestamp when the user profile was last updated
     */
    public long updatedAt;

    /**
     * Default constructor required for Firestore deserialization
     * Do not use directly - use constructor with parameters instead
     */
    public User() {
    }

    /**
     * Constructor with basic user information
     *
     * @param id Unique user identifier
     * @param displayName User's display name
     * @param email User's email address
     */
    public User(String id, String displayName, String email) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
        this.isOnline = false;
        this.lastSeen = System.currentTimeMillis();
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
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

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", isOnline=" + isOnline +
                ", lastSeen=" + lastSeen +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

