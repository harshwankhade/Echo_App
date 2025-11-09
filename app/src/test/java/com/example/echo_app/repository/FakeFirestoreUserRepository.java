package com.example.echo_app.repository;

import com.example.echo_app.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FakeFirestoreUserRepository.java
 *
 * In-memory fake implementation of UserRepository for unit testing.
 *
 * This class simulates Firebase Firestore behavior using local data structures.
 * It implements the UserRepository interface without requiring Firebase SDK.
 *
 * Features:
 * - In-memory HashMap for storing users
 * - Simulates Firestore operations (get, add, update, delete, query)
 * - Returns Task<T> for consistent API with real implementation
 * - Validates input parameters
 * - Supports email-based queries
 *
 * Usage in Tests:
 * FakeFirestoreUserRepository repository = new FakeFirestoreUserRepository();
 * Task<User> userTask = repository.getUserById("uid1");
 * User user = Tasks.await(userTask);
 */
public class FakeFirestoreUserRepository implements UserRepository {

    private final Map<String, User> users = new HashMap<>();

    /**
     * Default constructor.
     * Initializes empty in-memory user store.
     */
    public FakeFirestoreUserRepository() {
    }

    /**
     * Retrieve a user by their unique ID.
     *
     * @param userId The unique identifier of the user
     * @return Task<User> - Task that completes with the User if found, or fails if not found
     */
    @Override
    public Task<User> getUserById(String userId) {
        if (userId == null || userId.isEmpty()) {
            return Tasks.forException(new IllegalArgumentException("userId must not be null or empty"));
        }

        if (users.containsKey(userId)) {
            // Create a copy to simulate Firestore deserialization
            User user = users.get(userId);
            return Tasks.forResult(copyUser(user));
        } else {
            return Tasks.forException(new Exception("User not found: " + userId));
        }
    }

    /**
     * Retrieve all users from the database.
     *
     * @return Task<List<User>> - Task that completes with a list of all users
     */
    @Override
    public Task<List<User>> getAllUsers() {
        List<User> userList = new ArrayList<>();
        for (User user : users.values()) {
            userList.add(copyUser(user));
        }
        return Tasks.forResult(userList);
    }

    /**
     * Add a new user to the database.
     *
     * @param user The User object to add
     * @return Task<Void> - Task that completes when the user is added
     */
    @Override
    public Task<Void> addUser(User user) {
        if (user == null || user.getId() == null || user.getId().isEmpty()) {
            return Tasks.forException(new IllegalArgumentException("User and user.id must not be null"));
        }

        users.put(user.getId(), copyUser(user));
        return Tasks.forResult(null);
    }

    /**
     * Update an existing user's information.
     *
     * @param user The User object with updated information
     * @return Task<Void> - Task that completes when the user is updated
     */
    @Override
    public Task<Void> updateUser(User user) {
        if (user == null || user.getId() == null || user.getId().isEmpty()) {
            return Tasks.forException(new IllegalArgumentException("User and user.id must not be null"));
        }

        if (!users.containsKey(user.getId())) {
            return Tasks.forException(new Exception("User not found: " + user.getId()));
        }

        // Merge update: only update non-null fields
        User existing = users.get(user.getId());
        User merged = mergeUsers(existing, user);
        users.put(user.getId(), merged);
        return Tasks.forResult(null);
    }

    /**
     * Delete a user from the database.
     *
     * @param userId The unique identifier of the user to delete
     * @return Task<Void> - Task that completes when the user is deleted
     */
    @Override
    public Task<Void> deleteUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            return Tasks.forException(new IllegalArgumentException("userId must not be null or empty"));
        }

        if (!users.containsKey(userId)) {
            return Tasks.forException(new Exception("User not found: " + userId));
        }

        users.remove(userId);
        return Tasks.forResult(null);
    }

    /**
     * Retrieve a user by their email address.
     *
     * @param email The email address of the user to find
     * @return Task<User> - Task that completes with the User if found, or fails if not found
     */
    @Override
    public Task<User> getUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return Tasks.forException(new IllegalArgumentException("email must not be null or empty"));
        }

        for (User user : users.values()) {
            if (email.equals(user.getEmail())) {
                return Tasks.forResult(copyUser(user));
            }
        }

        return Tasks.forException(new Exception("User not found with email: " + email));
    }

    /**
     * Create a deep copy of a user to simulate Firestore deserialization.
     *
     * @param user The user to copy
     * @return A new User object with the same data
     */
    private User copyUser(User user) {
        if (user == null) {
            return null;
        }

        User copy = new User(user.getId(), user.getDisplayName(), user.getEmail());
        copy.setProfileImageUrl(user.getProfileImageUrl());
        copy.setOnline(user.isOnline());
        copy.setLastSeen(user.getLastSeen());
        copy.setCreatedAt(user.getCreatedAt());
        copy.setUpdatedAt(user.getUpdatedAt());
        return copy;
    }

    /**
     * Merge two User objects, preferring non-null fields from the update.
     * Simulates Firestore's merge (SetOptions.merge()) behavior.
     *
     * @param existing The existing user
     * @param update The user with updated data
     * @return A merged user object
     */
    private User mergeUsers(User existing, User update) {
        User merged = copyUser(existing);

        if (update.getDisplayName() != null) {
            merged.setDisplayName(update.getDisplayName());
        }
        if (update.getEmail() != null) {
            merged.setEmail(update.getEmail());
        }
        if (update.getProfileImageUrl() != null) {
            merged.setProfileImageUrl(update.getProfileImageUrl());
        }
        // For isOnline, we merge the value (simulating primitive type update)
        merged.setOnline(update.isOnline());

        if (update.getLastSeen() > 0) {
            merged.setLastSeen(update.getLastSeen());
        }
        if (update.getUpdatedAt() > 0) {
            merged.setUpdatedAt(update.getUpdatedAt());
        }

        return merged;
    }

    /**
     * Clear all data (useful for test cleanup).
     */
    public void clear() {
        users.clear();
    }

    /**
     * Get the number of users in the store.
     * Useful for test verification.
     */
    public int getUserCount() {
        return users.size();
    }
}

