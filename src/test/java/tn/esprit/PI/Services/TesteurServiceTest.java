package tn.esprit.PI.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.PI.entity.DemandeIntervention;
import tn.esprit.PI.entity.Testeur;
import tn.esprit.PI.entity.TesteurDTO;
import tn.esprit.PI.repository.TesteurRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TesteurServiceTest {

    @Mock
    private TesteurRepository testeurRepository;

    @InjectMocks
    private TesteurService service;

    private Testeur testTesteur;

    @BeforeEach
    void setUp() {
        testTesteur = new Testeur();
        testTesteur.setCodeGMAO("TEST001");
        testTesteur.setAtelier("Atelier A");
        testTesteur.setLigne("Ligne 1");
        testTesteur.setBancTest("Banc 1");
    }

    @Test
    void testCreateTesteur_Success() {
        // Arrange
        when(testeurRepository.save(any(Testeur.class))).thenReturn(testTesteur);

        // Act
        Testeur result = service.createTesteur(testTesteur);

        // Assert
        assertNotNull(result);
        assertEquals("TEST001", result.getCodeGMAO());
        assertEquals("Atelier A", result.getAtelier());
        verify(testeurRepository, times(1)).save(testTesteur);
    }

    @Test
    void testGetAllTesteurs_Success() {
        // Arrange
        List<Testeur> testeurs = Arrays.asList(testTesteur);
        when(testeurRepository.findAll()).thenReturn(testeurs);

        // Act
        List<Testeur> result = service.getAllTesteurs();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TEST001", result.get(0).getCodeGMAO());
        verify(testeurRepository, times(1)).findAll();
    }

    @Test
    void testGetAllTesteurs_Empty() {
        // Arrange
        when(testeurRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Testeur> result = service.getAllTesteurs();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllTesteursDTO_Success() {
        // Arrange
        List<Testeur> testeurs = Arrays.asList(testTesteur);
        when(testeurRepository.findAll()).thenReturn(testeurs);

        // Act
        List<TesteurDTO> result = service.getAllTesteursDTO();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TEST001", result.get(0).getCodeGMAO());
        assertEquals("Atelier A", result.get(0).getAtelier());
        assertEquals("Ligne 1", result.get(0).getLigne());
        verify(testeurRepository, times(1)).findAll();
    }

    @Test
    void testGetAllTesteursDTO_Empty() {
        // Arrange
        when(testeurRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<TesteurDTO> result = service.getAllTesteursDTO();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllTesteursDTO_WithNullTesteur() {
        // Arrange
        List<Testeur> testeurs = Arrays.asList(testTesteur, null);
        when(testeurRepository.findAll()).thenReturn(testeurs);

        // Act
        List<TesteurDTO> result = service.getAllTesteursDTO();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size()); // Null should be filtered out
    }

    @Test
    void testGetAllTesteursDTO_Exception() {
        // Arrange
        when(testeurRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act
        List<TesteurDTO> result = service.getAllTesteursDTO();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTesteurByAtelierAndLigne_Success() {
        // Arrange
        when(testeurRepository.findByAtelierAndLigne("Atelier A", "Ligne 1"))
            .thenReturn(Collections.singletonList(testTesteur));

        // Act
        Optional<Testeur> result = service.getTesteurByAtelierAndLigne("Atelier A", "Ligne 1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("TEST001", result.get().getCodeGMAO());
        verify(testeurRepository, times(1)).findByAtelierAndLigne("Atelier A", "Ligne 1");
    }

    @Test
    void testGetTesteurByAtelierAndLigne_NotFound() {
        // Arrange
        when(testeurRepository.findByAtelierAndLigne("Unknown", "Unknown"))
            .thenReturn(Collections.emptyList());

        // Act
        Optional<Testeur> result = service.getTesteurByAtelierAndLigne("Unknown", "Unknown");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateTesteur_Success_SameCodeGMAO() {
        // Arrange
        Testeur updatedDetails = new Testeur();
        updatedDetails.setCodeGMAO("TEST001"); // Same code
        updatedDetails.setBancTest("Banc 2");

        when(testeurRepository.findByAtelierAndLigne("Atelier A", "Ligne 1"))
            .thenReturn(Collections.singletonList(testTesteur));
        when(testeurRepository.save(any(Testeur.class))).thenReturn(testTesteur);

        // Act
        Testeur result = service.updateTesteur("Atelier A", "Ligne 1", updatedDetails);

        // Assert
        assertNotNull(result);
        verify(testeurRepository, times(1)).save(testTesteur);
        verify(testeurRepository, never()).delete(any());
    }

    @Test
    void testUpdateTesteur_Success_DifferentCodeGMAO() {
        // Arrange
        Testeur updatedDetails = new Testeur();
        updatedDetails.setCodeGMAO("TEST002"); // Different code
        updatedDetails.setBancTest("Banc 2");

        when(testeurRepository.findByAtelierAndLigne("Atelier A", "Ligne 1"))
            .thenReturn(Collections.singletonList(testTesteur));
        when(testeurRepository.save(any(Testeur.class))).thenReturn(testTesteur);

        // Act
        Testeur result = service.updateTesteur("Atelier A", "Ligne 1", updatedDetails);

        // Assert
        assertNotNull(result);
        verify(testeurRepository, times(1)).delete(testTesteur);
        verify(testeurRepository, times(1)).save(any(Testeur.class));
    }

    @Test
    void testUpdateTesteur_NotFound() {
        // Arrange
        Testeur updatedDetails = new Testeur();
        updatedDetails.setCodeGMAO("TEST002");

        when(testeurRepository.findByAtelierAndLigne("Unknown", "Unknown"))
            .thenReturn(Collections.emptyList());

        // Act
        Testeur result = service.updateTesteur("Unknown", "Unknown", updatedDetails);

        // Assert
        assertNull(result);
        verify(testeurRepository, never()).save(any());
        verify(testeurRepository, never()).delete(any());
    }

    @Test
    void testDeleteTesteur_Success() {
        // Arrange
        when(testeurRepository.findByAtelierAndLigne("Atelier A", "Ligne 1"))
            .thenReturn(Collections.singletonList(testTesteur));
        doNothing().when(testeurRepository).delete(testTesteur);

        // Act
        service.deleteTesteur("Atelier A", "Ligne 1");

        // Assert
        verify(testeurRepository, times(1)).delete(testTesteur);
    }

    @Test
    void testDeleteTesteur_NotFound() {
        // Arrange
        when(testeurRepository.findByAtelierAndLigne("Unknown", "Unknown"))
            .thenReturn(Collections.emptyList());

        // Act
        service.deleteTesteur("Unknown", "Unknown");

        // Assert
        verify(testeurRepository, never()).delete(any());
    }
}
