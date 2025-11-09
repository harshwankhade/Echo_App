package com.example.echo_app.repository;

import com.example.echo_app.model.Message;
import com.google.android.gms.tasks.Task;
import java.util.List;

/**
 * MessageRepository.java
 *
 * Repository interface for message-related data operations.
 *
 * Defines contracts for all message data operations with Firestore backend.
 * Implementation handles Firebase Firestore interactions for CRUD operations on Message entities.
 * Messages are stored in subcollections under Chat documents for scalability.
 *
 * All methods are asynchronous and return Task<T> for non-blocking operations.
 * Task objects support listeners, continuations, and can be awaited using Tasks.await().
 *
 * Message Lifecycle:
 * 1. sendMessage() - Create message with deliveryStatus: "sent"
 * 2. updateMessageStatus() - Update to "delivered" when received
 * 3. updateMessageStatus() - Update to "seen" when read
 * 4. deleteMessage() - Remove message (optional)
 *
 * Storage Pattern:
 * Messages are stored in: chats/{chatId}/messages/{messageId}
 * This subcollection approach enables:
 * - Scalability for large message volumes
 * - Efficient deletion of all messages for a chat
 * - Independent real-time listeners per chat
 *
 * @see Message
 * @see com.google.android.gms.tasks.Task
 */
public interface MessageRepository {

    /**
     * Retrieve all messages for a specific chat.
     *
     * Fetches all message documents from a chat's subcollection.
     * Messages are typically ordered by timestamp (newest first or oldest first).
     * For large chats, use pagination to limit results.
     *
     * @param chatId The unique identifier of the chat/conversation
     * @return Task<List<Message>> - A Task that completes with a list of Message objects
     *
     * Firestore Path: chats/{chatId}/messages/
     * Query Type: Subcollection query
     * Ordering: By timestamp (implementation determines order)
     * Performance: Consider pagination for chats with many messages
     *
     * Optimization:
     * - Add real-time listener for active chat screens
     * - Paginate for initial load (first 50-100 messages)
     * - Load older messages on scroll-up
     *
     * Use Case:
     * - Populating chat message history
     * - Real-time message updates
     */
    Task<List<Message>> getMessagesByChatId(String chatId);

    /**
     * Send a new message to a chat.
     *
     * Creates a new message document in the chat's subcollection.
     * Initially sets deliveryStatus to "sent".
     * The message should not have an ID (Firestore will auto-generate).
     *
     * @param message The Message object to send
     *               (should have: senderId, chatId, content, messageType)
     *               (timestamp will be set on server)
     * @return Task<Void> - A Task that completes when the message is created, or fails on error
     *
     * Firestore Path: chats/{message.chatId}/messages/
     * Operation: Add (create new with auto-generated ID)
     * Timestamp: Set to current time on server
     * DeliveryStatus: Set to "sent"
     *
     * Prerequisites:
     * - message.chatId must reference existing chat
     * - message.senderId must be valid user ID
     * - message.content must not be empty (for text messages)
     * - Sender must be participant of the chat (enforced by Security Rules)
     *
     * Side Effects:
     * - Update parent chat's lastMessageText and lastMessageTimestamp
     * - May update unread message counts for other participants
     */
    Task<Void> sendMessage(Message message);

    /**
     * Update a message's delivery status.
     *
     * Modifies the deliveryStatus field of an existing message.
     * Used to track message delivery progress through client devices.
     * Typical progression: "sent" → "delivered" → "seen"
     *
     * @param messageId The unique identifier of the message
     * @param newStatus The new delivery status ("sent", "delivered", or "seen")
     * @return Task<Void> - A Task that completes when status is updated, or fails if message not found
     *
     * Firestore Path: chats/{chatId}/messages/{messageId}
     * Operation: Update (single field)
     *
     * Possible Status Values:
     * - "sent": Message created and sent from sender device
     * - "delivered": Message received on recipient device
     * - "seen": Message has been read by recipient
     *
     * Typical Use Cases:
     * - "sent" → "delivered": Message persisted to Firebase
     * - "delivered" → "seen": User opens chat and message is visible
     *
     * Note: messageId must include chat context in implementation
     *       (typically passed as: chatId and messageId, or full path)
     */
    Task<Void> updateMessageStatus(String messageId, String newStatus);

    /**
     * Delete a message from a chat.
     *
     * Removes a message document from the chat's subcollection.
     * Typically only the sender can delete their own messages.
     * Security Rules should enforce deletion permissions.
     *
     * @param messageId The unique identifier of the message to delete
     * @return Task<Void> - A Task that completes when the message is deleted, or fails if not found
     *
     * Firestore Path: chats/{chatId}/messages/{messageId}
     * Operation: Delete document
     *
     * Permissions:
     * - Sender can delete their own messages
     * - Group admins may delete any message
     * - Enforce via Security Rules
     *
     * Alternatives:
     * - Instead of delete, mark as deleted with flag
     * - Keep message with content = "[Deleted]" for history
     *
     * Note: messageId must include chat context in implementation
     *       (typically full path is needed to locate correct subcollection)
     */
    Task<Void> deleteMessage(String messageId);
}

