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
import tn.esprit.PI.Services.TesteurService;
import tn.esprit.PI.entity.Testeur;
import tn.esprit.PI.entity.TesteurDTO;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TesteurControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TesteurService testeurService;

    @InjectMocks
    private TesteurController controller;

    private ObjectMapper objectMapper;
    private Testeur testTesteur;
    private TesteurDTO testDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        testTesteur = new Testeur();
        testTesteur.setCodeGMAO("TEST001");
        testTesteur.setAtelier("Atelier A");
        testTesteur.setLigne("Ligne 1");
        testTesteur.setBancTest("Banc 1");

        testDTO = new TesteurDTO();
        testDTO.setCodeGMAO("TEST001");
        testDTO.setAtelier("Atelier A");
        testDTO.setLigne("Ligne 1");
        testDTO.setBancTest("Banc 1");
    }

    @Test
    void testCreateTesteur() throws Exception {
        when(testeurService.createTesteur(any(Testeur.class))).thenReturn(testTesteur);

        mockMvc.perform(post("/PI/testeurs/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTesteur)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codeGMAO").value("TEST001"));

        verify(testeurService, times(1)).createTesteur(any(Testeur.class));
    }

    @Test
    void testGetAllTesteurs_Success() throws Exception {
        List<TesteurDTO> testeurs = Collections.singletonList(testDTO);
        when(testeurService.getAllTesteursDTO()).thenReturn(testeurs);

        mockMvc.perform(get("/PI/testeurs/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codeGMAO").value("TEST001"));

        verify(testeurService, times(1)).getAllTesteursDTO();
    }

    @Test
    void testGetAllTesteurs_Error() throws Exception {
        when(testeurService.getAllTesteursDTO()).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/PI/testeurs/all"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateTesteur_Success() throws Exception {
        when(testeurService.updateTesteur(anyString(), anyString(), any(Testeur.class)))
                .thenReturn(testTesteur);

        mockMvc.perform(put("/PI/testeurs/update/AtelierA/Ligne1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTesteur)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codeGMAO").value("TEST001"));
    }

    @Test
    void testUpdateTesteur_NotFound() throws Exception {
        when(testeurService.updateTesteur(anyString(), anyString(), any(Testeur.class)))
                .thenReturn(null);

        mockMvc.perform(put("/PI/testeurs/update/Unknown/Unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTesteur)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTesteur() throws Exception {
        doNothing().when(testeurService).deleteTesteur(anyString(), anyString());

        mockMvc.perform(delete("/PI/testeurs/delete/AtelierA/Ligne1"))
                .andExpect(status().isNoContent());

        verify(testeurService, times(1)).deleteTesteur("AtelierA", "Ligne1");
    }
}
