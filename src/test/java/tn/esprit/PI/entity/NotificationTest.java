package tn.esprit.PI.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    private Notification notification;
    private User recipient;
    private SousProjet sousProjet;

    @BeforeEach
    void setUp() {
        recipient = new User();
        recipient.setId(1L);
        recipient.setEmail("recipient@test.com");

        sousProjet = new SousProjet();
        sousProjet.setId(1L);
        sousProjet.setSousProjetName("Test Sous-Projet");

        notification = new Notification();
        notification.setId(1L);
        notification.setTitle("Test Notification");
        notification.setMessage("This is a test message");
        notification.setType(NotificationType.GENERAL);
        notification.setIsRead(false);
        notification.setPriority("HIGH");
        notification.setStatus("UNREAD");
        notification.setRecipient(recipient);
        notification.setSousProjet(sousProjet);
    }

    @Test
    void testNotificationCreation() {
        assertNotNull(notification);
        assertEquals(1L, notification.getId());
        assertEquals("Test Notification", notification.getTitle());
        assertEquals("This is a test message", notification.getMessage());
        assertEquals(NotificationType.GENERAL, notification.getType());
        assertFalse(notification.getIsRead());
        assertEquals("HIGH", notification.getPriority());
        assertEquals("UNREAD", notification.getStatus());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        Notification emptyNotification = new Notification();

        // Assert
        assertNotNull(emptyNotification);
        assertNull(emptyNotification.getId());
        assertNull(emptyNotification.getTitle());
        assertFalse(emptyNotification.getIsRead()); // Default value
        assertEquals("NORMAL", emptyNotification.getPriority()); // Default value
        assertEquals("UNREAD", emptyNotification.getStatus()); // Default value
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange & Act
        LocalDateTime now = LocalDateTime.now();
        Notification fullNotification = new Notification(
                2L,
                "Full Notification",
                "Full message",
                NotificationType.COMPONENT_ORDER,
                true,
                "LOW",
                "READ",
                now,
                recipient,
                sousProjet
        );

        // Assert
        assertNotNull(fullNotification);
        assertEquals(2L, fullNotification.getId());
        assertEquals("Full Notification", fullNotification.getTitle());
        assertEquals("Full message", fullNotification.getMessage());
        assertEquals(NotificationType.COMPONENT_ORDER, fullNotification.getType());
        assertTrue(fullNotification.getIsRead());
        assertEquals("LOW", fullNotification.getPriority());
        assertEquals("READ", fullNotification.getStatus());
        assertEquals(now, fullNotification.getCreatedAt());
    }

    @Test
    void testDefaultValues() {
        // Arrange
        Notification defaultNotification = new Notification();

        // Assert
        assertFalse(defaultNotification.getIsRead()); // Default false
        assertEquals("NORMAL", defaultNotification.getPriority()); // Default NORMAL
        assertEquals("UNREAD", defaultNotification.getStatus()); // Default UNREAD
    }

    @Test
    void testSetIsRead() {
        // Arrange
        notification.setIsRead(false);

        // Act
        notification.setIsRead(true);

        // Assert
        assertTrue(notification.getIsRead());
    }

    @Test
    void testSetPriority() {
        // Arrange
        String[] priorities = {"HIGH", "NORMAL", "LOW"};

        for (String priority : priorities) {
            // Act
            notification.setPriority(priority);

            // Assert
            assertEquals(priority, notification.getPriority());
        }
    }

    @Test
    void testSetStatus() {
        // Arrange
        String[] statuses = {"UNREAD", "READ", "ARCHIVED"};

        for (String status : statuses) {
            // Act
            notification.setStatus(status);

            // Assert
            assertEquals(status, notification.getStatus());
        }
    }

    @Test
    void testSetType() {
        // Arrange & Act
        notification.setType(NotificationType.STOCK_UPDATE);

        // Assert
        assertEquals(NotificationType.STOCK_UPDATE, notification.getType());
    }

    @Test
    void testRecipientRelationship() {
        // Assert
        assertNotNull(notification.getRecipient());
        assertEquals(recipient.getId(), notification.getRecipient().getId());
        assertEquals(recipient.getEmail(), notification.getRecipient().getEmail());
    }

    @Test
    void testSousProjetRelationship() {
        // Assert
        assertNotNull(notification.getSousProjet());
        assertEquals(sousProjet.getId(), notification.getSousProjet().getId());
        assertEquals(sousProjet.getSousProjetName(), notification.getSousProjet().getSousProjetName());
    }

    @Test
    void testSetCreatedAt() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        notification.setCreatedAt(now);

        // Assert
        assertEquals(now, notification.getCreatedAt());
    }

    @Test
    void testLongMessage() {
        // Arrange
        String longMessage = "A".repeat(1000); // Max length is 1000

        // Act
        notification.setMessage(longMessage);

        // Assert
        assertEquals(1000, notification.getMessage().length());
    }

    @Test
    void testNullSousProjet() {
        // Act
        notification.setSousProjet(null);

        // Assert
        assertNull(notification.getSousProjet());
    }

    @Test
    void testAllNotificationTypes() {
        // Test all enum values
        NotificationType[] types = NotificationType.values();
        
        for (NotificationType type : types) {
            // Act
            notification.setType(type);

            // Assert
            assertEquals(type, notification.getType());
        }
    }

    @Test
    void testSetTitle() {
        // Arrange
        String newTitle = "Updated Title";

        // Act
        notification.setTitle(newTitle);

        // Assert
        assertEquals(newTitle, notification.getTitle());
    }

    @Test
    void testSetMessage() {
        // Arrange
        String newMessage = "Updated message content";

        // Act
        notification.setMessage(newMessage);

        // Assert
        assertEquals(newMessage, notification.getMessage());
    }

    @Test
    void testMultipleNotificationStatuses() {
        // Test transition through statuses
        notification.setStatus("UNREAD");
        assertEquals("UNREAD", notification.getStatus());

        notification.setStatus("READ");
        assertEquals("READ", notification.getStatus());

        notification.setStatus("ARCHIVED");
        assertEquals("ARCHIVED", notification.getStatus());
    }
}
