package com.example.echo_app.model;

/**
 * Message.java
 *
 * Data model representing a message in the EchoApp.
 *
 * This POJO represents a message entity stored in Firestore. Each message has:
 * - Unique identifier (id - document ID)
 * - Sender and recipient information
 * - Chat/Conversation reference
 * - Content and media information
 * - Message type (text, image, video, audio)
 * - Delivery status tracking
 * - Timestamp for message creation
 *
 * Message Types:
 * - "text": Plain text message
 * - "image": Image message with mediaUrl
 * - "video": Video message with mediaUrl
 * - "audio": Audio message with mediaUrl
 *
 * Delivery Status:
 * - "sent": Message sent from sender's device
 * - "delivered": Message reached recipient's device
 * - "seen": Message read by recipient
 *
 * Used for: Chat display, message history, real-time messaging
 * Stored in Firestore collection: "chats/{chatId}/messages" or "messages"
 * Document ID: message.id
 *
 * @see User
 * @see Chat
 */
public class Message {

    /**
     * Unique identifier for the message (Firestore document ID)
     */
    public String id;

    /**
     * User ID of the message sender
     */
    public String senderId;

    /**
     * User ID of the message recipient (for private chats)
     * Null for group chat messages
     */
    public String receiverId;

    /**
     * Chat/Conversation ID this message belongs to
     */
    public String chatId;

    /**
     * The text content of the message
     */
    public String content;

    /**
     * URL to media file (image, video, or audio)
     * Null if message type is "text"
     */
    public String mediaUrl;

    /**
     * Type of message content
     * Possible values: "text", "image", "video", "audio"
     */
    public String messageType;

    /**
     * Delivery status of the message
     * Possible values: "sent", "delivered", "seen"
     */
    public String deliveryStatus;

    /**
     * Timestamp when the message was created
     */
    public long timestamp;

    /**
     * Default constructor required for Firestore deserialization
     * Do not use directly - use constructor with parameters instead
     */
    public Message() {
    }

    /**
     * Constructor for creating a text message
     *
     * @param id Message unique identifier
     * @param senderId ID of the sender
     * @param chatId ID of the chat/conversation
     * @param content Message text content
     * @param messageType Type of message (e.g., "text")
     */
    public Message(String id, String senderId, String chatId, String content, String messageType) {
        this.id = id;
        this.senderId = senderId;
        this.chatId = chatId;
        this.content = content;
        this.messageType = messageType;
        this.deliveryStatus = "sent";
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", chatId='" + chatId + '\'' +
                ", content='" + content + '\'' +
                ", messageType='" + messageType + '\'' +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

