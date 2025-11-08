package tn.esprit.PI.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.PI.entity.*;
import tn.esprit.PI.repository.NotificationRepository;
import tn.esprit.PI.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService service;

    private User testMagasinier1;
    private User testMagasinier2;
    private SousProjet testSousProjet;
    private Component testComponent;
    private List<User> magasiniers;
    private Notification savedNotification;

    @BeforeEach
    void setUp() {
        testMagasinier1 = new User();
        testMagasinier1.setId(1L);
        testMagasinier1.setFirstname("Magasinier");
        testMagasinier1.setLastname("One");
        testMagasinier1.setRole(UserRole.MAGASINIER);

        testMagasinier2 = new User();
        testMagasinier2.setId(2L);
        testMagasinier2.setFirstname("Magasinier");
        testMagasinier2.setLastname("Two");
        testMagasinier2.setRole(UserRole.MAGASINIER);

        magasiniers = Arrays.asList(testMagasinier1, testMagasinier2);

        testComponent = new Component();
        testComponent.setTrartArticle("COMP001");
        testComponent.setTrartDesignation("Test Component");

        testSousProjet = new SousProjet();
        testSousProjet.setId(1L);
        testSousProjet.setSousProjetName("Test Sous-Projet");
        testSousProjet.setComponents(Collections.singletonList(testComponent));

        savedNotification = new Notification();
        savedNotification.setId(1L);
        savedNotification.setTitle("Test Notification");
        savedNotification.setMessage("Test Message");
    }

    @Test
    void testNotifyMagasiniersForSousProjetCreation_Success() {
        // Arrange
        when(userRepository.findByRole(UserRole.MAGASINIER)).thenReturn(magasiniers);
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // Act
        service.notifyMagasiniersForSousProjetCreation(testSousProjet);

        // Assert
        verify(userRepository, times(1)).findByRole(UserRole.MAGASINIER);
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void testNotifyMagasiniersForSousProjetCreation_NoMagasiniers() {
        // Arrange
        when(userRepository.findByRole(UserRole.MAGASINIER)).thenReturn(Collections.emptyList());

        // Act
        service.notifyMagasiniersForSousProjetCreation(testSousProjet);

        // Assert
        verify(userRepository, times(1)).findByRole(UserRole.MAGASINIER);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void testNotifyMagasiniersForSousProjetCreation_NullMagasiniers() {
        // Arrange
        when(userRepository.findByRole(UserRole.MAGASINIER)).thenReturn(null);

        // Act
        service.notifyMagasiniersForSousProjetCreation(testSousProjet);

        // Assert
        verify(userRepository, times(1)).findByRole(UserRole.MAGASINIER);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void testNotifyMagasiniersForSousProjetCreation_WithException() {
        // Arrange
        when(userRepository.findByRole(UserRole.MAGASINIER)).thenReturn(magasiniers);
        when(notificationRepository.save(any(Notification.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act - Should not throw exception
        assertDoesNotThrow(() -> service.notifyMagasiniersForSousProjetCreation(testSousProjet));

        // Assert
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void testNotifyMagasiniersForComponentOrder_Success() {
        // Arrange
        List<Component> components = Collections.singletonList(testComponent);
        when(userRepository.findByRole(UserRole.MAGASINIER)).thenReturn(magasiniers);
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // Act
        service.notifyMagasiniersForComponentOrder(testSousProjet, components);

        // Assert
        verify(userRepository, times(1)).findByRole(UserRole.MAGASINIER);
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void testNotifyMagasiniersForComponentOrder_EmptyComponents() {
        // Arrange
        List<Component> emptyComponents = Collections.emptyList();
        when(userRepository.findByRole(UserRole.MAGASINIER)).thenReturn(magasiniers);
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // Act
        service.notifyMagasiniersForComponentOrder(testSousProjet, emptyComponents);

        // Assert
        verify(userRepository, times(1)).findByRole(UserRole.MAGASINIER);
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }
}
