package tn.esprit.PI.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import tn.esprit.PI.entity.Component;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for ComponentRp repository using @DataJpaTest
 * This test uses an in-memory H2 database automatically configured by Spring Boot
 */
@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ComponentRpTest {

    @Autowired
    private ComponentRp componentRp;

    @Autowired
    private TestEntityManager entityManager;

    private Component testComponent;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        componentRp.deleteAll();

        // Setup test data
        testComponent = new Component();
        testComponent.setTrartArticle("TEST001");
        testComponent.setTrartDesignation("Test Component");
        testComponent.setTrartFamille("Family A");
        testComponent.setTrartSousFamille("SubFamily 1");
        testComponent.setTrartMarque("Brand X");
        testComponent.setTrartQuantite("10");
        testComponent.setTrartSociete("Company");
        testComponent.setTrartUnite("Unit");
        testComponent.setTrartTransact("Trans");
        testComponent.setTrartTva("20");
        testComponent.setAtl("ATL001");
        testComponent.setBur("BUR001");
    }

    @Test
    void testSaveComponent_Success() {
        // Act
        Component savedComponent = componentRp.save(testComponent);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertNotNull(savedComponent);
        assertEquals("TEST001", savedComponent.getTrartArticle());
        assertEquals("Test Component", savedComponent.getTrartDesignation());
    }

    @Test
    void testFindByTrartArticle_Success() {
        // Arrange
        componentRp.save(testComponent);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Component> found = componentRp.findByTrartArticle("TEST001");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Test Component", found.get().getTrartDesignation());
    }

    @Test
    void testFindByTrartArticle_NotFound() {
        // Act
        Optional<Component> found = componentRp.findByTrartArticle("NONEXISTENT");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll_Success() {
        // Arrange
        Component component2 = new Component();
        component2.setTrartArticle("TEST002");
        component2.setTrartDesignation("Component 2");
        component2.setTrartFamille("Family B");

        componentRp.save(testComponent);
        componentRp.save(component2);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<Component> components = componentRp.findAll();

        // Assert
        assertEquals(2, components.size());
    }

    @Test
    void testDeleteComponent_Success() {
        // Arrange
        componentRp.save(testComponent);
        entityManager.flush();
        entityManager.clear();

        // Act
        componentRp.delete(testComponent);
        entityManager.flush();
        entityManager.clear();

        Optional<Component> found = componentRp.findByTrartArticle("TEST001");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void testSearchComponents_WithAllParameters() {
        // Arrange
        componentRp.save(testComponent);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<Component> results = componentRp.searchComponents(
                "TEST001", "ATL001", "BUR001", "Test", "Family A",
                "Brand X", "10", "Company", "SubFamily 1", "Trans",
                "20", "Unit", null
        );

        // Assert
        assertEquals(1, results.size());
        assertEquals("TEST001", results.get(0).getTrartArticle());
    }

    @Test
    void testSearchComponents_WithPartialMatch() {
        // Arrange
        componentRp.save(testComponent);
        entityManager.flush();
        entityManager.clear();

        // Act - Search with partial family name
        List<Component> results = componentRp.searchComponents(
                null, null, null, null, "Family",
                null, null, null, null, null,
                null, null, null
        );

        // Assert
        assertEquals(1, results.size());
        assertEquals("Family A", results.get(0).getTrartFamille());
    }

    @Test
    void testSearchComponents_NoResults() {
        // Arrange
        componentRp.save(testComponent);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<Component> results = componentRp.searchComponents(
                "NONEXISTENT", null, null, null, null,
                null, null, null, null, null,
                null, null, null
        );

        // Assert
        assertEquals(0, results.size());
    }

    @Test
    void testSearchComponents_WithNullParameters() {
        // Arrange
        componentRp.save(testComponent);
        entityManager.flush();
        entityManager.clear();

        // Act - All parameters null should return all components
        List<Component> results = componentRp.searchComponents(
                null, null, null, null, null,
                null, null, null, null, null,
                null, null, null
        );

        // Assert
        assertEquals(1, results.size());
    }
}
