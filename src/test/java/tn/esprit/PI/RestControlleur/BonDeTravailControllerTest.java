package tn.esprit.PI.RestControlleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.PI.Services.BonDeTravailService;
import tn.esprit.PI.entity.BonDeTravail;
import tn.esprit.PI.entity.BonTravailRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BonDeTravailControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BonDeTravailService bonService;

    @InjectMocks
    private BonDeTravailController controller;

    private ObjectMapper objectMapper;
    private BonDeTravail testBon;
    private BonTravailRequest testRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        testBon = new BonDeTravail();
        testBon.setId(1L);
        testBon.setDescription("Test Description");

        testRequest = new BonTravailRequest();
        testRequest.description = "Test Request";
        testRequest.technicien = 1L;
    }

    @Test
    void testGetAll() throws Exception {
        List<BonDeTravail> bons = Collections.singletonList(testBon);
        when(bonService.getAllBonDeTravail()).thenReturn(bons);

        mockMvc.perform(get("/pi/bons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(bonService, times(1)).getAllBonDeTravail();
    }

    @Test
    void testTest() throws Exception {
        mockMvc.perform(get("/pi/bons/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Contrôleur BonDeTravail fonctionne"));
    }

    @Test
    void testGetById() throws Exception {
        when(bonService.getBonDeTravailById(1L)).thenReturn(testBon);

        mockMvc.perform(get("/pi/bons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testCreate() throws Exception {
        when(bonService.createBonDeTravail(any(BonTravailRequest.class))).thenReturn(testBon);

        mockMvc.perform(post("/pi/bons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdate_Success() throws Exception {
        when(bonService.updateBonDeTravail(anyLong(), any(BonTravailRequest.class))).thenReturn(testBon);

        mockMvc.perform(put("/pi/bons/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdate_InvalidId() throws Exception {
        mockMvc.perform(put("/pi/bons/update/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate_NullRequest() throws Exception {
        mockMvc.perform(put("/pi/bons/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate_NotFound() throws Exception {
        when(bonService.updateBonDeTravail(anyLong(), any(BonTravailRequest.class)))
                .thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(put("/pi/bons/update/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDelete_Success() throws Exception {
        doNothing().when(bonService).deleteBonDeTravail(1L);

        mockMvc.perform(delete("/pi/bons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testDelete_NotFound() throws Exception {
        doThrow(new RuntimeException("Not found")).when(bonService).deleteBonDeTravail(999L);

        mockMvc.perform(delete("/pi/bons/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateFromIntervention_Success() throws Exception {
        when(bonService.createBonDeTravailFromIntervention(anyLong(), anyLong(), any()))
                .thenReturn(testBon);

        mockMvc.perform(post("/pi/bons/intervention/1/technicien/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateFromIntervention_BadRequest() throws Exception {
        when(bonService.createBonDeTravailFromIntervention(anyLong(), anyLong(), any()))
                .thenThrow(new RuntimeException("Invalid data"));

        mockMvc.perform(post("/pi/bons/intervention/1/technicien/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetByIntervention() throws Exception {
        List<BonDeTravail> bons = Collections.singletonList(testBon);
        when(bonService.getBonsDeTravailByIntervention(1L)).thenReturn(bons);

        mockMvc.perform(get("/pi/bons/intervention/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetByTesteur() throws Exception {
        List<BonDeTravail> bons = Collections.singletonList(testBon);
        when(bonService.getBonsDeTravailByTesteur("TEST001")).thenReturn(bons);

        mockMvc.perform(get("/pi/bons/testeur/TEST001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
