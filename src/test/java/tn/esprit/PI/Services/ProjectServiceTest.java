package tn.esprit.PI.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.PI.entity.Component;
import tn.esprit.PI.entity.Project;
import tn.esprit.PI.entity.ProjetDTO;
import tn.esprit.PI.repository.ComponentRp;
import tn.esprit.PI.repository.ProjectRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ComponentRp componentRepository;

    @Mock
    private ComponentService componentService;

    @InjectMocks
    private ProjectService projectService;

    private Project testProject;
    private Component testComponent;
    private ProjetDTO testProjetDTO;

    @BeforeEach
    void setUp() {
        // Setup test component
        testComponent = new Component();
        testComponent.setTrartArticle("COMP001");
        testComponent.setTrartDesignation("Test Component");
        testComponent.setTrartFamille("Family A");

        // Setup test project
        testProject = new Project();
        testProject.setId(1L);
        testProject.setProjectName("Test Project");
        testProject.setProjectManagerName("John Doe");
        testProject.setDescription("Test Description");
        testProject.setBudget(10000.0f);
        testProject.setDate(new Date());
        testProject.setComponents(new ArrayList<>(Arrays.asList(testComponent)));

        // Setup test ProjetDTO
        testProjetDTO = new ProjetDTO();
        testProjetDTO.setNomProjet("New Project");
        testProjetDTO.setNomChefProjet("Jane Smith");
        testProjetDTO.setDescription("New Description");
        testProjetDTO.setBudget(15000.0f);
        testProjetDTO.setDate(new Date());
        testProjetDTO.setComponents(Arrays.asList("COMP001", "COMP002"));
    }

    @Test
    void testCreateProjetFromDTO_Success() {
        // Arrange
        Component component2 = new Component();
        component2.setTrartArticle("COMP002");
        component2.setTrartDesignation("Component 2");

        when(componentService.findByTrartArticle("COMP001")).thenReturn(testComponent);
        when(componentService.findByTrartArticle("COMP002")).thenReturn(component2);
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // Act
        Project result = projectService.createProjetFromDTO(testProjetDTO);

        // Assert
        assertNotNull(result);
        verify(componentService, times(1)).findByTrartArticle("COMP001");
        verify(componentService, times(1)).findByTrartArticle("COMP002");
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void testCreateProjetFromDTO_WithEmptyComponents() {
        // Arrange
        testProjetDTO.setComponents(new ArrayList<>());
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // Act
        Project result = projectService.createProjetFromDTO(testProjetDTO);

        // Assert
        assertNotNull(result);
        verify(componentService, never()).findByTrartArticle(anyString());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void testGetAllProjets_Success() {
        // Arrange
        Project project2 = new Project();
        project2.setId(2L);
        project2.setProjectName("Project 2");
        project2.setProjectManagerName("Bob Smith");

        List<Project> projects = Arrays.asList(testProject, project2);
        when(projectRepository.findAll()).thenReturn(projects);

        // Act
        List<Project> result = projectService.getAllProjets();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void testGetAllProjets_EmptyList() {
        // Arrange
        when(projectRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<Project> result = projectService.getAllProjets();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void testAddComponentToProject_Success() {
        // Arrange
        Component newComponent = new Component();
        newComponent.setTrartArticle("COMP003");
        newComponent.setTrartDesignation("New Component");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(componentRepository.findById("COMP003")).thenReturn(Optional.of(newComponent));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // Act
        Project result = projectService.addComponentToProject(1L, "COMP003");

        // Assert
        assertNotNull(result);
        verify(projectRepository, times(1)).findById(1L);
        verify(componentRepository, times(1)).findById("COMP003");
        verify(projectRepository, times(1)).save(testProject);
    }

    @Test
    void testAddComponentToProject_ComponentAlreadyExists() {
        // Arrange
        testProject.setComponents(new ArrayList<>(Arrays.asList(testComponent)));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(componentRepository.findById("COMP001")).thenReturn(Optional.of(testComponent));

        // Act
        Project result = projectService.addComponentToProject(1L, "COMP001");

        // Assert
        assertNotNull(result);
        verify(projectRepository, times(1)).findById(1L);
        verify(componentRepository, times(1)).findById("COMP001");
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testAddComponentToProject_ProjectNotFound() {
        // Arrange
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            projectService.addComponentToProject(999L, "COMP001");
        });
        verify(projectRepository, times(1)).findById(999L);
        verify(componentRepository, never()).findById(anyString());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testAddComponentToProject_ComponentNotFound() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(componentRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            projectService.addComponentToProject(1L, "NONEXISTENT");
        });
        verify(projectRepository, times(1)).findById(1L);
        verify(componentRepository, times(1)).findById("NONEXISTENT");
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testAddProject_Success() {
        // Arrange
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // Act
        Project result = projectService.addProject(testProject);

        // Assert
        assertNotNull(result);
        assertEquals("Test Project", result.getProjectName());
        assertEquals("John Doe", result.getProjectManagerName());
        verify(projectRepository, times(1)).save(testProject);
    }

    @Test
    void testAddProject_ThrowsException_WhenProjectNameIsNull() {
        // Arrange
        testProject.setProjectName(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            projectService.addProject(testProject);
        });
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testAddProject_ThrowsException_WhenProjectNameIsEmpty() {
        // Arrange
        testProject.setProjectName("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            projectService.addProject(testProject);
        });
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testAddProject_ThrowsException_WhenManagerNameIsNull() {
        // Arrange
        testProject.setProjectManagerName(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            projectService.addProject(testProject);
        });
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testAddProject_ThrowsException_WhenManagerNameIsEmpty() {
        // Arrange
        testProject.setProjectManagerName("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            projectService.addProject(testProject);
        });
        verify(projectRepository, never()).save(any());
    }
}
