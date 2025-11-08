package tn.esprit.PI.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BonDeTravailTest {

    private BonDeTravail bonDeTravail;
    private User technicien;
    private Testeur testeur;
    private DemandeIntervention intervention;

    @BeforeEach
    void setUp() {
        technicien = new User();
        technicien.setId(1L);
        technicien.setEmail("technicien@test.com");
        technicien.setRole(UserRole.TECHNICIEN_CURATIF);

        testeur = new Testeur();
        testeur.setCodeGMAO("TEST001");
        testeur.setAtelier("Atelier A");

        intervention = new DemandeIntervention();
        intervention.setId(1L);
        intervention.setDescription("Test Intervention");

        bonDeTravail = new BonDeTravail();
        bonDeTravail.setId(1L);
        bonDeTravail.setDescription("Test Bon de Travail");
        bonDeTravail.setDateDebut(LocalDate.now());
        bonDeTravail.setDateFin(LocalDate.now().plusDays(1));
        bonDeTravail.setStatut(StatutBonTravail.EN_COURS);
        bonDeTravail.setTechnicien(technicien);
        bonDeTravail.setIntervention(intervention);
        bonDeTravail.setTesteur(testeur);
    }

    @Test
    void testBonDeTravailCreation() {
        assertNotNull(bonDeTravail);
        assertEquals(1L, bonDeTravail.getId());
        assertEquals("Test Bon de Travail", bonDeTravail.getDescription());
        assertEquals(StatutBonTravail.EN_COURS, bonDeTravail.getStatut());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        BonDeTravail emptyBon = new BonDeTravail();

        // Assert
        assertNotNull(emptyBon);
        assertNull(emptyBon.getId());
        assertNull(emptyBon.getDescription());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange & Act
        List<BonTravailComponent> composants = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        BonDeTravail fullBon = new BonDeTravail(
                2L,
                "Full Description",
                today,
                today,
                today.plusDays(1),
                StatutBonTravail.TERMINE,
                technicien,
                intervention,
                testeur,
                composants
        );

        // Assert
        assertNotNull(fullBon);
        assertEquals(2L, fullBon.getId());
        assertEquals("Full Description", fullBon.getDescription());
        assertEquals(StatutBonTravail.TERMINE, fullBon.getStatut());
        assertEquals(technicien, fullBon.getTechnicien());
    }

    @Test
    void testSetStatut() {
        // Test all statuses
        bonDeTravail.setStatut(StatutBonTravail.EN_ATTENTE);
        assertEquals(StatutBonTravail.EN_ATTENTE, bonDeTravail.getStatut());

        bonDeTravail.setStatut(StatutBonTravail.EN_COURS);
        assertEquals(StatutBonTravail.EN_COURS, bonDeTravail.getStatut());

        bonDeTravail.setStatut(StatutBonTravail.TERMINE);
        assertEquals(StatutBonTravail.TERMINE, bonDeTravail.getStatut());
    }

    @Test
    void testTechnicienRelationship() {
        // Assert
        assertNotNull(bonDeTravail.getTechnicien());
        assertEquals(technicien.getId(), bonDeTravail.getTechnicien().getId());
        assertEquals(technicien.getEmail(), bonDeTravail.getTechnicien().getEmail());
    }

    @Test
    void testInterventionRelationship() {
        // Assert
        assertNotNull(bonDeTravail.getIntervention());
        assertEquals(intervention.getId(), bonDeTravail.getIntervention().getId());
    }

    @Test
    void testTesteurRelationship() {
        // Assert
        assertNotNull(bonDeTravail.getTesteur());
        assertEquals("TEST001", bonDeTravail.getTesteur().getCodeGMAO());
        assertEquals("Atelier A", bonDeTravail.getTesteur().getAtelier());
    }

    @Test
    void testComposantsRelationship() {
        // Arrange
        BonTravailComponent comp1 = new BonTravailComponent();
        comp1.setId(1L);
        comp1.setQuantiteUtilisee(5);

        BonTravailComponent comp2 = new BonTravailComponent();
        comp2.setId(2L);
        comp2.setQuantiteUtilisee(10);

        List<BonTravailComponent> composants = new ArrayList<>();
        composants.add(comp1);
        composants.add(comp2);

        // Act
        bonDeTravail.setComposants(composants);

        // Assert
        assertNotNull(bonDeTravail.getComposants());
        assertEquals(2, bonDeTravail.getComposants().size());
        assertEquals(5, bonDeTravail.getComposants().get(0).getQuantiteUtilisee());
        assertEquals(10, bonDeTravail.getComposants().get(1).getQuantiteUtilisee());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        LocalDate newDate = LocalDate.of(2024, 12, 31);

        // Act
        bonDeTravail.setId(100L);
        bonDeTravail.setDescription("Updated Description");
        bonDeTravail.setDateDebut(newDate);
        bonDeTravail.setDateFin(newDate.plusDays(5));
        bonDeTravail.setStatut(StatutBonTravail.TERMINE);

        // Assert
        assertEquals(100L, bonDeTravail.getId());
        assertEquals("Updated Description", bonDeTravail.getDescription());
        assertEquals(newDate, bonDeTravail.getDateDebut());
        assertEquals(newDate.plusDays(5), bonDeTravail.getDateFin());
        assertEquals(StatutBonTravail.TERMINE, bonDeTravail.getStatut());
    }

    @Test
    void testDateCreationTimestamp() {
        // Note: @CreationTimestamp is handled by Hibernate
        // We can test that the field exists and can be set

        // Arrange
        LocalDate creationDate = LocalDate.now();

        // Act
        bonDeTravail.setDateCreation(creationDate);

        // Assert
        assertEquals(creationDate, bonDeTravail.getDateCreation());
    }

    @Test
    void testNullTechnicien() {
        // Act
        bonDeTravail.setTechnicien(null);

        // Assert
        assertNull(bonDeTravail.getTechnicien());
    }

    @Test
    void testNullIntervention() {
        // Act
        bonDeTravail.setIntervention(null);

        // Assert
        assertNull(bonDeTravail.getIntervention());
    }

    @Test
    void testNullTesteur() {
        // Act
        bonDeTravail.setTesteur(null);

        // Assert
        assertNull(bonDeTravail.getTesteur());
    }

    @Test
    void testEmptyComposants() {
        // Arrange
        List<BonTravailComponent> emptyList = new ArrayList<>();

        // Act
        bonDeTravail.setComposants(emptyList);

        // Assert
        assertNotNull(bonDeTravail.getComposants());
        assertTrue(bonDeTravail.getComposants().isEmpty());
    }

    @Test
    void testDateRange() {
        // Arrange
        LocalDate debut = LocalDate.of(2024, 1, 1);
        LocalDate fin = LocalDate.of(2024, 1, 10);

        // Act
        bonDeTravail.setDateDebut(debut);
        bonDeTravail.setDateFin(fin);

        // Assert
        assertTrue(bonDeTravail.getDateFin().isAfter(bonDeTravail.getDateDebut()));
    }

    @Test
    void testAllStatutBonTravail() {
        // Test all enum values
        StatutBonTravail[] statuts = StatutBonTravail.values();
        
        for (StatutBonTravail statut : statuts) {
            // Act
            bonDeTravail.setStatut(statut);

            // Assert
            assertEquals(statut, bonDeTravail.getStatut());
        }
    }
}
