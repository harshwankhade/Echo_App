package com.example.echo_app.repository;

import android.util.Log;

import com.example.echo_app.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * FirestoreUserRepository.java
 *
 * Implementation of UserRepository using Firebase Firestore as the backend.
 *
 * Provides CRUD operations for User documents stored in the Firestore "users" collection.
 * All operations are asynchronous and return Task<T> for non-blocking execution.
 *
 * Features:
 * - Firebase Firestore as data source
 * - Asynchronous operations using Task API
 * - Automatic serialization/deserialization of User POJOs
 * - Comprehensive error handling and logging
 * - Collection reference constants for maintainability
 *
 * Usage:
 * UserRepository userRepository = new FirestoreUserRepository();
 * userRepository.getUserById("uid123")
 *     .addOnSuccessListener(user -> { /* handle user */ })
 *     .addOnFailureListener(e -> { /* handle error */ });
 */
public class FirestoreUserRepository implements UserRepository {

    private static final String TAG = "FirestoreUserRepository";
    private static final String USERS_COLLECTION = "users";

    private final FirebaseFirestore db;

    /**
     * Default constructor.
     * Initializes Firebase Firestore instance for database operations.
     */
    public FirestoreUserRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Retrieve a user by their unique ID.
     *
     * Fetches a single user document from the "users" collection by user ID.
     * The user ID typically matches the Firebase Authentication UID.
     *
     * @param userId The unique identifier of the user (Firebase Auth UID)
     * @return Task<User> - Task that completes with the User object if found,
     *         or fails with an exception if not found or on error
     *
     * Firestore Path: users/{userId}
     * Operation: Document read
     * Error Handling: Logs error and returns failed task
     */
    @Override
    public Task<User> getUserById(String userId) {
        Log.d(TAG, "Fetching user with ID: " + userId);

        return db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        Exception exception = task.getException();
                        Log.e(TAG, "Error fetching user: " + userId, exception);
                        throw exception;
                    }

                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        Log.d(TAG, "User found: " + user.getDisplayName());
                        return user;
                    } else {
                        Exception notFoundError = new Exception("User not found: " + userId);
                        Log.e(TAG, "User not found: " + userId);
                        throw notFoundError;
                    }
                });
    }

    /**
     * Retrieve all users from the database.
     *
     * Fetches all user documents from the "users" collection.
     * Note: This operation can be slow for large user bases.
     * Consider implementing pagination for production use.
     *
     * @return Task<List<User>> - Task that completes with a list of all User objects,
     *         or fails with an exception on error
     *
     * Firestore Path: users/
     * Operation: Collection query (all documents)
     * Warning: Use with caution on large datasets
     */
    @Override
    public Task<List<User>> getAllUsers() {
        Log.d(TAG, "Fetching all users");

        return db.collection(USERS_COLLECTION)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        Exception exception = task.getException();
                        Log.e(TAG, "Error fetching all users", exception);
                        throw exception;
                    }

                    QuerySnapshot querySnapshot = task.getResult();
                    List<User> userList = new ArrayList<>();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            userList.add(user);
                        }
                    }

                    Log.d(TAG, "Fetched " + userList.size() + " users");
                    return userList;
                });
    }

    /**
     * Add a new user to the database.
     *
     * Creates a new user document in the "users" collection.
     * The document ID is set to the user's ID (typically Firebase Auth UID).
     * Timestamps (createdAt and updatedAt) should be set in the User object before calling.
     *
     * @param user The User object to add
     * @return Task<Void> - Task that completes when the user is successfully added,
     *         or fails with an exception on error
     *
     * Firestore Path: users/{user.id}
     * Operation: Set (create new document)
     * Prerequisites: user.id must be set
     */
    @Override
    public Task<Void> addUser(User user) {
        if (user == null || user.getId() == null) {
            Log.e(TAG, "Cannot add user: user or user.id is null");
            return Tasks.forException(new IllegalArgumentException("User and user.id must not be null"));
        }

        Log.d(TAG, "Adding new user: " + user.getId());

        return db.collection(USERS_COLLECTION)
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User added successfully: " + user.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding user: " + user.getId(), e);
                });
    }

    /**
     * Update an existing user's information.
     *
     * Modifies fields in an existing user document in the "users" collection.
     * Only non-null fields are updated (partial update).
     * The updatedAt timestamp should be set in the User object before calling.
     *
     * @param user The User object with updated information (user.id identifies the document)
     * @return Task<Void> - Task that completes when the user is successfully updated,
     *         or fails with an exception if user not found or on error
     *
     * Firestore Path: users/{user.id}
     * Operation: Update (merge, not overwrite)
     */
    @Override
    public Task<Void> updateUser(User user) {
        if (user == null || user.getId() == null) {
            Log.e(TAG, "Cannot update user: user or user.id is null");
            return Tasks.forException(new IllegalArgumentException("User and user.id must not be null"));
        }

        Log.d(TAG, "Updating user: " + user.getId());

        return db.collection(USERS_COLLECTION)
                .document(user.getId())
                .set(user, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User updated successfully: " + user.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating user: " + user.getId(), e);
                });
    }

    /**
     * Delete a user from the database.
     *
     * Removes a user document from the "users" collection.
     * Note: This does not cascade delete related data (chats, messages, groups).
     * Consider using a Cloud Function for cascading deletes to ensure data consistency.
     *
     * @param userId The unique identifier of the user to delete
     * @return Task<Void> - Task that completes when the user is successfully deleted,
     *         or fails with an exception on error
     *
     * Firestore Path: users/{userId}
     * Operation: Delete document
     */
    @Override
    public Task<Void> deleteUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "Cannot delete user: userId is null or empty");
            return Tasks.forException(new IllegalArgumentException("userId must not be null or empty"));
        }

        Log.d(TAG, "Deleting user: " + userId);

        return db.collection(USERS_COLLECTION)
                .document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User deleted successfully: " + userId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting user: " + userId, e);
                });
    }

    /**
     * Retrieve a user by their email address.
     *
     * Queries the "users" collection to find a user with the specified email.
     * Emails should be unique in the system.
     * Note: This query requires a Firestore index on the email field.
     *
     * @param email The email address of the user to find
     * @return Task<User> - Task that completes with the User object if found,
     *         or fails with an exception if not found or on error
     *
     * Firestore Path: users/ (with WHERE clause)
     * Query: whereEqualTo("email", email)
     * Index Required: Composite index on email field
     */
    @Override
    public Task<User> getUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            Log.e(TAG, "Cannot query user: email is null or empty");
            return Tasks.forException(new IllegalArgumentException("email must not be null or empty"));
        }

        Log.d(TAG, "Querying user by email: " + email);

        return db.collection(USERS_COLLECTION)
                .whereEqualTo("email", email)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        Exception exception = task.getException();
                        Log.e(TAG, "Error querying user by email: " + email, exception);
                        throw exception;
                    }

                    QuerySnapshot querySnapshot = task.getResult();

                    if (querySnapshot.isEmpty()) {
                        Exception notFoundError = new Exception("User not found with email: " + email);
                        Log.e(TAG, "User not found with email: " + email);
                        throw notFoundError;
                    }

                    if (querySnapshot.size() > 1) {
                        Log.w(TAG, "Multiple users found with email: " + email);
                    }

                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    User user = document.toObject(User.class);
                    Log.d(TAG, "User found by email: " + user.getDisplayName());
                    return user;
                });
    }
}

