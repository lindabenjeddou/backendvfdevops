package tn.esprit.PI.Services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.PI.entity.*;
import tn.esprit.PI.repository.ComponentRp;
import tn.esprit.PI.repository.ProjectRepository;
import tn.esprit.PI.repository.SousProjetRepository;
import tn.esprit.PI.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SousProjetServiceTest {

    @Mock
    private SousProjetRepository sousProjetRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ComponentRp componentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ComponentService componentService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private StockService stockService;

    @InjectMocks
    private SousProjetService service;

    private SousProjet testSousProjet;
    private SousProjetDto testDto;
    private Project testProject;
    private Component testComponent;
    private User testUser;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setId(1L);
        testProject.setProjectName("Test Project");

        testComponent = new Component();
        testComponent.setTrartArticle("COMP001");
        testComponent.setTrartDesignation("Test Component");
        testComponent.setTrartQuantite("50");

        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstname("Test");
        testUser.setLastname("User");

        testSousProjet = new SousProjet();
        testSousProjet.setId(1L);
        testSousProjet.setSousProjetName("Test Sous-Projet");
        testSousProjet.setDescription("Test Description");
        testSousProjet.setTotalPrice(1000.0);
        testSousProjet.setProject(testProject);
        testSousProjet.setComponents(Collections.singletonList(testComponent));
        testSousProjet.setUsers(Collections.singletonList(testUser));
        testSousProjet.setConfirmed(0);

        testDto = new SousProjetDto();
        testDto.setSousProjetName("Test Sous-Projet");
        testDto.setDescription("Test Description");
        testDto.setTotalPrice(1000.0);
        testDto.setProjectId(1L);
        testDto.setComponents(Collections.singletonList("COMP001"));
        testDto.setUsers(Collections.singletonList(1L));
    }

    @Test
    void testCreateSousProjet_Success() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(componentService.findByTrartArticle("COMP001")).thenReturn(testComponent);
        when(userRepository.findAllById(anyList())).thenReturn(Collections.singletonList(testUser));
        when(sousProjetRepository.save(any(SousProjet.class))).thenReturn(testSousProjet);
        doNothing().when(notificationService).notifyMagasiniersForSousProjetCreation(any());
        doNothing().when(notificationService).notifyMagasiniersForComponentOrder(any(), anyList());
        doNothing().when(stockService).decrementComponentStock(anyList());

        // Act
        SousProjet result = service.createSousProjet(testDto, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Sous-Projet", result.getSousProjetName());
        verify(sousProjetRepository, times(1)).save(any(SousProjet.class));
        verify(notificationService, times(1)).notifyMagasiniersForSousProjetCreation(any());
        verify(stockService, times(1)).decrementComponentStock(anyList());
    }

    @Test
    void testCreateSousProjet_NullTotalPrice() {
        // Arrange
        testDto.setTotalPrice(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.createSousProjet(testDto, 1L));
        assertTrue(exception.getMessage().contains("Total price must be a positive value"));
    }

    @Test
    void testCreateSousProjet_NegativeTotalPrice() {
        // Arrange
        testDto.setTotalPrice(-100.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.createSousProjet(testDto, 1L));
        assertTrue(exception.getMessage().contains("Total price must be a positive value"));
    }

    @Test
    void testCreateSousProjet_ProjectNotFound() {
        // Arrange
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> service.createSousProjet(testDto, 999L));
        assertTrue(exception.getMessage().contains("Project not found"));
    }

    @Test
    void testCreateSousProjet_ComponentNotFound() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(componentService.findByTrartArticle("COMP001")).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> service.createSousProjet(testDto, 1L));
        assertTrue(exception.getMessage().contains("Component not found"));
    }

    @Test
    void testCreateSousProjet_NoUsersFound() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(componentService.findByTrartArticle("COMP001")).thenReturn(testComponent);
        when(userRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> service.createSousProjet(testDto, 1L));
        assertTrue(exception.getMessage().contains("No users found"));
    }

    @Test
    void testCreateSousProjet_NotificationException() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(componentService.findByTrartArticle("COMP001")).thenReturn(testComponent);
        when(userRepository.findAllById(anyList())).thenReturn(Collections.singletonList(testUser));
        when(sousProjetRepository.save(any(SousProjet.class))).thenReturn(testSousProjet);
        doThrow(new RuntimeException("Notification error"))
            .when(notificationService).notifyMagasiniersForSousProjetCreation(any());

        // Act - Should not throw, exception is caught
        SousProjet result = service.createSousProjet(testDto, 1L);

        // Assert
        assertNotNull(result);
        verify(sousProjetRepository, times(1)).save(any(SousProjet.class));
    }

    @Test
    void testCreateSousProjet_StockUpdateException() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(componentService.findByTrartArticle("COMP001")).thenReturn(testComponent);
        when(userRepository.findAllById(anyList())).thenReturn(Collections.singletonList(testUser));
        when(sousProjetRepository.save(any(SousProjet.class))).thenReturn(testSousProjet);
        doThrow(new RuntimeException("Stock error"))
            .when(stockService).decrementComponentStock(anyList());

        // Act - Should not throw, exception is caught
        SousProjet result = service.createSousProjet(testDto, 1L);

        // Assert
        assertNotNull(result);
        verify(sousProjetRepository, times(1)).save(any(SousProjet.class));
    }

    @Test
    void testGetAllSousProjets_Success() {
        // Arrange
        when(sousProjetRepository.findAll()).thenReturn(Collections.singletonList(testSousProjet));

        // Act
        List<SousProjetDto> result = service.getAllSousProjets();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Sous-Projet", result.get(0).getSousProjetName());
        verify(sousProjetRepository, times(1)).findAll();
    }

    @Test
    void testGetAllSousProjets_Empty() {
        // Arrange
        when(sousProjetRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<SousProjetDto> result = service.getAllSousProjets();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateSousProjet_Success() {
        // Arrange
        when(sousProjetRepository.findById(1L)).thenReturn(Optional.of(testSousProjet));
        when(componentRepository.findAllById(anyList()))
            .thenReturn(Collections.singletonList(testComponent));
        when(sousProjetRepository.save(any(SousProjet.class))).thenReturn(testSousProjet);

        SousProjetDto updateDto = new SousProjetDto();
        updateDto.setSousProjetName("Updated Name");
        updateDto.setDescription("Updated Description");
        updateDto.setTotalPrice(2000.0);
        updateDto.setComponents(Collections.singletonList("COMP001"));

        // Act
        SousProjet result = service.updateSousProjet(1L, updateDto);

        // Assert
        assertNotNull(result);
        verify(sousProjetRepository, times(1)).save(any(SousProjet.class));
    }

    @Test
    void testUpdateSousProjet_NotFound() {
        // Arrange
        when(sousProjetRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> service.updateSousProjet(999L, testDto));
        assertTrue(exception.getMessage().contains("SousProjet not found"));
    }

    @Test
    void testDeleteSousProjet_Success() {
        // Arrange
        doNothing().when(sousProjetRepository).deleteById(1L);

        // Act
        service.deleteSousProjet(1L);

        // Assert
        verify(sousProjetRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetSousProjetsByProjectId_Success() {
        // Arrange
        when(sousProjetRepository.findByProjectId(1L))
            .thenReturn(Collections.singletonList(testSousProjet));

        // Act
        List<SousProjet> result = service.getSousProjetsByProjectId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(sousProjetRepository, times(1)).findByProjectId(1L);
    }

    @Test
    void testGetSousProjetById_Success() {
        // Arrange
        when(sousProjetRepository.findById(1L)).thenReturn(Optional.of(testSousProjet));

        // Act
        SousProjet result = service.getSousProjetById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Sous-Projet", result.getSousProjetName());
    }

    @Test
    void testGetSousProjetById_NotFound() {
        // Arrange
        when(sousProjetRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> service.getSousProjetById(999L));
        assertTrue(exception.getMessage().contains("SousProjet not found"));
    }

    @Test
    void testConfirmSousProjetAutomatically_Success() {
        // Arrange
        when(sousProjetRepository.findById(1L)).thenReturn(Optional.of(testSousProjet));
        when(sousProjetRepository.save(any(SousProjet.class))).thenReturn(testSousProjet);

        // Act
        SousProjet result = service.confirmSousProjetAutomatically(1L);

        // Assert
        assertNotNull(result);
        verify(sousProjetRepository, times(1)).save(any(SousProjet.class));
    }

    @Test
    void testConfirmSousProjetAutomatically_NotFound() {
        // Arrange
        when(sousProjetRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> service.confirmSousProjetAutomatically(999L));
        assertTrue(exception.getMessage().contains("SousProjet not found"));
    }
}
