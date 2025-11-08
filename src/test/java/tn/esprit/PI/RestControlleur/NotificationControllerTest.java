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
import tn.esprit.PI.Services.NotificationService;
import tn.esprit.PI.entity.Notification;
import tn.esprit.PI.entity.User;
import tn.esprit.PI.repository.UserRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationController controller;

    private ObjectMapper objectMapper;
    private User testUser;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");

        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setMessage("Test notification");
    }

    @Test
    void testGetNotificationsForUser_Success() throws Exception {
        List<Notification> notifications = Collections.singletonList(testNotification);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(notificationService.getNotificationsForUser(any(User.class))).thenReturn(notifications);

        mockMvc.perform(get("/PI/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetNotificationsForUser_UserNotFound() throws Exception {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/PI/notifications/user/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetUnreadNotificationsForUser_Success() throws Exception {
        List<Notification> notifications = Collections.singletonList(testNotification);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(notificationService.getUnreadNotificationsForUser(any(User.class))).thenReturn(notifications);

        mockMvc.perform(get("/PI/notifications/user/1/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testCountUnreadNotifications_Success() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(notificationService.countUnreadNotifications(any(User.class))).thenReturn(5L);

        mockMvc.perform(get("/PI/notifications/user/1/unread/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }

    @Test
    void testMarkAsRead_Success() throws Exception {
        doNothing().when(notificationService).markAsRead(1L);

        mockMvc.perform(put("/PI/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Notification marquée comme lue"));
    }

    @Test
    void testMarkAsRead_Error() throws Exception {
        doThrow(new RuntimeException("Error")).when(notificationService).markAsRead(999L);

        mockMvc.perform(put("/PI/notifications/999/read"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetAllMagasinierNotifications_Success() throws Exception {
        List<Notification> notifications = Collections.singletonList(testNotification);
        when(notificationService.getAllMagasinierNotifications()).thenReturn(notifications);

        mockMvc.perform(get("/PI/notifications/magasiniers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testDeleteNotification_Success() throws Exception {
        mockMvc.perform(delete("/PI/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Notification supprimée"));
    }

    @Test
    void testNotifyTechnicianAssignment_Success() throws Exception {
        doNothing().when(notificationService).notifyTechnicianForAssignment(anyLong(), anyLong(), anyString());

        mockMvc.perform(post("/PI/notifications/assignation-technicien")
                        .param("technicienId", "1")
                        .param("interventionId", "1")
                        .param("interventionDescription", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Notification envoyée au technicien"));
    }

    @Test
    void testNotifyTechnicianAssignment_Error() throws Exception {
        doThrow(new RuntimeException("Error"))
                .when(notificationService).notifyTechnicianForAssignment(anyLong(), anyLong(), anyString());

        mockMvc.perform(post("/PI/notifications/assignation-technicien")
                        .param("technicienId", "1")
                        .param("interventionId", "1")
                        .param("interventionDescription", "Test"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testNotifyChefsSecteurForNewIntervention_Success() throws Exception {
        doNothing().when(notificationService).notifyChefsSecteurForNewIntervention(anyLong(), anyString());

        mockMvc.perform(post("/PI/notifications/nouvelle-intervention")
                        .param("interventionId", "1")
                        .param("interventionDescription", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Notifications envoyées aux chefs de secteur"));
    }

    @Test
    void testNotifyMagasiniersForBonTravailCreation_Success() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("bonTravailId", 1);
        payload.put("interventionId", 1);
        payload.put("description", "Test");
        payload.put("technicianId", 1);
        payload.put("componentCount", 5);
        payload.put("components", "Comp1, Comp2");

        doNothing().when(notificationService).notifyMagasiniersForBonTravailCreation(
                anyLong(), anyLong(), anyString(), anyLong(), anyInt(), anyString());

        mockMvc.perform(post("/PI/notifications/bon-travail-created")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Notifications envoyées aux magasiniers"));
    }
}
