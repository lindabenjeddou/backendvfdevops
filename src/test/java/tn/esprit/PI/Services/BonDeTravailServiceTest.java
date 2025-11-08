package tn.esprit.PI.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.PI.entity.*;
import tn.esprit.PI.repository.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BonDeTravailServiceTest {

    @Mock
    private BonDeTravailRepository bonDeTravailRepository;

    @Mock
    private UserRepository technicienRepository;

    @Mock
    private ComponentRp composantRepository;

    @Mock
    private DemandeInterventionRepository interventionRepository;

    @Mock
    private TesteurRepository testeurRepository;

    @InjectMocks
    private BonDeTravailService service;

    private BonDeTravail testBon;
    private User testTechnicien;
    private Component testComponent;
    private DemandeIntervention testIntervention;
    private Testeur testTesteur;

    @BeforeEach
    void setUp() {
        testTechnicien = new User();
        testTechnicien.setId(1L);
        testTechnicien.setFirstname("Tech");
        testTechnicien.setLastname("User");

        testComponent = new Component();
        testComponent.setTrartArticle("COMP001");
        testComponent.setTrartQuantite("100");

        testTesteur = new Testeur();
        testTesteur.setCodeGMAO("TEST001");

        testIntervention = new Curative();
        testIntervention.setId(1L);
        testIntervention.setDescription("Test intervention");
        testIntervention.setTesteur(testTesteur);

        testBon = new BonDeTravail();
        testBon.setId(1L);
        testBon.setDescription("Test Bon");
        testBon.setStatut(StatutBonTravail.EN_ATTENTE);
        testBon.setTechnicien(testTechnicien);
        testBon.setIntervention(testIntervention);
        testBon.setTesteur(testTesteur);
        testBon.setComposants(new ArrayList<>());
    }

    @Test
    void testGetAllBonDeTravail_Success() {
        // Arrange
        List<BonDeTravail> bons = Arrays.asList(testBon);
        when(bonDeTravailRepository.findAll()).thenReturn(bons);

        // Act
        List<BonDeTravail> result = service.getAllBonDeTravail();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bonDeTravailRepository, times(1)).findAll();
    }

    @Test
    void testGetBonDeTravailById_Success() {
        // Arrange
        when(bonDeTravailRepository.findById(1L)).thenReturn(Optional.of(testBon));

        // Act
        BonDeTravail result = service.getBonDeTravailById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Bon", result.getDescription());
        verify(bonDeTravailRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBonDeTravailById_NullId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.getBonDeTravailById(null));
        assertTrue(exception.getMessage().contains("ne peut pas être null"));
    }

    @Test
    void testGetBonDeTravailById_NotFound() {
        // Arrange
        when(bonDeTravailRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> service.getBonDeTravailById(999L));
        assertTrue(exception.getMessage().contains("non trouvé"));
    }

    @Test
    void testCreateBonDeTravail_Success() {
        // Arrange
        BonTravailRequest dto = new BonTravailRequest();
        dto.description = "New Bon";
        dto.statut = StatutBonTravail.EN_COURS;
        dto.technicien = 1L;
        dto.interventionId = 1L;
        dto.testeurCodeGMAO = "TEST001";
        
        BonTravailRequest.ComposantQuantite cq = new BonTravailRequest.ComposantQuantite();
        cq.id = "COMP001";
        cq.quantite = 5;
        dto.composants = Collections.singletonList(cq);

        when(technicienRepository.findById(1L)).thenReturn(Optional.of(testTechnicien));
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(testIntervention));
        when(testeurRepository.findById("TEST001")).thenReturn(Optional.of(testTesteur));
        when(composantRepository.findById("COMP001")).thenReturn(Optional.of(testComponent));
        when(composantRepository.save(any(Component.class))).thenReturn(testComponent);
        when(bonDeTravailRepository.save(any(BonDeTravail.class))).thenReturn(testBon);

        // Act
        BonDeTravail result = service.createBonDeTravail(dto);

        // Assert
        assertNotNull(result);
        verify(bonDeTravailRepository, times(1)).save(any(BonDeTravail.class));
        verify(composantRepository, times(1)).save(any(Component.class));
    }

    @Test
    void testCreateBonDeTravail_TechnicienNull() {
        // Arrange
        BonTravailRequest dto = new BonTravailRequest();
        dto.description = "New Bon";
        dto.technicien = null;
        dto.composants = new ArrayList<>();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> service.createBonDeTravail(dto));
    }

    @Test
    void testCreateBonDeTravail_TechnicienNotFound() {
        // Arrange
        BonTravailRequest dto = new BonTravailRequest();
        dto.description = "New Bon";
        dto.technicien = 999L;
        dto.composants = new ArrayList<>();

        when(technicienRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> service.createBonDeTravail(dto));
        assertTrue(exception.getMessage().contains("Technicien non trouvé"));
    }

    @Test
    void testCreateBonDeTravail_InsufficientStock() {
        // Arrange
        BonTravailRequest dto = new BonTravailRequest();
        dto.description = "New Bon";
        dto.technicien = 1L;
        
        BonTravailRequest.ComposantQuantite cq = new BonTravailRequest.ComposantQuantite();
        cq.id = "COMP001";
        cq.quantite = 150; // More than available (100)
        dto.composants = Collections.singletonList(cq);

        when(technicienRepository.findById(1L)).thenReturn(Optional.of(testTechnicien));
        when(composantRepository.findById("COMP001")).thenReturn(Optional.of(testComponent));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> service.createBonDeTravail(dto));
        assertTrue(exception.getMessage().contains("Quantité insuffisante"));
    }

    @Test
    void testCreateBonDeTravailFromIntervention_Success() {
        // Arrange
        BonTravailRequest dto = new BonTravailRequest();
        dto.description = "Intervention Bon";
        dto.statut = StatutBonTravail.EN_COURS;
        dto.composants = new ArrayList<>();

        when(interventionRepository.existsById(1L)).thenReturn(true);
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(testIntervention));
        when(technicienRepository.findById(1L)).thenReturn(Optional.of(testTechnicien));
        when(bonDeTravailRepository.save(any(BonDeTravail.class))).thenReturn(testBon);

        // Act
        BonDeTravail result = service.createBonDeTravailFromIntervention(1L, 1L, dto);

        // Assert
        assertNotNull(result);
        verify(bonDeTravailRepository, times(1)).save(any(BonDeTravail.class));
    }

    @Test
    void testCreateBonDeTravailFromIntervention_InterventionNotFound() {
        // Arrange
        BonTravailRequest dto = new BonTravailRequest();
        when(interventionRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> service.createBonDeTravailFromIntervention(999L, 1L, dto));
        assertTrue(exception.getMessage().contains("Intervention non trouvée"));
    }

    @Test
    void testCreateBonDeTravailFromIntervention_NoTesteur() {
        // Arrange
        testIntervention.setTesteur(null);
        BonTravailRequest dto = new BonTravailRequest();
        
        when(interventionRepository.existsById(1L)).thenReturn(true);
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(testIntervention));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> service.createBonDeTravailFromIntervention(1L, 1L, dto));
        assertTrue(exception.getMessage().contains("testeur"));
    }

    @Test
    void testGetBonsDeTravailByIntervention_Success() {
        // Arrange
        List<BonDeTravail> bons = Arrays.asList(testBon);
        when(bonDeTravailRepository.findByInterventionId(1L)).thenReturn(bons);

        // Act
        List<BonDeTravail> result = service.getBonsDeTravailByIntervention(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bonDeTravailRepository, times(1)).findByInterventionId(1L);
    }

    @Test
    void testGetBonsDeTravailByTesteur_Success() {
        // Arrange
        List<BonDeTravail> bons = Arrays.asList(testBon);
        when(bonDeTravailRepository.findByTesteurCodeGMAO("TEST001")).thenReturn(bons);

        // Act
        List<BonDeTravail> result = service.getBonsDeTravailByTesteur("TEST001");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bonDeTravailRepository, times(1)).findByTesteurCodeGMAO("TEST001");
    }

    @Test
    void testUpdateBonDeTravail_Success() {
        // Arrange
        BonTravailRequest dto = new BonTravailRequest();
        dto.description = "Updated Description";
        dto.statut = StatutBonTravail.TERMINE;

        when(bonDeTravailRepository.findById(1L)).thenReturn(Optional.of(testBon));
        when(bonDeTravailRepository.saveAndFlush(any(BonDeTravail.class))).thenReturn(testBon);

        // Act
        BonDeTravail result = service.updateBonDeTravail(1L, dto);

        // Assert
        assertNotNull(result);
        verify(bonDeTravailRepository, times(1)).saveAndFlush(any(BonDeTravail.class));
    }

    @Test
    void testDeleteBonDeTravail_Success() {
        // Arrange
        when(bonDeTravailRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bonDeTravailRepository).deleteComponentsByBonId(1L);
        doNothing().when(bonDeTravailRepository).deleteById(1L);

        // Act
        service.deleteBonDeTravail(1L);

        // Assert
        verify(bonDeTravailRepository, times(1)).deleteComponentsByBonId(1L);
        verify(bonDeTravailRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteBonDeTravail_NotFound() {
        // Arrange
        when(bonDeTravailRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> service.deleteBonDeTravail(999L));
        assertTrue(exception.getMessage().contains("non trouvé"));
    }
}
