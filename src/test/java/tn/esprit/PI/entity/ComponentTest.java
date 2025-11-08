package tn.esprit.PI.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComponentTest {

    private Component component;

    @BeforeEach
    void setUp() {
        component = new Component();
        component.setTrartArticle("COMP001");
        component.setTrartDesignation("Test Component");
        component.setTrartFamille("Family A");
        component.setTrartSousFamille("SubFamily 1");
        component.setTrartMarque("Brand X");
        component.setTrartQuantite("100");
        component.setTrartSociete("Company ABC");
        component.setTrartUnite("Unit");
        component.setTrartTransact("Trans");
        component.setTrartTva("20");
        component.setAtl("ATL001");
        component.setBur("BUR001");
        component.setTest("Test");
        component.setPrix("99.99");
    }

    @Test
    void testComponentCreation() {
        assertNotNull(component);
        assertEquals("COMP001", component.getTrartArticle());
        assertEquals("Test Component", component.getTrartDesignation());
        assertEquals("Family A", component.getTrartFamille());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        Component emptyComponent = new Component();

        // Assert
        assertNotNull(emptyComponent);
        assertNull(emptyComponent.getTrartArticle());
        assertNull(emptyComponent.getTrartDesignation());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange & Act
        Component fullComponent = new Component(
                "COMP002",
                "ATL002",
                "BUR002",
                "Full Component",
                "Family B",
                "Brand Y",
                "50",
                "Company XYZ",
                "SubFamily 2",
                "Trans2",
                "15",
                "TestValue",
                "Piece",
                "100.50"
        );

        // Assert
        assertNotNull(fullComponent);
        assertEquals("COMP002", fullComponent.getTrartArticle());
        assertEquals("Full Component", fullComponent.getTrartDesignation());
        assertEquals("Family B", fullComponent.getTrartFamille());
        assertEquals("Brand Y", fullComponent.getTrartMarque());
    }

    @Test
    void testSettersAndGetters() {
        // Act
        component.setTrartArticle("NEW001");
        component.setTrartDesignation("New Component");
        component.setTrartFamille("New Family");
        component.setTrartSousFamille("New SubFamily");
        component.setTrartMarque("New Brand");
        component.setTrartQuantite("200");
        component.setTrartSociete("New Company");
        component.setTrartUnite("New Unit");
        component.setTrartTransact("New Trans");
        component.setTrartTva("10");
        component.setAtl("ATL_NEW");
        component.setBur("BUR_NEW");
        component.setTest("NewTest");

        // Assert
        assertEquals("NEW001", component.getTrartArticle());
        assertEquals("New Component", component.getTrartDesignation());
        assertEquals("New Family", component.getTrartFamille());
        assertEquals("New SubFamily", component.getTrartSousFamille());
        assertEquals("New Brand", component.getTrartMarque());
        assertEquals("200", component.getTrartQuantite());
        assertEquals("New Company", component.getTrartSociete());
        assertEquals("New Unit", component.getTrartUnite());
        assertEquals("New Trans", component.getTrartTransact());
        assertEquals("10", component.getTrartTva());
        assertEquals("ATL_NEW", component.getAtl());
        assertEquals("BUR_NEW", component.getBur());
        assertEquals("NewTest", component.getTest());
    }

    @Test
    void testQuantiteAsString() {
        // Test that quantity is stored as String
        component.setTrartQuantite("1000");
        assertEquals("1000", component.getTrartQuantite());
        assertTrue(component.getTrartQuantite() instanceof String);
    }

    @Test
    void testTvaAsString() {
        // Test that TVA is stored as String
        component.setTrartTva("19.6");
        assertEquals("19.6", component.getTrartTva());
    }

    @Test
    void testAllFieldsNotNull() {
        // Assert all fields are set
        assertNotNull(component.getTrartArticle());
        assertNotNull(component.getTrartDesignation());
        assertNotNull(component.getTrartFamille());
        assertNotNull(component.getTrartSousFamille());
        assertNotNull(component.getTrartMarque());
        assertNotNull(component.getTrartQuantite());
        assertNotNull(component.getTrartSociete());
        assertNotNull(component.getTrartUnite());
        assertNotNull(component.getTrartTransact());
        assertNotNull(component.getTrartTva());
    }

    @Test
    void testEmptyStrings() {
        // Act
        component.setTrartDesignation("");
        component.setTrartFamille("");
        component.setTrartQuantite("0");

        // Assert
        assertEquals("", component.getTrartDesignation());
        assertEquals("", component.getTrartFamille());
        assertEquals("0", component.getTrartQuantite());
    }

    @Test
    void testNullFields() {
        // Act
        component.setAtl(null);
        component.setBur(null);
        component.setTest(null);

        // Assert
        assertNull(component.getAtl());
        assertNull(component.getBur());
        assertNull(component.getTest());
    }

    @Test
    void testIdField() {
        // The ID is trartArticle
        String id = "ID_TEST_123";
        component.setTrartArticle(id);
        
        assertEquals(id, component.getTrartArticle());
    }

    @Test
    void testLongQuantite() {
        // Test large quantity
        component.setTrartQuantite("999999");
        assertEquals("999999", component.getTrartQuantite());
    }

    @Test
    void testSpecialCharactersInFields() {
        // Test special characters
        component.setTrartDesignation("Component-123 (New)");
        component.setTrartMarque("Brand & Co.");
        
        assertEquals("Component-123 (New)", component.getTrartDesignation());
        assertEquals("Brand & Co.", component.getTrartMarque());
    }

    @Test
    void testPrixField() {
        // Test Prix as String
        component.setPrix("150.75");
        assertEquals("150.75", component.getPrix());
        assertTrue(component.getPrix() instanceof String);
    }

    @Test
    void testPrixWithDifferentFormats() {
        // Test different price formats
        component.setPrix("100");
        assertEquals("100", component.getPrix());

        component.setPrix("99.99");
        assertEquals("99.99", component.getPrix());

        component.setPrix("1234.567");
        assertEquals("1234.567", component.getPrix());
    }

    @Test
    void testPrePersist_GeneratesIdWhenNull() {
        // Arrange
        Component newComponent = new Component();
        newComponent.setTrartArticle(null);

        // Act
        newComponent.prePersist();

        // Assert
        assertNotNull(newComponent.getTrartArticle());
        // UUID format check (36 characters with dashes)
        assertEquals(36, newComponent.getTrartArticle().length());
    }

    @Test
    void testPrePersist_DoesNotOverrideExistingId() {
        // Arrange
        Component newComponent = new Component();
        String existingId = "EXISTING_ID_123";
        newComponent.setTrartArticle(existingId);

        // Act
        newComponent.prePersist();

        // Assert
        assertEquals(existingId, newComponent.getTrartArticle());
    }
}
