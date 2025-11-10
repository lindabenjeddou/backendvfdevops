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
import tn.esprit.PI.Services.DemandeInterventionService;
import tn.esprit.PI.entity.*;
import tn.esprit.PI.repository.DemandeInterventionRepository;
import tn.esprit.PI.repository.TesteurRepository;
import tn.esprit.PI.repository.UserRepository;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DemandeInterventionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DemandeInterventionService demandeInterventionService;

    @Mock
    private DemandeInterventionRepository demandeInterventionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BonDeTravailService bonDeTravailService;

    @Mock
    private TesteurRepository testeurRepository;

    @InjectMocks
    private DemandeInterventionController controller;

    private ObjectMapper objectMapper;
    private DemandeInterventionDTO testDTO;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");

        testDTO = new DemandeInterventionDTO();
        testDTO.setId(1L);
        testDTO.setDescription("Test Description");
        testDTO.setStatut(StatutDemande.EN_ATTENTE);
        testDTO.setPriorite("HAUTE");
        testDTO.setDemandeurId(1L);
    }

    @Test
    void testGetByTechnicien() throws Exception {
        List<DemandeInterventionDTO> demandes = Collections.singletonList(testDTO);
        when(demandeInterventionService.getByTechnicien(1L)).thenReturn(demandes);

        mockMvc.perform(get("/demandes/technicien/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Test Description"));

        verify(demandeInterventionService, times(1)).getByTechnicien(1L);
    }

    @Test
    void testGetDemandeById_Success() throws Exception {
        when(demandeInterventionService.getDemandeById(1L)).thenReturn(Optional.of(testDTO));

        mockMvc.perform(get("/demandes/recuperer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void testGetDemandeById_NotFound() throws Exception {
        when(demandeInterventionService.getDemandeById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/demandes/recuperer/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllDemandes() throws Exception {
        List<DemandeInterventionDTO> demandes = Collections.singletonList(testDTO);
        when(demandeInterventionService.getAllDemandes()).thenReturn(demandes);

        mockMvc.perform(get("/demandes/recuperer/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(demandeInterventionService, times(1)).getAllDemandes();
    }

    @Test
    void testGetAllDemandesShort_Success() throws Exception {
        List<DemandeInterventionDTO> demandes = Collections.singletonList(testDTO);
        when(demandeInterventionService.getAllDemandes()).thenReturn(demandes);

        mockMvc.perform(get("/demandes/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testUpdateDemande_Success() throws Exception {
        when(demandeInterventionService.updateDemande(anyLong(), any(DemandeInterventionDTO.class)))
                .thenReturn(testDTO);

        mockMvc.perform(put("/demandes/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateDemande_NotFound() throws Exception {
        when(demandeInterventionService.updateDemande(anyLong(), any(DemandeInterventionDTO.class)))
                .thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(put("/demandes/update/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAssignTechnicianToIntervention_Success() throws Exception {
        when(demandeInterventionService.assignTechnicianToIntervention(1L, 2L))
                .thenReturn(testDTO);

        mockMvc.perform(put("/demandes/assign/1/technicien/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testAssignTechnicianToIntervention_NotFound() throws Exception {
        when(demandeInterventionService.assignTechnicianToIntervention(999L, 2L))
                .thenThrow(new RuntimeException("Intervention not found"));

        mockMvc.perform(put("/demandes/assign/999/technicien/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAssignTesteurToIntervention_Success() throws Exception {
        when(demandeInterventionService.assignTesteurToIntervention(1L, "TEST001"))
                .thenReturn(testDTO);

        mockMvc.perform(put("/demandes/assign/1/testeur/TEST001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testConfirmerIntervention_Success() throws Exception {
        when(demandeInterventionService.confirmerIntervention(1L))
                .thenReturn(testDTO);

        mockMvc.perform(put("/demandes/confirmer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testCreateBonDeTravailForIntervention_Success() throws Exception {
        BonDeTravail bon = new BonDeTravail();
        bon.setId(1L);
        
        BonTravailRequest request = new BonTravailRequest();
        request.description = "Test bon";

        when(bonDeTravailService.createBonDeTravailFromIntervention(anyLong(), anyLong(), any()))
                .thenReturn(bon);

        mockMvc.perform(post("/demandes/1/bon-travail/technicien/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetBonsDeTravailForIntervention_Success() throws Exception {
        List<BonDeTravail> bons = new ArrayList<>();
        when(bonDeTravailService.getBonsDeTravailByIntervention(1L)).thenReturn(bons);

        mockMvc.perform(get("/demandes/1/bons-travail"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateIntervention_Curative_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("type_demande", "CURATIVE");
        request.put("demandeurId", 1L);
        request.put("description", "Test");
        request.put("priorite", "HAUTE");
        request.put("statut", "EN_ATTENTE");
        request.put("panne", "Panne test");
        request.put("urgence", true);

        Curative curative = new Curative();
        curative.setId(1L);
        curative.setDescription("Test");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(demandeInterventionRepository.save(any(Curative.class))).thenReturn(curative);

        mockMvc.perform(post("/demandes/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateIntervention_InvalidType() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("type_demande", "INVALID_TYPE");
        request.put("demandeurId", 1L);
        request.put("description", "Test");
        request.put("priorite", "HAUTE");
        request.put("statut", "EN_ATTENTE");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/demandes/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
