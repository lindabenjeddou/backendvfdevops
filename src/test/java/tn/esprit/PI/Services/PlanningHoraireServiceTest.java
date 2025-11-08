package tn.esprit.PI.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.PI.entity.PlanningHoraire;
import tn.esprit.PI.entity.User;
import tn.esprit.PI.entity.UserRole;
import tn.esprit.PI.repository.PlanningHoraireRepository;
import tn.esprit.PI.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanningHoraireServiceTest {

    @Mock
    private PlanningHoraireRepository planningHoraireRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PlanningHoraireService service;

    private PlanningHoraire testPlanning;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setRole(UserRole.TECHNICIEN_CURATIF);

        testPlanning = new PlanningHoraire();
        testPlanning.setId(1L);
        testPlanning.setUser(testUser);
        testPlanning.setStartDate(LocalDateTime.now());
        testPlanning.setEndDate(LocalDateTime.now().plusDays(1));
        testPlanning.setDescription("Test Planning");
        testPlanning.setValid(true);
    }

    @Test
    void testSavePlanningHoraire_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(planningHoraireRepository.save(any(PlanningHoraire.class))).thenReturn(testPlanning);

        PlanningHoraire result = service.savePlanningHoraire(testPlanning);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(planningHoraireRepository, times(1)).save(any(PlanningHoraire.class));
    }

    @Test
    void testSavePlanningHoraire_NullUser() {
        testPlanning.setUser(null);

        assertThrows(IllegalArgumentException.class, () -> {
            service.savePlanningHoraire(testPlanning);
        });
    }

    @Test
    void testSavePlanningHoraire_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            service.savePlanningHoraire(testPlanning);
        });
    }

    @Test
    void testFindById_Success() {
        when(planningHoraireRepository.findById(1L)).thenReturn(Optional.of(testPlanning));

        Optional<PlanningHoraire> result = service.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testFindById_NotFound() {
        when(planningHoraireRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<PlanningHoraire> result = service.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void testUpdatePlanningHoraire_Success() {
        PlanningHoraire newPlanning = new PlanningHoraire();
        newPlanning.setStartDate(LocalDateTime.now().plusDays(2));
        newPlanning.setEndDate(LocalDateTime.now().plusDays(3));
        newPlanning.setDescription("Updated");
        newPlanning.setValid(false);

        when(planningHoraireRepository.findById(1L)).thenReturn(Optional.of(testPlanning));
        when(planningHoraireRepository.save(any(PlanningHoraire.class))).thenReturn(testPlanning);

        PlanningHoraire result = service.updatePlanningHoraire(1L, newPlanning);

        assertNotNull(result);
        verify(planningHoraireRepository, times(1)).save(testPlanning);
    }

    @Test
    void testUpdatePlanningHoraire_NotFound() {
        PlanningHoraire newPlanning = new PlanningHoraire();
        when(planningHoraireRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            service.updatePlanningHoraire(999L, newPlanning);
        });
    }

    @Test
    void testDeletePlanningHoraire() {
        doNothing().when(planningHoraireRepository).deleteById(1L);

        service.deletePlanningHoraire(1L);

        verify(planningHoraireRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindAllPlanningHoraires() {
        List<PlanningHoraire> plannings = Collections.singletonList(testPlanning);
        when(planningHoraireRepository.findAll()).thenReturn(plannings);

        List<PlanningHoraire> result = service.findAllPlanningHoraires();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(planningHoraireRepository, times(1)).findAll();
    }

    @Test
    void testGetTechniciensDisponibles_Success() {
        LocalDate date = LocalDate.now();
        testPlanning.setStartDate(date.atStartOfDay());
        testPlanning.setEndDate(date.atTime(23, 59));
        testPlanning.setValid(true);

        List<PlanningHoraire> plannings = Collections.singletonList(testPlanning);
        List<User> users = Collections.singletonList(testUser);

        when(planningHoraireRepository.findAll()).thenReturn(plannings);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = service.getTechniciensDisponibles(date);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
    }

    @Test
    void testGetTechniciensDisponibles_NoValidPlanning() {
        LocalDate date = LocalDate.now();
        testPlanning.setValid(false);

        List<PlanningHoraire> plannings = Collections.singletonList(testPlanning);
        List<User> users = Collections.singletonList(testUser);

        when(planningHoraireRepository.findAll()).thenReturn(plannings);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = service.getTechniciensDisponibles(date);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTechniciensDisponibles_NullUser() {
        LocalDate date = LocalDate.now();
        testPlanning.setUser(null);

        List<PlanningHoraire> plannings = Collections.singletonList(testPlanning);

        when(planningHoraireRepository.findAll()).thenReturn(plannings);
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<User> result = service.getTechniciensDisponibles(date);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTechniciensDisponibles_NotTechnicien() {
        LocalDate date = LocalDate.now();
        testUser.setRole(UserRole.ADMIN);
        testPlanning.setStartDate(date.atStartOfDay());
        testPlanning.setEndDate(date.atTime(23, 59));

        List<PlanningHoraire> plannings = Collections.singletonList(testPlanning);
        List<User> users = Collections.singletonList(testUser);

        when(planningHoraireRepository.findAll()).thenReturn(plannings);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = service.getTechniciensDisponibles(date);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
