package tn.esprit.PI.RestControlleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.PI.Services.PlaningService;
import tn.esprit.PI.entity.Planing;
import tn.esprit.PI.entity.PlanningStatus;
import tn.esprit.PI.entity.Location;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PlaningControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PlaningService planingService;

    @InjectMocks
    private PlaningController controller;

    private ObjectMapper objectMapper;
    private Planing testPlaning;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testPlaning = new Planing();
        testPlaning.setId(1L);
        testPlaning.setStartDate(LocalDateTime.now());
        testPlaning.setEndDate(LocalDateTime.now().plusHours(2));
        testPlaning.setStatus(PlanningStatus.PENDING);
        testPlaning.setTaskDescription("Test Task");
        testPlaning.setLocation(Location.CMS2);
        testPlaning.setIsUrgent(false);
        testPlaning.setPriority("MEDIUM");
    }

    @Test
    void testCreatePlaning() throws Exception {
        when(planingService.createPlaning(any(Planing.class))).thenReturn(testPlaning);

        mockMvc.perform(post("/PI/planing/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPlaning)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(planingService, times(1)).createPlaning(any(Planing.class));
    }

    @Test
    void testGetAllPlannings() throws Exception {
        List<Planing> plannings = Collections.singletonList(testPlaning);
        when(planingService.getAllPlannings()).thenReturn(plannings);

        mockMvc.perform(get("/PI/planing/recuperer/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(planingService, times(1)).getAllPlannings();
    }

    @Test
    void testGetPlaningById_Success() throws Exception {
        when(planingService.getPlaningById(1L)).thenReturn(Optional.of(testPlaning));

        mockMvc.perform(get("/PI/planing/recuperer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetPlaningById_NotFound() throws Exception {
        when(planingService.getPlaningById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/PI/planing/recuperer/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdatePlaning_Success() throws Exception {
        when(planingService.updatePlaning(anyLong(), any(Planing.class))).thenReturn(testPlaning);

        mockMvc.perform(put("/PI/planing/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPlaning)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdatePlaning_NotFound() throws Exception {
        when(planingService.updatePlaning(anyLong(), any(Planing.class))).thenReturn(null);

        mockMvc.perform(put("/PI/planing/update/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPlaning)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeletePlaning_Success() throws Exception {
        when(planingService.deletePlaning(1L)).thenReturn(true);

        mockMvc.perform(delete("/PI/planing/delete/1"))
                .andExpect(status().isNoContent());

        verify(planingService, times(1)).deletePlaning(1L);
    }

    @Test
    void testDeletePlaning_NotFound() throws Exception {
        when(planingService.deletePlaning(999L)).thenReturn(false);

        mockMvc.perform(delete("/PI/planing/delete/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPlanningsByUserId() throws Exception {
        List<Planing> plannings = Collections.singletonList(testPlaning);
        when(planingService.getPlanningsByUserId(1L)).thenReturn(plannings);

        mockMvc.perform(get("/PI/planing/recuperer/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testCheckTechnicianAvailability_True() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        
        when(planingService.isTechnicianAvailable(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);

        mockMvc.perform(get("/PI/planing/check-availability/1")
                        .param("startDate", start.toString())
                        .param("endDate", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testCheckTechnicianAvailability_False() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        
        when(planingService.isTechnicianAvailable(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);

        mockMvc.perform(get("/PI/planing/check-availability/1")
                        .param("startDate", start.toString())
                        .param("endDate", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }
}
