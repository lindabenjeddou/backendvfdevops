package tn.esprit.PI.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.PI.entity.*;
import tn.esprit.PI.repository.DemandeInterventionRepository;
import tn.esprit.PI.repository.TesteurRepository;
import tn.esprit.PI.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemandeInterventionServiceTest {

    @Mock
    private DemandeInterventionRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TesteurRepository testeurRepository;

    @InjectMocks
    private DemandeInterventionService service;

    private Curative testDemande;
    private User testUser;
    private User testTechnicien;
    private Testeur testTesteur;

    @BeforeEach
    void setUp() {
        // Setup User (demandeur)
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("demandeur");

        // Setup Technicien
        testTechnicien = new User();
        testTechnicien.setId(2L);
        testTechnicien.setUsername("technicien");

        // Setup Testeur
        testTesteur = new Testeur();
        testTesteur.setCodeGMAO("TEST001");

        // Setup Curative (DemandeIntervention)
        testDemande = new Curative();
        testDemande.setId(1L);
        testDemande.setDescription("Test Description");
        testDemande.setDateDemande(new Date());
        testDemande.setStatut(StatutDemande.EN_ATTENTE);
        testDemande.setPriorite("HAUTE");
        testDemande.setDemandeur(testUser);
        testDemande.setType_demande("CURATIVE");
        testDemande.setDateCreation(new Date());
        testDemande.setDateValidation(new Date());
        testDemande.setConfirmation(1);
        testDemande.setTesteur(testTesteur);
        testDemande.setTechnicienAssigne(testTechnicien);
        testDemande.setPanne("Panne test");
        testDemande.setUrgence(true);
    }

    @Test
    void testGetDemandeById_Success() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(testDemande));

        // Act
        Optional<DemandeInterventionDTO> result = service.getDemandeById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Test Description", result.get().getDescription());
        assertEquals(StatutDemande.EN_ATTENTE, result.get().getStatut());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testGetDemandeById_NotFound() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<DemandeInterventionDTO> result = service.getDemandeById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetDemandeById_Exception() {
        // Arrange
        when(repository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        // Act
        Optional<DemandeInterventionDTO> result = service.getDemandeById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetAllDemandes_Success() {
        // Arrange
        when(repository.findAll()).thenReturn(Collections.singletonList(testDemande));

        // Act
        List<DemandeInterventionDTO> result = service.getAllDemandes();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetAllDemandes_Exception() {
        // Arrange
        when(repository.findAll()).thenThrow(new RuntimeException("Error"));

        // Act
        List<DemandeInterventionDTO> result = service.getAllDemandes();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetDemandesByTechnicien_Success() {
        // Arrange
        when(repository.findAll()).thenReturn(Collections.singletonList(testDemande));

        // Act
        List<DemandeInterventionDTO> result = service.getDemandesByTechnicien(2L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getTechnicienAssigneId());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetByTechnicien_Success() {
        // Arrange
        when(repository.findAll()).thenReturn(Collections.singletonList(testDemande));

        // Act
        List<DemandeInterventionDTO> result = service.getByTechnicien(2L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testAssignTechnicianToIntervention_Success() {
        // Arrange
        when(userRepository.existsById(3L)).thenReturn(true);
        when(repository.assignTechnicianNative(1L, 3L)).thenReturn(1);
        when(repository.findById(1L)).thenReturn(Optional.of(testDemande));

        // Act
        DemandeInterventionDTO result = service.assignTechnicianToIntervention(1L, 3L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repository, times(1)).assignTechnicianNative(1L, 3L);
    }

    @Test
    void testAssignTechnicianToIntervention_InterventionNotFound() {
        // Arrange
        when(userRepository.existsById(3L)).thenReturn(true);
        when(repository.assignTechnicianNative(999L, 3L)).thenReturn(0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.assignTechnicianToIntervention(999L, 3L));
        assertTrue(exception.getMessage().contains("Aucune ligne mise à jour"));
    }

    @Test
    void testAssignTechnicianToIntervention_TechnicienNotFound() {
        // Arrange
        when(userRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.assignTechnicianToIntervention(1L, 999L));
        assertTrue(exception.getMessage().contains("Technicien non trouvé"));
    }

    @Test
    void testAssignTechnicianToIntervention_NoRowsUpdated() {
        // Arrange
        when(userRepository.existsById(3L)).thenReturn(true);
        when(repository.assignTechnicianNative(1L, 3L)).thenReturn(0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.assignTechnicianToIntervention(1L, 3L));
        assertTrue(exception.getMessage().contains("Aucune ligne mise à jour"));
    }

    @Test
    void testAssignTesteurToIntervention_Success() {
        // Arrange
        when(testeurRepository.existsById("TEST001")).thenReturn(true);
        when(repository.assignTesteurNative(1L, "TEST001")).thenReturn(1);
        when(repository.findById(1L)).thenReturn(Optional.of(testDemande));

        // Act
        DemandeInterventionDTO result = service.assignTesteurToIntervention(1L, "TEST001");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("TEST001", result.getTesteurCodeGMAO());
        verify(repository, times(1)).assignTesteurNative(1L, "TEST001");
    }

    @Test
    void testAssignTesteurToIntervention_TesteurNotFound() {
        // Arrange
        when(testeurRepository.existsById("INVALID")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.assignTesteurToIntervention(1L, "INVALID"));
        assertTrue(exception.getMessage().contains("Testeur/Équipement non trouvé"));
    }

    @Test
    void testConfirmerIntervention_Success() {
        // Arrange
        when(repository.confirmerInterventionNative(1L)).thenReturn(1);
        when(repository.findById(1L)).thenReturn(Optional.of(testDemande));

        // Act
        DemandeInterventionDTO result = service.confirmerIntervention(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repository, times(1)).confirmerInterventionNative(1L);
    }

    @Test
    void testConfirmerIntervention_NotFound() {
        // Arrange
        when(repository.confirmerInterventionNative(999L)).thenReturn(0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.confirmerIntervention(999L));
        assertTrue(exception.getMessage().contains("Aucune ligne mise à jour"));
    }

    @Test
    void testUpdateDemande_Success() {
        // Arrange
        DemandeInterventionDTO dto = new DemandeInterventionDTO();
        dto.setDescription("Updated Description");
        dto.setStatut(StatutDemande.TERMINEE);
        dto.setPriorite("BASSE");
        dto.setTechnicienAssigneId(5L);

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.updateDemandeBasicFields(1L, "Updated Description", "TERMINEE", "BASSE", 5L))
            .thenReturn(1);
        when(repository.findById(1L)).thenReturn(Optional.of(testDemande));

        // Act
        DemandeInterventionDTO result = service.updateDemande(1L, dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repository, times(1)).updateDemandeBasicFields(1L, "Updated Description", "TERMINEE", "BASSE", 5L);
    }

    @Test
    void testUpdateDemande_NotFound() {
        // Arrange
        DemandeInterventionDTO dto = new DemandeInterventionDTO();
        when(repository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.updateDemande(999L, dto));
        assertTrue(exception.getMessage().contains("non trouvée"));
    }

    @Test
    void testUpdateDemande_NoRowsUpdated() {
        // Arrange
        DemandeInterventionDTO dto = new DemandeInterventionDTO();
        dto.setDescription("Test");
        
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.updateDemandeBasicFields(any(), any(), any(), any(), any())).thenReturn(0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.updateDemande(1L, dto));
        assertTrue(exception.getMessage().contains("Aucune ligne mise à jour"));
    }

    @Test
    void testDeleteDemande_Success() {
        // Arrange
        doNothing().when(repository).deleteById(1L);

        // Act
        service.deleteDemande(1L);

        // Assert
        verify(repository, times(1)).deleteById(1L);
    }
}
