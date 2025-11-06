package tn.esprit.PI.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.PI.entity.Component;
import tn.esprit.PI.repository.ComponentRp;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComponentServiceTest {

    @Mock
    private ComponentRp componentRp;

    @InjectMocks
    private ComponentService componentService;

    private Component testComponent;

    @BeforeEach
    void setUp() {
        testComponent = new Component();
        testComponent.setTrartArticle("TEST001");
        testComponent.setTrartDesignation("Test Component");
        testComponent.setTrartFamille("Family A");
        testComponent.setTrartSousFamille("SubFamily 1");
        testComponent.setTrartMarque("Brand X");
        testComponent.setTrartQuantite("10");
        testComponent.setTrartSociete("Company");
        testComponent.setTrartUnite("Unit");
        testComponent.setTrartTransact("Trans");
        testComponent.setTrartTva("20");
    }

    @Test
    void testAddOrIncrement_Success() {
        // Arrange
        when(componentRp.save(any(Component.class))).thenReturn(testComponent);

        // Act
        Component result = componentService.addOrIncrement(testComponent);

        // Assert
        assertNotNull(result);
        assertEquals("TEST001", result.getTrartArticle());
        verify(componentRp, times(1)).save(testComponent);
    }

    @Test
    void testAddOrIncrement_ThrowsException_WhenComponentIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            componentService.addOrIncrement(null);
        });
        verify(componentRp, never()).save(any());
    }

    @Test
    void testUpdateCompo_Success() {
        // Arrange
        Component updatedComponent = new Component();
        updatedComponent.setTrartDesignation("Updated Component");
        updatedComponent.setTrartFamille("Family B");
        updatedComponent.setTrartQuantite("20");

        when(componentRp.findByTrartArticle("TEST001")).thenReturn(Optional.of(testComponent));
        when(componentRp.save(any(Component.class))).thenReturn(testComponent);

        // Act
        Component result = componentService.updateCompo("TEST001", updatedComponent);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Component", result.getTrartDesignation());
        assertEquals("Family B", result.getTrartFamille());
        verify(componentRp, times(1)).findByTrartArticle("TEST001");
        verify(componentRp, times(1)).save(any(Component.class));
    }

    @Test
    void testUpdateCompo_ReturnsNull_WhenComponentNotFound() {
        // Arrange
        Component updatedComponent = new Component();
        when(componentRp.findByTrartArticle("NONEXISTENT")).thenReturn(Optional.empty());

        // Act
        Component result = componentService.updateCompo("NONEXISTENT", updatedComponent);

        // Assert
        assertNull(result);
        verify(componentRp, times(1)).findByTrartArticle("NONEXISTENT");
        verify(componentRp, never()).save(any());
    }

    @Test
    void testRetrieveComp_Success() {
        // Arrange
        Component component2 = new Component();
        component2.setTrartArticle("TEST002");
        component2.setTrartDesignation("Test Component 2");

        List<Component> components = Arrays.asList(testComponent, component2);
        when(componentRp.findAll()).thenReturn(components);

        // Act
        List<Component> result = componentService.retrievecomp();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(componentRp, times(1)).findAll();
    }

    @Test
    void testDeleteCompByTRART_ARTICLE_Success() {
        // Arrange
        when(componentRp.findByTrartArticle("TEST001")).thenReturn(Optional.of(testComponent));
        doNothing().when(componentRp).delete(any(Component.class));

        // Act
        Boolean result = componentService.deleteCompByTRART_ARTICLE("TEST001");

        // Assert
        assertTrue(result);
        verify(componentRp, times(1)).findByTrartArticle("TEST001");
        verify(componentRp, times(1)).delete(testComponent);
    }

    @Test
    void testDeleteCompByTRART_ARTICLE_ReturnsFalse_WhenNotFound() {
        // Arrange
        when(componentRp.findByTrartArticle("NONEXISTENT")).thenReturn(Optional.empty());

        // Act
        Boolean result = componentService.deleteCompByTRART_ARTICLE("NONEXISTENT");

        // Assert
        assertFalse(result);
        verify(componentRp, times(1)).findByTrartArticle("NONEXISTENT");
        verify(componentRp, never()).delete(any());
    }

    @Test
    void testFindByTrartArticle_Success() {
        // Arrange
        when(componentRp.findByTrartArticle("TEST001")).thenReturn(Optional.of(testComponent));

        // Act
        Component result = componentService.findByTrartArticle("TEST001");

        // Assert
        assertNotNull(result);
        assertEquals("TEST001", result.getTrartArticle());
        verify(componentRp, times(1)).findByTrartArticle("TEST001");
    }

    @Test
    void testFindByTrartArticle_ThrowsException_WhenNotFound() {
        // Arrange
        when(componentRp.findByTrartArticle("NONEXISTENT")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            componentService.findByTrartArticle("NONEXISTENT");
        });
        verify(componentRp, times(1)).findByTrartArticle("NONEXISTENT");
    }

    @Test
    void testSearchComponents_Success() {
        // Arrange
        List<Component> components = Arrays.asList(testComponent);
        when(componentRp.searchComponents(
                any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(),
                any(), any(), any()
        )).thenReturn(components);

        // Act
        List<Component> result = componentService.searchComponents(
                "TEST001", null, null, "Test", null,
                null, null, null, null, null,
                null, null, null
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(componentRp, times(1)).searchComponents(
                any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(),
                any(), any(), any()
        );
    }
}
