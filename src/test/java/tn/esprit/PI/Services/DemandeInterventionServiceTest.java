package tn.esprit.PI.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.PI.entity.DemandeInterventionDTO;
import tn.esprit.PI.entity.StatutDemande;
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

    private Map<String, Object> testRow;
    private List<Map<String, Object>> testRows;

    @BeforeEach
    void setUp() {
        testRow = new HashMap<>();
        testRow.put("id", 1L);
        testRow.put("description", "Test Description");
        testRow.put("date_demande", new Date());
        testRow.put("statut", "EN_ATTENTE");
        testRow.put("priorite", "HAUTE");
        testRow.put("demandeur", 1L);
        testRow.put("type_demande", "CURATIVE");
        testRow.put("date_creation", new Date());
        testRow.put("date_validation", new Date());
        testRow.put("confirmation", 1);
        testRow.put("testeur_code_gmao", "TEST001");
        testRow.put("technicien_id", 2L);
        testRow.put("panne", "Panne test");
        testRow.put("urgence", true);
        testRow.put("frequence", "MENSUELLE");
        testRow.put("prochainrdv", new Date());

        testRows = Collections.singletonList(testRow);
    }

    @Test
    void testGetDemandeById_Success() {
        // Arrange
        when(repository.findAllWithNullSafeDates()).thenReturn(testRows);

        // Act
        Optional<DemandeInterventionDTO> result = service.getDemandeById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Test Description", result.get().getDescription());
        assertEquals(StatutDemande.EN_ATTENTE, result.get().getStatut());
        verify(repository, times(1)).findAllWithNullSafeDates();
    }

    @Test
    void testGetDemandeById_NotFound() {
        // Arrange
        when(repository.findAllWithNullSafeDates()).thenReturn(testRows);

        // Act
        Optional<DemandeInterventionDTO> result = service.getDemandeById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetDemandeById_Exception() {
        // Arrange
        when(repository.findAllWithNullSafeDates()).thenThrow(new RuntimeException("Database error"));

        // Act
        Optional<DemandeInterventionDTO> result = service.getDemandeById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetAllDemandes_Success() {
        // Arrange
        when(repository.findAllWithNullSafeDates()).thenReturn(testRows);

        // Act
        List<DemandeInterventionDTO> result = service.getAllDemandes();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(repository, times(1)).findAllWithNullSafeDates();
    }

    @Test
    void testGetAllDemandes_Exception() {
        // Arrange
        when(repository.findAllWithNullSafeDates()).thenThrow(new RuntimeException("Error"));

        // Act
        List<DemandeInterventionDTO> result = service.getAllDemandes();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetDemandesByTechnicien_Success() {
        // Arrange
        when(repository.findAllByTechnicienIdWithNullSafeDates(2L)).thenReturn(testRows);

        // Act
        List<DemandeInterventionDTO> result = service.getDemandesByTechnicien(2L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getTechnicienAssigneId());
        verify(repository, times(1)).findAllByTechnicienIdWithNullSafeDates(2L);
    }

    @Test
    void testGetByTechnicien_Success() {
        // Arrange
        when(repository.findAllByTechnicienIdWithNullSafeDates(2L)).thenReturn(testRows);

        // Act
        List<DemandeInterventionDTO> result = service.getByTechnicien(2L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testAssignTechnicianToIntervention_Success() {
        // Arrange
        when(repository.findAllWithNullSafeDates()).thenReturn(testRows);
        when(userRepository.existsById(3L)).thenReturn(true);
        when(repository.assignTechnicianNative(1L, 3L)).thenReturn(1);

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
        when(repository.findAllWithNullSafeDates()).thenReturn(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.assignTechnicianToIntervention(999L, 3L));
        assertTrue(exception.getMessage().contains("Intervention non trouvée"));
    }

    @Test
    void testAssignTechnicianToIntervention_TechnicienNotFound() {
        // Arrange
        when(repository.findAllWithNullSafeDates()).thenReturn(testRows);
        when(userRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.assignTechnicianToIntervention(1L, 999L));
        assertTrue(exception.getMessage().contains("Technicien non trouvé"));
    }

    @Test
    void testAssignTechnicianToIntervention_NoRowsUpdated() {
        // Arrange
        when(repository.findAllWithNullSafeDates()).thenReturn(testRows);
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
        when(repository.findAllWithNullSafeDates()).thenReturn(testRows);
        when(testeurRepository.existsById("TEST001")).thenReturn(true);
        when(repository.assignTesteurNative(1L, "TEST001")).thenReturn(1);

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
        when(repository.findAllWithNullSafeDates()).thenReturn(testRows);
        when(testeurRepository.existsById("INVALID")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.assignTesteurToIntervention(1L, "INVALID"));
        assertTrue(exception.getMessage().contains("Testeur/Équipement non trouvé"));
    }

    @Test
    void testConfirmerIntervention_Success() {
        // Arrange
        when(repository.findAllWithNullSafeDates()).thenReturn(testRows);
        when(repository.confirmerInterventionNative(1L)).thenReturn(1);

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
        when(repository.findAllWithNullSafeDates()).thenReturn(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.confirmerIntervention(999L));
        assertTrue(exception.getMessage().contains("Intervention non trouvée"));
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
        when(repository.findAllWithNullSafeDates()).thenReturn(testRows);

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

    @Test
    void testGetAll_Success() {
        // Arrange
        tn.esprit.PI.entity.DemandeIntervention demande = new tn.esprit.PI.entity.Curative();
        demande.setId(1L);
        demande.setDescription("Test");
        when(repository.findAll()).thenReturn(Collections.singletonList(demande));

        // Act
        List<tn.esprit.PI.entity.DemandeIntervention> result = service.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetDemandesByTechnicien_Exception() {
        // Arrange
        when(repository.findAllByTechnicienIdWithNullSafeDates(2L))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        List<DemandeInterventionDTO> result = service.getDemandesByTechnicien(2L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testAssignTesteurToIntervention_NoRowsUpdated() {
        // Arrange
        when(repository.findAllWithNullSafeDates()).thenReturn(testRows);
        when(testeurRepository.existsById("TEST001")).thenReturn(true);
        when(repository.assignTesteurNative(1L, "TEST001")).thenReturn(0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.assignTesteurToIntervention(1L, "TEST001"));
        assertTrue(exception.getMessage().contains("Aucune ligne mise à jour"));
    }

    @Test
    void testConfirmerIntervention_NoRowsUpdated() {
        // Arrange
        when(repository.findAllWithNullSafeDates()).thenReturn(testRows);
        when(repository.confirmerInterventionNative(1L)).thenReturn(0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.confirmerIntervention(1L));
        assertTrue(exception.getMessage().contains("Aucune ligne mise à jour"));
    }

    @Test
    void testMapRowToDTO_WithNullValues() {
        // Arrange
        Map<String, Object> rowWithNulls = new HashMap<>();
        rowWithNulls.put("id", 1L);
        rowWithNulls.put("description", "Test");
        rowWithNulls.put("date_demande", new Date());
        rowWithNulls.put("statut", "EN_ATTENTE");
        rowWithNulls.put("priorite", null);
        rowWithNulls.put("demandeur", null);
        rowWithNulls.put("type_demande", "CURATIVE");
        rowWithNulls.put("date_creation", null);
        rowWithNulls.put("date_validation", null);
        rowWithNulls.put("confirmation", null);
        rowWithNulls.put("testeur_code_gmao", null);
        rowWithNulls.put("technicien_id", null);
        rowWithNulls.put("panne", null);
        rowWithNulls.put("urgence", null);
        rowWithNulls.put("frequence", null);
        rowWithNulls.put("prochainrdv", null);

        when(repository.findAllWithNullSafeDates()).thenReturn(Collections.singletonList(rowWithNulls));

        // Act
        Optional<DemandeInterventionDTO> result = service.getDemandeById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertNull(result.get().getDemandeurId());
        assertNull(result.get().getTechnicienAssigneId());
        assertEquals(0, result.get().getConfirmation());
    }

    @Test
    void testAssignTechnicianToIntervention_GenericException() {
        // Arrange
        when(repository.findAllWithNullSafeDates()).thenReturn(testRows);
        when(userRepository.existsById(3L)).thenReturn(true);
        when(repository.assignTechnicianNative(1L, 3L))
            .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.assignTechnicianToIntervention(1L, 3L));
        assertTrue(exception.getMessage().contains("Erreur lors de l'affectation"));
    }

    @Test
    void testAssignTesteurToIntervention_GenericException() {
        // Arrange
        when(repository.findAllWithNullSafeDates()).thenReturn(testRows);
        when(testeurRepository.existsById("TEST001")).thenReturn(true);
        when(repository.assignTesteurNative(1L, "TEST001"))
            .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.assignTesteurToIntervention(1L, "TEST001"));
        assertTrue(exception.getMessage().contains("Erreur lors de l'affectation du testeur"));
    }

    @Test
    void testConfirmerIntervention_GenericException() {
        // Arrange
        when(repository.findAllWithNullSafeDates()).thenReturn(testRows);
        when(repository.confirmerInterventionNative(1L))
            .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.confirmerIntervention(1L));
        assertTrue(exception.getMessage().contains("Erreur lors de la confirmation"));
    }

    @Test
    void testUpdateDemande_GenericException() {
        // Arrange
        DemandeInterventionDTO dto = new DemandeInterventionDTO();
        dto.setDescription("Test");
        
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.updateDemandeBasicFields(any(), any(), any(), any(), any()))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.updateDemande(1L, dto));
        assertTrue(exception.getMessage().contains("Erreur lors de la mise à jour"));
    }

    @Test
    void testUpdateDemande_WithNullStatut() {
        // Arrange
        DemandeInterventionDTO dto = new DemandeInterventionDTO();
        dto.setDescription("Updated Description");
        dto.setStatut(null);
        dto.setPriorite("BASSE");
        dto.setTechnicienAssigneId(5L);

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.updateDemandeBasicFields(1L, "Updated Description", null, "BASSE", 5L))
            .thenReturn(1);
        when(repository.findAllWithNullSafeDates()).thenReturn(testRows);

        // Act
        DemandeInterventionDTO result = service.updateDemande(1L, dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repository, times(1)).updateDemandeBasicFields(1L, "Updated Description", null, "BASSE", 5L);
    }
}
