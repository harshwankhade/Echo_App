package com.example.echo_app.model;

import java.util.List;

/**
 * Chat.java
 *
 * Data model representing a conversation/chat in the EchoApp.
 *
 * This POJO represents a chat or conversation entity stored in Firestore.
 * A chat can be:
 * - Private chat: Between two users (isGroup = false)
 * - Group chat: Between multiple users (isGroup = true)
 *
 * Each chat tracks:
 * - Unique identifier (id - document ID)
 * - List of participant user IDs
 * - Last message information for preview
 * - Group status
 * - Last update timestamp
 *
 * Used for: Chat list display, chat preview, conversation management
 * Stored in Firestore collection: "chats"
 * Document ID: chat.id
 * Subcollection: "chats/{chatId}/messages" contains Message objects
 *
 * @see Message
 * @see User
 * @see Group
 */
public class Chat {

    /**
     * Unique identifier for the chat/conversation (Firestore document ID)
     */
    public String id;

    /**
     * List of user IDs participating in this chat
     * For private chats: typically 2 user IDs
     * For group chats: multiple user IDs
     */
    public List<String> participantIds;

    /**
     * ID of the last message in this chat
     * Used to link to the last message quickly
     */
    public String lastMessageId;

    /**
     * Preview text of the last message
     * Displayed in chat list without loading full message
     */
    public String lastMessageText;

    /**
     * Timestamp of the last message
     * Used for sorting chat list by most recent
     */
    public long lastMessageTimestamp;

    /**
     * Flag indicating if this is a group chat
     * true = group chat, false = private chat (1-to-1)
     */
    public boolean isGroup;

    /**
     * Timestamp when this chat was last updated
     * Includes message additions and metadata changes
     */
    public long updatedAt;

    /**
     * Default constructor required for Firestore deserialization
     * Do not use directly - use constructor with parameters instead
     */
    public Chat() {
    }

    /**
     * Constructor for creating a new chat
     *
     * @param id Unique chat identifier
     * @param participantIds List of user IDs in this chat
     * @param isGroup Whether this is a group chat
     */
    public Chat(String id, List<String> participantIds, boolean isGroup) {
        this.id = id;
        this.participantIds = participantIds;
        this.isGroup = isGroup;
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }

    public String getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public String getLastMessageText() {
        return lastMessageText;
    }

    public void setLastMessageText(String lastMessageText) {
        this.lastMessageText = lastMessageText;
    }

    public long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(long lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id='" + id + '\'' +
                ", participantIds=" + participantIds +
                ", lastMessageId='" + lastMessageId + '\'' +
                ", lastMessageText='" + lastMessageText + '\'' +
                ", isGroup=" + isGroup +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

