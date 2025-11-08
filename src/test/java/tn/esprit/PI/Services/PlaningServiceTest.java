package tn.esprit.PI.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.PI.entity.Planing;
import tn.esprit.PI.entity.PlanningStatus;
import tn.esprit.PI.entity.Location;
import tn.esprit.PI.repository.PlaningRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaningServiceTest {

    @Mock
    private PlaningRepository planingRepository;

    @InjectMocks
    private PlaningService service;

    private Planing testPlaning;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.now();
        endDate = startDate.plusHours(2);

        testPlaning = new Planing();
        testPlaning.setId(1L);
        testPlaning.setStartDate(startDate);
        testPlaning.setEndDate(endDate);
        testPlaning.setStatus(PlanningStatus.PENDING);
        testPlaning.setTaskDescription("Test Task");
        testPlaning.setLocation(Location.CMS2);
        testPlaning.setComments("Test Comments");
        testPlaning.setIsUrgent(false);
        testPlaning.setPriority("MEDIUM");
    }

    @Test
    void testCreatePlaning_WithDates() {
        // Arrange
        when(planingRepository.save(any(Planing.class))).thenReturn(testPlaning);

        // Act
        Planing result = service.createPlaning(testPlaning);

        // Assert
        assertNotNull(result);
        assertEquals("Test Task", result.getTaskDescription());
        assertNotNull(result.getStartDate());
        assertNotNull(result.getEndDate());
        verify(planingRepository, times(1)).save(testPlaning);
    }

    @Test
    void testCreatePlaning_WithoutStartDate() {
        // Arrange
        Planing planingWithoutDate = new Planing();
        planingWithoutDate.setTaskDescription("Test Task");
        when(planingRepository.save(any(Planing.class))).thenReturn(planingWithoutDate);

        // Act
        Planing result = service.createPlaning(planingWithoutDate);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getStartDate()); // Should be set to now
        assertNotNull(result.getEndDate()); // Should be set to start + 1 hour
        verify(planingRepository, times(1)).save(planingWithoutDate);
    }

    @Test
    void testCreatePlaning_WithoutEndDate() {
        // Arrange
        Planing planingWithoutEndDate = new Planing();
        planingWithoutEndDate.setStartDate(startDate);
        planingWithoutEndDate.setTaskDescription("Test Task");
        when(planingRepository.save(any(Planing.class))).thenReturn(planingWithoutEndDate);

        // Act
        Planing result = service.createPlaning(planingWithoutEndDate);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getEndDate()); // Should be set to start + 1 hour
        verify(planingRepository, times(1)).save(planingWithoutEndDate);
    }

    @Test
    void testGetAllPlannings_Success() {
        // Arrange
        when(planingRepository.findAll()).thenReturn(Collections.singletonList(testPlaning));

        // Act
        List<Planing> result = service.getAllPlannings();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTaskDescription());
        verify(planingRepository, times(1)).findAll();
    }

    @Test
    void testGetAllPlannings_Empty() {
        // Arrange
        when(planingRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Planing> result = service.getAllPlannings();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPlaningById_Success() {
        // Arrange
        when(planingRepository.findById(1L)).thenReturn(Optional.of(testPlaning));

        // Act
        Optional<Planing> result = service.getPlaningById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Task", result.get().getTaskDescription());
        verify(planingRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPlaningById_NotFound() {
        // Arrange
        when(planingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Planing> result = service.getPlaningById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testUpdatePlaning_Success() {
        // Arrange
        Planing updatedPlaning = new Planing();
        updatedPlaning.setStatus(PlanningStatus.COMPLETED);
        updatedPlaning.setTaskDescription("Updated Task");
        updatedPlaning.setIsUrgent(true);

        when(planingRepository.findById(1L)).thenReturn(Optional.of(testPlaning));
        when(planingRepository.save(any(Planing.class))).thenReturn(testPlaning);

        // Act
        Planing result = service.updatePlaning(1L, updatedPlaning);

        // Assert
        assertNotNull(result);
        verify(planingRepository, times(1)).save(testPlaning);
    }

    @Test
    void testUpdatePlaning_NotFound() {
        // Arrange
        Planing updatedPlaning = new Planing();
        when(planingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Planing result = service.updatePlaning(999L, updatedPlaning);

        // Assert
        assertNull(result);
        verify(planingRepository, never()).save(any());
    }

    @Test
    void testUpdatePlaning_PartialUpdate() {
        // Arrange
        Planing partialUpdate = new Planing();
        partialUpdate.setStatus(PlanningStatus.IN_PROGRESS); // Only update status

        when(planingRepository.findById(1L)).thenReturn(Optional.of(testPlaning));
        when(planingRepository.save(any(Planing.class))).thenReturn(testPlaning);

        // Act
        Planing result = service.updatePlaning(1L, partialUpdate);

        // Assert
        assertNotNull(result);
        verify(planingRepository, times(1)).save(testPlaning);
    }

    @Test
    void testDeletePlaning_Success() {
        // Arrange
        when(planingRepository.findById(1L)).thenReturn(Optional.of(testPlaning));
        doNothing().when(planingRepository).delete(testPlaning);

        // Act
        boolean result = service.deletePlaning(1L);

        // Assert
        assertTrue(result);
        verify(planingRepository, times(1)).delete(testPlaning);
    }

    @Test
    void testDeletePlaning_NotFound() {
        // Arrange
        when(planingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        boolean result = service.deletePlaning(999L);

        // Assert
        assertFalse(result);
        verify(planingRepository, never()).delete(any());
    }

    @Test
    void testGetPlanningsByUserId_Success() {
        // Arrange
        when(planingRepository.findByUserId(1L)).thenReturn(Collections.singletonList(testPlaning));

        // Act
        List<Planing> result = service.getPlanningsByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(planingRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testGetPlanningsByUserId_Empty() {
        // Arrange
        when(planingRepository.findByUserId(999L)).thenReturn(Collections.emptyList());

        // Act
        List<Planing> result = service.getPlanningsByUserId(999L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testIsTechnicianAvailable_True() {
        // Arrange
        when(planingRepository.findByUserIdAndDateRange(1L, startDate, endDate))
            .thenReturn(Collections.emptyList());

        // Act
        boolean result = service.isTechnicianAvailable(1L, startDate, endDate);

        // Assert
        assertTrue(result);
        verify(planingRepository, times(1)).findByUserIdAndDateRange(1L, startDate, endDate);
    }

    @Test
    void testIsTechnicianAvailable_False() {
        // Arrange
        when(planingRepository.findByUserIdAndDateRange(1L, startDate, endDate))
            .thenReturn(Collections.singletonList(testPlaning));

        // Act
        boolean result = service.isTechnicianAvailable(1L, startDate, endDate);

        // Assert
        assertFalse(result);
        verify(planingRepository, times(1)).findByUserIdAndDateRange(1L, startDate, endDate);
    }
}
