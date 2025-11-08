package tn.esprit.PI.RestControlleur;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.PI.Services.ProjectService;
import tn.esprit.PI.entity.Project;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController controller;

    private Project testProject;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setId(1L);
        testProject.setProjectName("Test Project");
        testProject.setDescription("Test Description");
    }

    @Test
    void testGetAllProjects_Success() {
        // Arrange
        List<Project> projects = Arrays.asList(testProject);
        when(projectService.getAllProjets()).thenReturn(projects);

        // Act
        ResponseEntity<List<Project>> response = controller.getAllProjects();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Project", response.getBody().get(0).getProjectName());
        verify(projectService, times(1)).getAllProjets();
    }

    @Test
    void testGetAllProjects_Empty() {
        // Arrange
        when(projectService.getAllProjets()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<Project>> response = controller.getAllProjects();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testAddProject_Success() {
        // Arrange
        when(projectService.addProject(any(Project.class))).thenReturn(testProject);

        // Act
        ResponseEntity<Project> response = controller.addProject(testProject);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Project", response.getBody().getProjectName());
        verify(projectService, times(1)).addProject(any(Project.class));
    }

    @Test
    void testAddProject_BadRequest() {
        // Arrange
        when(projectService.addProject(any(Project.class)))
            .thenThrow(new IllegalArgumentException("Invalid project"));

        // Act
        ResponseEntity<Project> response = controller.addProject(testProject);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(projectService, times(1)).addProject(any(Project.class));
    }

    @Test
    void testAddComponentToProject_Success() {
        // Arrange
        when(projectService.addComponentToProject(anyLong(), anyString())).thenReturn(testProject);

        // Act
        ResponseEntity<Project> response = controller.addComponentToProject(1L, "COMP001");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Project", response.getBody().getProjectName());
        verify(projectService, times(1)).addComponentToProject(1L, "COMP001");
    }

    @Test
    void testAddComponentToProject_ProjectNotFound() {
        // Arrange
        when(projectService.addComponentToProject(anyLong(), anyString()))
            .thenThrow(new RuntimeException("Project not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            controller.addComponentToProject(999L, "COMP001");
        });
        verify(projectService, times(1)).addComponentToProject(999L, "COMP001");
    }
}
