package com.example.echo_app.repository;

import com.example.echo_app.model.User;
import com.google.android.gms.tasks.Task;
import java.util.List;

/**
 * UserRepository.java
 *
 * Repository interface for user-related data operations.
 *
 * Defines contracts for all user data operations with Firestore backend.
 * Implementation handles Firebase Firestore interactions for CRUD operations on User entities.
 *
 * All methods are asynchronous and return Task<T> for non-blocking operations.
 * Task objects support listeners, continuations, and can be awaited using Tasks.await().
 *
 * Usage Pattern:
 * - Call repository method, get Task<T>
 * - Add listener or continuation to handle result
 * - Handle both success and failure cases
 *
 * Example:
 * userRepository.getUserById("uid123")
 *     .addOnSuccessListener(user -> {
 *         // Handle user data
 *     })
 *     .addOnFailureListener(e -> {
 *         // Handle error
 *     });
 *
 * @see User
 * @see com.google.android.gms.tasks.Task
 */
public interface UserRepository {

    /**
     * Retrieve a user by their unique ID.
     *
     * Fetches a single user document from Firestore by user ID (matches Firebase Auth UID).
     * Returns a Task that completes with the User object if found, or fails if not found.
     *
     * @param userId The unique identifier of the user (Firebase Auth UID)
     * @return Task<User> - A Task that completes with the User object
     *
     * Firestore Path: users/{userId}
     * Query Type: Document read (single document)
     * Performance: Very fast (direct document lookup)
     */
    Task<User> getUserById(String userId);

    /**
     * Retrieve all users from the database.
     *
     * Fetches all user documents from Firestore. Use with caution on large datasets.
     * For better performance, consider filtering queries when possible.
     *
     * @return Task<List<User>> - A Task that completes with a list of all User objects
     *
     * Firestore Path: users/
     * Query Type: Collection query (all documents)
     * Performance: Slow for large user bases - consider pagination
     * Note: Consider adding pagination or filtering for production use
     */
    Task<List<User>> getAllUsers();

    /**
     * Add a new user to the database.
     *
     * Creates a new user document in Firestore with the provided User object.
     * The user ID (User.id) should match the Firebase Authentication UID.
     * Fails if user already exists (use updateUser instead).
     *
     * @param user The User object to add (must have id, displayName, email)
     * @return Task<Void> - A Task that completes when the user is added, or fails on error
     *
     * Firestore Path: users/{user.id}
     * Operation: Set (create new)
     * Timestamps: createdAt and updatedAt automatically set to current time
     *
     * Prerequisites:
     * - user.id must be set (matches Firebase Auth UID)
     * - user.email must be unique
     */
    Task<Void> addUser(User user);

    /**
     * Update an existing user's information.
     *
     * Modifies an existing user document in Firestore with new data.
     * Only non-null fields are updated (partial update).
     * The user ID (User.id) must exist in Firestore.
     *
     * @param user The User object with updated information (id field identifies which user to update)
     * @return Task<Void> - A Task that completes when the user is updated, or fails if user not found
     *
     * Firestore Path: users/{user.id}
     * Operation: Update (merge, not overwrite)
     * Timestamps: updatedAt automatically set to current time
     *
     * Prerequisites:
     * - user.id must match an existing Firestore document
     * - Typically used for: displayName, profileImageUrl, isOnline, lastSeen
     */
    Task<Void> updateUser(User user);

    /**
     * Delete a user from the database.
     *
     * Removes a user document from Firestore.
     * Note: Also consider deleting related data (chats, messages, groups, memberships).
     * This should typically be done with a Cloud Function to ensure data consistency.
     *
     * @param userId The unique identifier of the user to delete
     * @return Task<Void> - A Task that completes when the user is deleted, or fails if not found
     *
     * Firestore Path: users/{userId}
     * Operation: Delete document
     *
     * Cascade Considerations:
     * - Related Chat documents should be updated to remove userId from participantIds
     * - Group documents should remove userId from memberIds
     * - Membership documents should be deleted
     * - Consider using Cloud Function for cascading deletes
     */
    Task<Void> deleteUser(String userId);

    /**
     * Retrieve a user by their email address.
     *
     * Fetches a single user document from Firestore by email address.
     * Emails must be unique in the system.
     * Used primarily for searching/finding users and verifying email uniqueness.
     *
     * @param email The email address of the user to find
     * @return Task<User> - A Task that completes with the User object if found, or fails if not found
     *
     * Firestore Path: users/ with where clause
     * Query Type: Collection query with filter
     * Performance: Requires index on email field
     * Use Case: Finding user for private chat, email verification
     *
     * Note: Requires Firestore index on email field for performance
     */
    Task<User> getUserByEmail(String email);
}

