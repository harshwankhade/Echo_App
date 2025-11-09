package com.example.echo_app.repository;

import static org.junit.Assert.*;

import com.example.echo_app.model.Message;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * FirestoreMessageRepositoryTest.java
 *
 * Unit tests for FirestoreMessageRepository using local fakes and in-memory objects.
 *
 * Uses FakeFirestoreMessageRepository (in-memory implementation) to test without Firebase.
 * Tests all message CRUD operations and edge cases.
 *
 * Test Coverage:
 * - getMessagesByChatId: empty chats, message retrieval, ordering
 * - sendMessage: new message creation, validation
 * - updateMessageStatus: status progression, validation
 * - deleteMessage: message deletion, validation
 */
public class FirestoreMessageRepositoryTest {

    private FakeFirestoreMessageRepository messageRepository;

    @Before
    public void setUp() {
        messageRepository = new FakeFirestoreMessageRepository();
    }

    // ═════════════════════════════════════════════════════════════════════════════════
    // Test: getMessagesByChatId
    // ═════════════════════════════════════════════════════════════════════════════════

    @Test
    public void testGetMessagesByChatId_EmptyChat() throws ExecutionException, InterruptedException {
        // Act
        Task<List<Message>> task = messageRepository.getMessagesByChatId("chat1");
        List<Message> messages = Tasks.await(task);

        // Assert
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    public void testGetMessagesByChatId_SingleMessage() throws ExecutionException, InterruptedException {
        // Arrange
        Message message = new Message("msg1", "user1", "chat1", "Hello!", "text");
        messageRepository.sendMessage(message).await();

        // Act
        Task<List<Message>> task = messageRepository.getMessagesByChatId("chat1");
        List<Message> messages = Tasks.await(task);

        // Assert
        assertNotNull(messages);
        assertEquals(1, messages.size());
        assertEquals("Hello!", messages.get(0).getContent());
    }

    @Test
    public void testGetMessagesByChatId_MultipleMessages() throws ExecutionException, InterruptedException {
        // Arrange
        for (int i = 0; i < 5; i++) {
            Message message = new Message("msg" + i, "user1", "chat1", "Message " + i, "text");
            message.setTimestamp(System.currentTimeMillis() + i * 1000);
            messageRepository.sendMessage(message).await();
        }

        // Act
        Task<List<Message>> task = messageRepository.getMessagesByChatId("chat1");
        List<Message> messages = Tasks.await(task);

        // Assert
        assertEquals(5, messages.size());
    }

    @Test
    public void testGetMessagesByChatId_NullChatId() throws ExecutionException, InterruptedException {
        // Act & Assert
        Task<List<Message>> task = messageRepository.getMessagesByChatId(null);
        try {
            Tasks.await(task);
            fail("Should throw exception for null chatId");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testGetMessagesByChatId_EmptyChatId() throws ExecutionException, InterruptedException {
        // Act & Assert
        Task<List<Message>> task = messageRepository.getMessagesByChatId("");
        try {
            Tasks.await(task);
            fail("Should throw exception for empty chatId");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testGetMessagesByChatId_OrderedByTimestamp() throws ExecutionException, InterruptedException {
        // Arrange - add messages out of order
        Message msg3 = new Message("msg3", "user1", "chat1", "Third", "text");
        msg3.setTimestamp(3000);
        Message msg1 = new Message("msg1", "user1", "chat1", "First", "text");
        msg1.setTimestamp(1000);
        Message msg2 = new Message("msg2", "user1", "chat1", "Second", "text");
        msg2.setTimestamp(2000);

        messageRepository.sendMessage(msg3).await();
        messageRepository.sendMessage(msg1).await();
        messageRepository.sendMessage(msg2).await();

        // Act
        Task<List<Message>> task = messageRepository.getMessagesByChatId("chat1");
        List<Message> messages = Tasks.await(task);

        // Assert - should be ordered by timestamp
        assertEquals(3, messages.size());
        assertEquals(1000, messages.get(0).getTimestamp());
        assertEquals(2000, messages.get(1).getTimestamp());
        assertEquals(3000, messages.get(2).getTimestamp());
    }

    // ═════════════════════════════════════════════════════════════════════════════════
    // Test: sendMessage
    // ═════════════════════════════════════════════════════════════════════════════════

    @Test
    public void testSendMessage_Success() throws ExecutionException, InterruptedException {
        // Arrange
        Message message = new Message("msg1", "user1", "chat1", "Hello!", "text");
        message.setDeliveryStatus("sent");

        // Act
        Task<Void> task = messageRepository.sendMessage(message);
        Tasks.await(task);

        // Assert - verify message was sent
        List<Message> messages = Tasks.await(messageRepository.getMessagesByChatId("chat1"));
        assertEquals(1, messages.size());
        assertEquals("Hello!", messages.get(0).getContent());
    }

    @Test
    public void testSendMessage_TextMessage() throws ExecutionException, InterruptedException {
        // Arrange
        Message message = new Message("msg1", "user1", "chat1", "Hello!", "text");
        message.setDeliveryStatus("sent");

        // Act
        Task<Void> task = messageRepository.sendMessage(message);
        Tasks.await(task);

        // Assert
        List<Message> messages = Tasks.await(messageRepository.getMessagesByChatId("chat1"));
        Message retrieved = messages.get(0);
        assertEquals("text", retrieved.getMessageType());
        assertNull(retrieved.getMediaUrl());
    }

    @Test
    public void testSendMessage_ImageMessage() throws ExecutionException, InterruptedException {
        // Arrange
        Message message = new Message("msg1", "user1", "chat1", "", "image");
        message.setMediaUrl("https://example.com/image.jpg");
        message.setDeliveryStatus("sent");

        // Act
        Task<Void> task = messageRepository.sendMessage(message);
        Tasks.await(task);

        // Assert
        List<Message> messages = Tasks.await(messageRepository.getMessagesByChatId("chat1"));
        Message retrieved = messages.get(0);
        assertEquals("image", retrieved.getMessageType());
        assertEquals("https://example.com/image.jpg", retrieved.getMediaUrl());
    }

    @Test
    public void testSendMessage_NullMessage() throws ExecutionException, InterruptedException {
        // Act & Assert
        Task<Void> task = messageRepository.sendMessage(null);
        try {
            Tasks.await(task);
            fail("Should throw exception for null message");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testSendMessage_NullChatId() throws ExecutionException, InterruptedException {
        // Arrange
        Message message = new Message("msg1", "user1", null, "Hello!", "text");

        // Act & Assert
        Task<Void> task = messageRepository.sendMessage(message);
        try {
            Tasks.await(task);
            fail("Should throw exception for null chatId");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════════════
    // Test: updateMessageStatus
    // ═════════════════════════════════════════════════════════════════════════════════

    @Test
    public void testUpdateMessageStatus_SentToDelivered() throws ExecutionException, InterruptedException {
        // Arrange
        Message message = new Message("msg1", "user1", "chat1", "Hello!", "text");
        message.setDeliveryStatus("sent");
        messageRepository.sendMessage(message).await();

        // Act - update to delivered
        Task<Void> task = messageRepository.updateMessageStatus("chat1/msg1", "delivered");
        Tasks.await(task);

        // Assert
        List<Message> messages = Tasks.await(messageRepository.getMessagesByChatId("chat1"));
        assertEquals("delivered", messages.get(0).getDeliveryStatus());
    }

    @Test
    public void testUpdateMessageStatus_FullProgression() throws ExecutionException, InterruptedException {
        // Arrange
        Message message = new Message("msg1", "user1", "chat1", "Hello!", "text");
        message.setDeliveryStatus("sent");
        messageRepository.sendMessage(message).await();

        // Act - sent → delivered
        messageRepository.updateMessageStatus("chat1/msg1", "delivered").await();
        List<Message> messages1 = Tasks.await(messageRepository.getMessagesByChatId("chat1"));
        assertEquals("delivered", messages1.get(0).getDeliveryStatus());

        // Act - delivered → seen
        messageRepository.updateMessageStatus("chat1/msg1", "seen").await();
        List<Message> messages2 = Tasks.await(messageRepository.getMessagesByChatId("chat1"));
        assertEquals("seen", messages2.get(0).getDeliveryStatus());
    }

    @Test
    public void testUpdateMessageStatus_NullMessageId() throws ExecutionException, InterruptedException {
        // Act & Assert
        Task<Void> task = messageRepository.updateMessageStatus(null, "delivered");
        try {
            Tasks.await(task);
            fail("Should throw exception for null messageId");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testUpdateMessageStatus_NullStatus() throws ExecutionException, InterruptedException {
        // Act & Assert
        Task<Void> task = messageRepository.updateMessageStatus("chat1/msg1", null);
        try {
            Tasks.await(task);
            fail("Should throw exception for null status");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testUpdateMessageStatus_InvalidMessageIdFormat() throws ExecutionException, InterruptedException {
        // Act & Assert - missing chatId
        Task<Void> task = messageRepository.updateMessageStatus("invalidFormat", "delivered");
        try {
            Tasks.await(task);
            fail("Should throw exception for invalid messageId format");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════════════
    // Test: deleteMessage
    // ═════════════════════════════════════════════════════════════════════════════════

    @Test
    public void testDeleteMessage_Success() throws ExecutionException, InterruptedException {
        // Arrange
        Message message = new Message("msg1", "user1", "chat1", "Hello!", "text");
        messageRepository.sendMessage(message).await();

        // Verify message exists
        List<Message> before = Tasks.await(messageRepository.getMessagesByChatId("chat1"));
        assertEquals(1, before.size());

        // Act
        Task<Void> task = messageRepository.deleteMessage("chat1/msg1");
        Tasks.await(task);

        // Assert
        List<Message> after = Tasks.await(messageRepository.getMessagesByChatId("chat1"));
        assertEquals(0, after.size());
    }

    @Test
    public void testDeleteMessage_MultipleMessages() throws ExecutionException, InterruptedException {
        // Arrange
        Message msg1 = new Message("msg1", "user1", "chat1", "First", "text");
        Message msg2 = new Message("msg2", "user1", "chat1", "Second", "text");
        Message msg3 = new Message("msg3", "user1", "chat1", "Third", "text");

        messageRepository.sendMessage(msg1).await();
        messageRepository.sendMessage(msg2).await();
        messageRepository.sendMessage(msg3).await();

        // Act - delete middle message
        messageRepository.deleteMessage("chat1/msg2").await();

        // Assert
        List<Message> messages = Tasks.await(messageRepository.getMessagesByChatId("chat1"));
        assertEquals(2, messages.size());
        assertEquals("msg1", messages.get(0).getId());
        assertEquals("msg3", messages.get(1).getId());
    }

    @Test
    public void testDeleteMessage_NullMessageId() throws ExecutionException, InterruptedException {
        // Act & Assert
        Task<Void> task = messageRepository.deleteMessage(null);
        try {
            Tasks.await(task);
            fail("Should throw exception for null messageId");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testDeleteMessage_InvalidMessageIdFormat() throws ExecutionException, InterruptedException {
        // Act & Assert
        Task<Void> task = messageRepository.deleteMessage("invalidFormat");
        try {
            Tasks.await(task);
            fail("Should throw exception for invalid messageId format");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════════════
    // Integration Tests
    // ═════════════════════════════════════════════════════════════════════════════════

    @Test
    public void testMessageLifecycle() throws ExecutionException, InterruptedException {
        // Create message
        Message message = new Message("msg1", "user1", "chat1", "Hello!", "text");
        message.setDeliveryStatus("sent");
        message.setTimestamp(System.currentTimeMillis());
        messageRepository.sendMessage(message).await();

        // Retrieve and verify
        List<Message> messages = Tasks.await(messageRepository.getMessagesByChatId("chat1"));
        assertEquals(1, messages.size());
        assertEquals("sent", messages.get(0).getDeliveryStatus());

        // Update status to delivered
        messageRepository.updateMessageStatus("chat1/msg1", "delivered").await();
        messages = Tasks.await(messageRepository.getMessagesByChatId("chat1"));
        assertEquals("delivered", messages.get(0).getDeliveryStatus());

        // Update status to seen
        messageRepository.updateMessageStatus("chat1/msg1", "seen").await();
        messages = Tasks.await(messageRepository.getMessagesByChatId("chat1"));
        assertEquals("seen", messages.get(0).getDeliveryStatus());

        // Delete message
        messageRepository.deleteMessage("chat1/msg1").await();
        messages = Tasks.await(messageRepository.getMessagesByChatId("chat1"));
        assertEquals(0, messages.size());
    }

    @Test
    public void testMultiChatMessages() throws ExecutionException, InterruptedException {
        // Add messages to different chats
        for (int chatNum = 1; chatNum <= 3; chatNum++) {
            for (int msgNum = 1; msgNum <= 3; msgNum++) {
                String chatId = "chat" + chatNum;
                String msgId = "msg_c" + chatNum + "_m" + msgNum;
                Message message = new Message(msgId, "user1", chatId, "Message " + msgNum, "text");
                messageRepository.sendMessage(message).await();
            }
        }

        // Verify each chat has correct messages
        for (int chatNum = 1; chatNum <= 3; chatNum++) {
            String chatId = "chat" + chatNum;
            List<Message> messages = Tasks.await(messageRepository.getMessagesByChatId(chatId));
            assertEquals(3, messages.size());
        }
    }

    @Test
    public void testMessageTypes() throws ExecutionException, InterruptedException {
        // Add different message types
        Message textMsg = new Message("msg1", "user1", "chat1", "Hello", "text");
        Message imageMsg = new Message("msg2", "user1", "chat1", "", "image");
        imageMsg.setMediaUrl("https://example.com/image.jpg");
        Message videoMsg = new Message("msg3", "user1", "chat1", "", "video");
        videoMsg.setMediaUrl("https://example.com/video.mp4");
        Message audioMsg = new Message("msg4", "user1", "chat1", "", "audio");
        audioMsg.setMediaUrl("https://example.com/audio.mp3");

        messageRepository.sendMessage(textMsg).await();
        messageRepository.sendMessage(imageMsg).await();
        messageRepository.sendMessage(videoMsg).await();
        messageRepository.sendMessage(audioMsg).await();

        // Verify all types
        List<Message> messages = Tasks.await(messageRepository.getMessagesByChatId("chat1"));
        assertEquals(4, messages.size());
        assertEquals("text", messages.get(0).getMessageType());
        assertEquals("image", messages.get(1).getMessageType());
        assertEquals("video", messages.get(2).getMessageType());
        assertEquals("audio", messages.get(3).getMessageType());
    }
}

