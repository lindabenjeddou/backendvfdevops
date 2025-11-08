package tn.esprit.PI.RestControlleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.PI.Services.SousProjetService;
import tn.esprit.PI.entity.SousProjet;
import tn.esprit.PI.entity.SousProjetDto;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SousProjetControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SousProjetService sousProjetService;

    @InjectMocks
    private SousProjetController controller;

    private ObjectMapper objectMapper;
    private SousProjet testSousProjet;
    private SousProjetDto testDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        testSousProjet = new SousProjet();
        testSousProjet.setId(1L);
        testSousProjet.setSousProjetName("Test Sous-Projet");

        testDto = new SousProjetDto();
        testDto.setId(1L);
        testDto.setSousProjetName("Test Sous-Projet");
        testDto.setTotalPrice(1000.0);
    }

    @Test
    void testCreateSousProjet_Success() throws Exception {
        when(sousProjetService.createSousProjet(any(SousProjetDto.class), anyLong()))
                .thenReturn(testSousProjet);

        mockMvc.perform(post("/PI/sousprojets/create/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testCreateSousProjet_NotFound() throws Exception {
        when(sousProjetService.createSousProjet(any(SousProjetDto.class), anyLong()))
                .thenThrow(new EntityNotFoundException("Project not found"));

        mockMvc.perform(post("/PI/sousprojets/create/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateSousProjet_BadRequest() throws Exception {
        when(sousProjetService.createSousProjet(any(SousProjetDto.class), anyLong()))
                .thenThrow(new RuntimeException("Invalid data"));

        mockMvc.perform(post("/PI/sousprojets/create/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllSousProjets() throws Exception {
        List<SousProjetDto> sousProjets = Collections.singletonList(testDto);
        when(sousProjetService.getAllSousProjets()).thenReturn(sousProjets);

        mockMvc.perform(get("/PI/sousprojets/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetSousProjetsByProjectId() throws Exception {
        List<SousProjet> sousProjets = Collections.singletonList(testSousProjet);
        when(sousProjetService.getSousProjetsByProjectId(1L)).thenReturn(sousProjets);

        mockMvc.perform(get("/PI/sousprojets/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetSousProjetById() throws Exception {
        when(sousProjetService.getSousProjetById(1L)).thenReturn(testSousProjet);

        mockMvc.perform(get("/PI/sousprojets/sousprojet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateSousProjet() throws Exception {
        when(sousProjetService.updateSousProjet(anyLong(), any(SousProjetDto.class)))
                .thenReturn(testSousProjet);

        mockMvc.perform(put("/PI/sousprojets/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testDeleteSousProjet() throws Exception {
        doNothing().when(sousProjetService).deleteSousProjet(1L);

        mockMvc.perform(delete("/PI/sousprojets/delete/1"))
                .andExpect(status().isNoContent());

        verify(sousProjetService, times(1)).deleteSousProjet(1L);
    }

    @Test
    void testConfirmSousProjetAutomatically() throws Exception {
        when(sousProjetService.confirmSousProjetAutomatically(1L)).thenReturn(testSousProjet);

        mockMvc.perform(put("/PI/sousprojets/confirm/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
