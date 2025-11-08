package tn.esprit.PI.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DemandeInterventionDTOTest {

    private DemandeInterventionDTO dto;
    private Date testDate;

    @BeforeEach
    void setUp() {
        testDate = new Date();
        
        dto = new DemandeInterventionDTO();
        dto.setId(1L);
        dto.setDescription("Test Intervention");
        dto.setDateDemande(testDate);
        dto.setStatut(StatutDemande.EN_ATTENTE);
        dto.setDemandeurId(10L);
        dto.setPriorite("HAUTE");
        dto.setTypeDemande("CURATIVE");
        dto.setPanne("Panne test");
        dto.setUrgence(true);
        dto.setFrequence("MENSUELLE");
        dto.setProchainRDV(testDate);
    }

    @Test
    void testDTOCreation() {
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test Intervention", dto.getDescription());
        assertEquals(testDate, dto.getDateDemande());
        assertEquals(StatutDemande.EN_ATTENTE, dto.getStatut());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        DemandeInterventionDTO emptyDTO = new DemandeInterventionDTO();

        // Assert
        assertNotNull(emptyDTO);
        assertNull(emptyDTO.getId());
        assertNull(emptyDTO.getDescription());
        assertNull(emptyDTO.getStatut());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange & Act
        Date date = new Date();
        DemandeInterventionDTO fullDTO = new DemandeInterventionDTO(
                2L,
                "Full Intervention",
                date,
                StatutDemande.EN_COURS,
                "MOYENNE",
                20L,
                "PREVENTIVE",
                null,
                false,
                "HEBDOMADAIRE",
                date
        );

        // Assert
        assertNotNull(fullDTO);
        assertEquals(2L, fullDTO.getId());
        assertEquals("Full Intervention", fullDTO.getDescription());
        assertEquals(StatutDemande.EN_COURS, fullDTO.getStatut());
        assertEquals("PREVENTIVE", fullDTO.getTypeDemande());
        assertFalse(fullDTO.getUrgence());
    }

    @Test
    void testCurativeFields() {
        // Curative-specific fields
        dto.setTypeDemande("CURATIVE");
        dto.setPanne("Machine broken");
        dto.setUrgence(true);

        assertEquals("CURATIVE", dto.getTypeDemande());
        assertEquals("Machine broken", dto.getPanne());
        assertTrue(dto.getUrgence());
    }

    @Test
    void testPreventiveFields() {
        // Preventive-specific fields
        Date rdv = new Date();
        dto.setTypeDemande("PREVENTIVE");
        dto.setFrequence("MENSUELLE");
        dto.setProchainRDV(rdv);

        assertEquals("PREVENTIVE", dto.getTypeDemande());
        assertEquals("MENSUELLE", dto.getFrequence());
        assertEquals(rdv, dto.getProchainRDV());
    }

    @Test
    void testAdditionalFields() {
        // Test additional fields
        Date creation = new Date();
        Date validation = new Date();
        
        dto.setDateCreation(creation);
        dto.setDateValidation(validation);
        dto.setConfirmation(1);
        dto.setTesteurCodeGMAO("TEST001");
        dto.setTechnicienAssigneId(5L);

        assertEquals(creation, dto.getDateCreation());
        assertEquals(validation, dto.getDateValidation());
        assertEquals(1, dto.getConfirmation());
        assertEquals("TEST001", dto.getTesteurCodeGMAO());
        assertEquals(5L, dto.getTechnicienAssigneId());
    }

    @Test
    void testAllStatuts() {
        // Test all possible statuts
        dto.setStatut(StatutDemande.EN_ATTENTE);
        assertEquals(StatutDemande.EN_ATTENTE, dto.getStatut());

        dto.setStatut(StatutDemande.EN_COURS);
        assertEquals(StatutDemande.EN_COURS, dto.getStatut());

        dto.setStatut(StatutDemande.TERMINEE);
        assertEquals(StatutDemande.TERMINEE, dto.getStatut());
    }

    @Test
    void testPriorityValues() {
        // Test different priority values
        String[] priorities = {"HAUTE", "MOYENNE", "BASSE"};
        
        for (String priority : priorities) {
            dto.setPriorite(priority);
            assertEquals(priority, dto.getPriorite());
        }
    }

    @Test
    void testTypeDemandeValues() {
        // Test type demande values
        dto.setTypeDemande("CURATIVE");
        assertEquals("CURATIVE", dto.getTypeDemande());

        dto.setTypeDemande("PREVENTIVE");
        assertEquals("PREVENTIVE", dto.getTypeDemande());

        dto.setTypeDemande("CORRECTIVE");
        assertEquals("CORRECTIVE", dto.getTypeDemande());
    }

    @Test
    void testNullableFields() {
        // Test that fields can be null
        dto.setPanne(null);
        dto.setUrgence(null);
        dto.setFrequence(null);
        dto.setProchainRDV(null);
        dto.setTesteurCodeGMAO(null);
        dto.setTechnicienAssigneId(null);

        assertNull(dto.getPanne());
        assertNull(dto.getUrgence());
        assertNull(dto.getFrequence());
        assertNull(dto.getProchainRDV());
        assertNull(dto.getTesteurCodeGMAO());
        assertNull(dto.getTechnicienAssigneId());
    }

    @Test
    void testSettersAndGetters() {
        // Test all setters and getters
        dto.setId(100L);
        dto.setDescription("Updated");
        dto.setDemandeurId(99L);
        dto.setPriorite("BASSE");

        assertEquals(100L, dto.getId());
        assertEquals("Updated", dto.getDescription());
        assertEquals(99L, dto.getDemandeurId());
        assertEquals("BASSE", dto.getPriorite());
    }

    @Test
    void testUrgenceFlag() {
        // Test urgence boolean
        dto.setUrgence(true);
        assertTrue(dto.getUrgence());

        dto.setUrgence(false);
        assertFalse(dto.getUrgence());
    }

    @Test
    void testDateFields() {
        // Test all date fields
        Date now = new Date();
        Date tomorrow = new Date(now.getTime() + 86400000);

        dto.setDateDemande(now);
        dto.setDateCreation(now);
        dto.setDateValidation(tomorrow);
        dto.setProchainRDV(tomorrow);

        assertEquals(now, dto.getDateDemande());
        assertEquals(now, dto.getDateCreation());
        assertEquals(tomorrow, dto.getDateValidation());
        assertEquals(tomorrow, dto.getProchainRDV());
    }

    @Test
    void testConfirmationValues() {
        // Test confirmation field
        dto.setConfirmation(0);
        assertEquals(0, dto.getConfirmation());

        dto.setConfirmation(1);
        assertEquals(1, dto.getConfirmation());
    }
}
