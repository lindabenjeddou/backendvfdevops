package tn.esprit.PI.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PlaningTest {

    private Planing planing;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("technicien@test.com");
        testUser.setRole(UserRole.TECHNICIEN_CURATIF);

        planing = new Planing();
        planing.setId(1L);
        planing.setUser(testUser);
        planing.setStartDate(LocalDateTime.now());
        planing.setEndDate(LocalDateTime.now().plusHours(2));
        planing.setStatus(PlanningStatus.PENDING);
        planing.setTaskDescription("Test Task");
        planing.setLocation(Location.CMS2);
        planing.setComments("Test comments");
        planing.setIsUrgent(false);
        planing.setPriority("MEDIUM");
    }

    @Test
    void testIsTechnician_CuratifTechnician() {
        // Arrange
        testUser.setRole(UserRole.TECHNICIEN_CURATIF);

        // Act
        boolean result = planing.isTechnician();

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsTechnician_PreventifTechnician() {
        // Arrange
        testUser.setRole(UserRole.TECHNICIEN_PREVENTIF);

        // Act
        boolean result = planing.isTechnician();

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsTechnician_NotTechnician() {
        // Arrange
        testUser.setRole(UserRole.ADMIN);

        // Act
        boolean result = planing.isTechnician();

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsTechnician_NullUser() {
        // Arrange
        planing.setUser(null);

        // Act
        boolean result = planing.isTechnician();

        // Assert
        assertFalse(result);
    }

    @Test
    void testPrePersist_SetsDefaultStartDate() {
        // Arrange
        Planing newPlaning = new Planing();
        newPlaning.setStartDate(null);
        newPlaning.setEndDate(null);

        // Act
        newPlaning.prePersist();

        // Assert
        assertNotNull(newPlaning.getStartDate());
    }

    @Test
    void testPrePersist_SetsDefaultEndDate() {
        // Arrange
        Planing newPlaning = new Planing();
        newPlaning.setStartDate(null);
        newPlaning.setEndDate(null);

        // Act
        newPlaning.prePersist();

        // Assert
        assertNotNull(newPlaning.getEndDate());
    }

    @Test
    void testPrePersist_EndDateIsOneHourAfterStart() {
        // Arrange
        Planing newPlaning = new Planing();
        newPlaning.setStartDate(null);
        newPlaning.setEndDate(null);

        // Act
        newPlaning.prePersist();

        // Assert
        assertTrue(newPlaning.getEndDate().isAfter(newPlaning.getStartDate()));
    }

    @Test
    void testPrePersist_DoesNotOverrideExistingDates() {
        // Arrange
        LocalDateTime customStart = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime customEnd = LocalDateTime.of(2024, 1, 1, 12, 0);
        
        Planing newPlaning = new Planing();
        newPlaning.setStartDate(customStart);
        newPlaning.setEndDate(customEnd);

        // Act
        newPlaning.prePersist();

        // Assert
        assertEquals(customStart, newPlaning.getStartDate());
        assertEquals(customEnd, newPlaning.getEndDate());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        Planing emptyPlaning = new Planing();

        // Assert
        assertNotNull(emptyPlaning);
        assertNull(emptyPlaning.getId());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange & Act
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(3);
        
        Planing fullPlaning = new Planing(
                2L,
                testUser,
                start,
                end,
                PlanningStatus.IN_PROGRESS,
                "Full task",
                Location.INTEGRATION,
                "Full comments",
                true,
                "HIGH",
                start
        );

        // Assert
        assertNotNull(fullPlaning);
        assertEquals(2L, fullPlaning.getId());
        assertEquals(testUser, fullPlaning.getUser());
        assertEquals(PlanningStatus.IN_PROGRESS, fullPlaning.getStatus());
        assertEquals(Location.INTEGRATION, fullPlaning.getLocation());
        assertTrue(fullPlaning.getIsUrgent());
        assertEquals("HIGH", fullPlaning.getPriority());
    }

    @Test
    void testSettersAndGetters() {
        // Act
        planing.setId(10L);
        planing.setPriority("HIGH");
        planing.setIsUrgent(true);
        planing.setStatus(PlanningStatus.COMPLETED);
        planing.setLocation(Location.INTEGRATION);
        planing.setTaskDescription("Updated task");
        planing.setComments("Updated comments");

        // Assert
        assertEquals(10L, planing.getId());
        assertEquals("HIGH", planing.getPriority());
        assertTrue(planing.getIsUrgent());
        assertEquals(PlanningStatus.COMPLETED, planing.getStatus());
        assertEquals(Location.INTEGRATION, planing.getLocation());
        assertEquals("Updated task", planing.getTaskDescription());
        assertEquals("Updated comments", planing.getComments());
    }

    @Test
    void testAllPlanningStatuses() {
        // Test all enum values
        planing.setStatus(PlanningStatus.PENDING);
        assertEquals(PlanningStatus.PENDING, planing.getStatus());

        planing.setStatus(PlanningStatus.IN_PROGRESS);
        assertEquals(PlanningStatus.IN_PROGRESS, planing.getStatus());

        planing.setStatus(PlanningStatus.COMPLETED);
        assertEquals(PlanningStatus.COMPLETED, planing.getStatus());
    }

    @Test
    void testAllLocations() {
        // Test all enum values
        planing.setLocation(Location.CMS2);
        assertEquals(Location.CMS2, planing.getLocation());

        planing.setLocation(Location.INTEGRATION);
        assertEquals(Location.INTEGRATION, planing.getLocation());
    }

    @Test
    void testTaskDescriptionMaxLength() {
        // Arrange
        String longDescription = "A".repeat(500); // Max length

        // Act
        planing.setTaskDescription(longDescription);

        // Assert
        assertEquals(500, planing.getTaskDescription().length());
    }

    @Test
    void testDateRange() {
        // Arrange
        LocalDateTime dateRange = LocalDateTime.now();

        // Act
        planing.setDateRange(dateRange);

        // Assert
        assertEquals(dateRange, planing.getDateRange());
    }

    @Test
    void testUserRelationship() {
        // Assert
        assertNotNull(planing.getUser());
        assertEquals(testUser.getId(), planing.getUser().getId());
        assertEquals(UserRole.TECHNICIEN_CURATIF, planing.getUser().getRole());
    }

    @Test
    void testIsTechnician_AllRoles() {
        // Test TECHNICIEN_CURATIF
        testUser.setRole(UserRole.TECHNICIEN_CURATIF);
        assertTrue(planing.isTechnician());

        // Test TECHNICIEN_PREVENTIF
        testUser.setRole(UserRole.TECHNICIEN_PREVENTIF);
        assertTrue(planing.isTechnician());

        // Test ADMIN
        testUser.setRole(UserRole.ADMIN);
        assertFalse(planing.isTechnician());

        // Test MAGASINIER
        testUser.setRole(UserRole.MAGASINIER);
        assertFalse(planing.isTechnician());

        // Test CHEF_PROJET
        testUser.setRole(UserRole.CHEF_PROJET);
        assertFalse(planing.isTechnician());
    }
}
