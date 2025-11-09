package com.example.echo_app.repository;

import static org.junit.Assert.*;

import com.example.echo_app.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * FirestoreUserRepositoryTest.java
 *
 * Unit tests for FirestoreUserRepository using local fakes and in-memory objects.
 *
 * Uses FakeFirestoreUserRepository (in-memory implementation) to test without Firebase.
 * Tests all CRUD operations and edge cases.
 *
 * Test Coverage:
 * - getUserById: success and not found scenarios
 * - getAllUsers: empty and populated lists
 * - addUser: new user creation and validation
 * - updateUser: field updates and validation
 * - deleteUser: successful deletion and validation
 * - getUserByEmail: email query and edge cases
 */
public class FirestoreUserRepositoryTest {

    private FakeFirestoreUserRepository userRepository;

    @Before
    public void setUp() {
        userRepository = new FakeFirestoreUserRepository();
    }

    // ═════════════════════════════════════════════════════════════════��═══════════════
    // Test: getUserById
    // ═════════════════════════════════════════════════════════════════════════════════

    @Test
    public void testGetUserById_Success() throws ExecutionException, InterruptedException {
        // Arrange
        User expectedUser = new User("uid1", "John Doe", "john@example.com");
        userRepository.addUser(expectedUser).await();

        // Act
        Task<User> task = userRepository.getUserById("uid1");
        User actualUser = Tasks.await(task);

        // Assert
        assertNotNull(actualUser);
        assertEquals("uid1", actualUser.getId());
        assertEquals("John Doe", actualUser.getDisplayName());
        assertEquals("john@example.com", actualUser.getEmail());
    }

    @Test
    public void testGetUserById_NotFound() throws ExecutionException, InterruptedException {
        // Act & Assert
        Task<User> task = userRepository.getUserById("nonexistent");
        try {
            Tasks.await(task);
            fail("Should throw exception for non-existent user");
        } catch (ExecutionException e) {
            assertTrue(e.getMessage().contains("not found") || e.getCause() != null);
        }
    }

    @Test
    public void testGetUserById_NullUserId() throws ExecutionException, InterruptedException {
        // Act & Assert
        Task<User> task = userRepository.getUserById(null);
        try {
            Tasks.await(task);
            fail("Should throw exception for null userId");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testGetUserById_EmptyUserId() throws ExecutionException, InterruptedException {
        // Act & Assert
        Task<User> task = userRepository.getUserById("");
        try {
            Tasks.await(task);
            fail("Should throw exception for empty userId");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════════════
    // Test: getAllUsers
    // ═════════════════════════════════════════════════════════════════════════════════

    @Test
    public void testGetAllUsers_Empty() throws ExecutionException, InterruptedException {
        // Act
        Task<List<User>> task = userRepository.getAllUsers();
        List<User> users = Tasks.await(task);

        // Assert
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    public void testGetAllUsers_MultipleUsers() throws ExecutionException, InterruptedException {
        // Arrange
        User user1 = new User("uid1", "John Doe", "john@example.com");
        User user2 = new User("uid2", "Jane Smith", "jane@example.com");
        User user3 = new User("uid3", "Bob Johnson", "bob@example.com");

        userRepository.addUser(user1).await();
        userRepository.addUser(user2).await();
        userRepository.addUser(user3).await();

        // Act
        Task<List<User>> task = userRepository.getAllUsers();
        List<User> users = Tasks.await(task);

        // Assert
        assertNotNull(users);
        assertEquals(3, users.size());
    }

    // ═════════════════════════════════════════════════════════════════════════════════
    // Test: addUser
    // ═════════════════════════════════════════════════════════════════════════════════

    @Test
    public void testAddUser_Success() throws ExecutionException, InterruptedException {
        // Arrange
        User newUser = new User("uid1", "John Doe", "john@example.com");

        // Act
        Task<Void> task = userRepository.addUser(newUser);
        Tasks.await(task);

        // Assert - verify user was added
        User retrievedUser = Tasks.await(userRepository.getUserById("uid1"));
        assertNotNull(retrievedUser);
        assertEquals("John Doe", retrievedUser.getDisplayName());
    }

    @Test
    public void testAddUser_NullUser() throws ExecutionException, InterruptedException {
        // Act & Assert
        Task<Void> task = userRepository.addUser(null);
        try {
            Tasks.await(task);
            fail("Should throw exception for null user");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testAddUser_NullUserId() throws ExecutionException, InterruptedException {
        // Arrange
        User userWithoutId = new User("", "John Doe", "john@example.com");

        // Act & Assert
        Task<Void> task = userRepository.addUser(userWithoutId);
        try {
            Tasks.await(task);
            fail("Should throw exception for user with empty id");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════════════
    // Test: updateUser
    // ═══���═════════════════════════════════════════════════════════════════════════════

    @Test
    public void testUpdateUser_Success() throws ExecutionException, InterruptedException {
        // Arrange
        User user = new User("uid1", "John Doe", "john@example.com");
        userRepository.addUser(user).await();

        // Act - update user
        User updatedUser = new User("uid1", "John Updated", "john@example.com");
        updatedUser.setOnline(true);
        Task<Void> task = userRepository.updateUser(updatedUser);
        Tasks.await(task);

        // Assert - verify update
        User retrievedUser = Tasks.await(userRepository.getUserById("uid1"));
        assertEquals("John Updated", retrievedUser.getDisplayName());
        assertTrue(retrievedUser.isOnline());
    }

    @Test
    public void testUpdateUser_NullUser() throws ExecutionException, InterruptedException {
        // Act & Assert
        Task<Void> task = userRepository.updateUser(null);
        try {
            Tasks.await(task);
            fail("Should throw exception for null user");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testUpdateUser_PartialUpdate() throws ExecutionException, InterruptedException {
        // Arrange
        User user = new User("uid1", "John Doe", "john@example.com");
        user.setOnline(false);
        userRepository.addUser(user).await();

        // Act - update only online status
        User updateData = new User("uid1", null, null);
        updateData.setOnline(true);
        Task<Void> task = userRepository.updateUser(updateData);
        Tasks.await(task);

        // Assert - verify only online status changed
        User retrievedUser = Tasks.await(userRepository.getUserById("uid1"));
        assertTrue(retrievedUser.isOnline());
        assertEquals("John Doe", retrievedUser.getDisplayName()); // Should remain unchanged
    }

    // ═════════════════════════════════════════════════════════════════════════════════
    // Test: deleteUser
    // ═════════════════════════════════════════════════════════════════════════════════

    @Test
    public void testDeleteUser_Success() throws ExecutionException, InterruptedException {
        // Arrange
        User user = new User("uid1", "John Doe", "john@example.com");
        userRepository.addUser(user).await();

        // Act
        Task<Void> task = userRepository.deleteUser("uid1");
        Tasks.await(task);

        // Assert - verify user was deleted
        Task<User> getTask = userRepository.getUserById("uid1");
        try {
            Tasks.await(getTask);
            fail("User should not exist after deletion");
        } catch (ExecutionException e) {
            assertTrue(e.getMessage().contains("not found") || e.getCause() != null);
        }
    }

    @Test
    public void testDeleteUser_NullUserId() throws ExecutionException, InterruptedException {
        // Act & Assert
        Task<Void> task = userRepository.deleteUser(null);
        try {
            Tasks.await(task);
            fail("Should throw exception for null userId");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testDeleteUser_EmptyUserId() throws ExecutionException, InterruptedException {
        // Act & Assert
        Task<Void> task = userRepository.deleteUser("");
        try {
            Tasks.await(task);
            fail("Should throw exception for empty userId");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════════════
    // Test: getUserByEmail
    // ═════════════════════════════════════════════════════════════════════════════════

    @Test
    public void testGetUserByEmail_Success() throws ExecutionException, InterruptedException {
        // Arrange
        User user = new User("uid1", "John Doe", "john@example.com");
        userRepository.addUser(user).await();

        // Act
        Task<User> task = userRepository.getUserByEmail("john@example.com");
        User retrievedUser = Tasks.await(task);

        // Assert
        assertNotNull(retrievedUser);
        assertEquals("uid1", retrievedUser.getId());
        assertEquals("John Doe", retrievedUser.getDisplayName());
    }

    @Test
    public void testGetUserByEmail_NotFound() throws ExecutionException, InterruptedException {
        // Act & Assert
        Task<User> task = userRepository.getUserByEmail("nonexistent@example.com");
        try {
            Tasks.await(task);
            fail("Should throw exception for non-existent email");
        } catch (ExecutionException e) {
            assertTrue(e.getMessage().contains("not found") || e.getCause() != null);
        }
    }

    @Test
    public void testGetUserByEmail_NullEmail() throws ExecutionException, InterruptedException {
        // Act & Assert
        Task<User> task = userRepository.getUserByEmail(null);
        try {
            Tasks.await(task);
            fail("Should throw exception for null email");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testGetUserByEmail_EmptyEmail() throws ExecutionException, InterruptedException {
        // Act & Assert
        Task<User> task = userRepository.getUserByEmail("");
        try {
            Tasks.await(task);
            fail("Should throw exception for empty email");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testGetUserByEmail_CaseSensitivity() throws ExecutionException, InterruptedException {
        // Arrange
        User user = new User("uid1", "John Doe", "john@example.com");
        userRepository.addUser(user).await();

        // Act - try different case
        Task<User> task = userRepository.getUserByEmail("JOHN@EXAMPLE.COM");

        // Assert - should not find (case sensitive)
        try {
            Tasks.await(task);
            fail("Should not find user with different case email");
        } catch (ExecutionException e) {
            // Expected behavior
        }
    }

    // ═════════════════════════════════════════════════════════════════════════════════
    // Integration Tests
    // ═════════════════════════════════════════════════════════════════════════════════

    @Test
    public void testUserLifecycle() throws ExecutionException, InterruptedException {
        // Create user
        User newUser = new User("uid1", "John Doe", "john@example.com");
        newUser.setProfileImageUrl("https://example.com/image.jpg");
        userRepository.addUser(newUser).await();

        // Retrieve and verify
        User retrievedUser = Tasks.await(userRepository.getUserById("uid1"));
        assertEquals("John Doe", retrievedUser.getDisplayName());

        // Update user
        retrievedUser.setOnline(true);
        retrievedUser.setLastSeen(System.currentTimeMillis());
        userRepository.updateUser(retrievedUser).await();

        // Verify update
        User updatedUser = Tasks.await(userRepository.getUserById("uid1"));
        assertTrue(updatedUser.isOnline());

        // Delete user
        userRepository.deleteUser("uid1").await();

        // Verify deletion
        try {
            Tasks.await(userRepository.getUserById("uid1"));
            fail("User should be deleted");
        } catch (ExecutionException e) {
            // Expected
        }
    }

    @Test
    public void testMultipleUsers() throws ExecutionException, InterruptedException {
        // Add multiple users
        for (int i = 1; i <= 5; i++) {
            User user = new User("uid" + i, "User " + i, "user" + i + "@example.com");
            userRepository.addUser(user).await();
        }

        // Verify all users exist
        List<User> allUsers = Tasks.await(userRepository.getAllUsers());
        assertEquals(5, allUsers.size());

        // Verify individual retrieval
        User user3 = Tasks.await(userRepository.getUserById("uid3"));
        assertEquals("User 3", user3.getDisplayName());

        // Verify email search
        User userByEmail = Tasks.await(userRepository.getUserByEmail("user3@example.com"));
        assertEquals("uid3", userByEmail.getId());
    }
}

