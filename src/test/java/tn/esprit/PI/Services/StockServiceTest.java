package tn.esprit.PI.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.PI.entity.Component;
import tn.esprit.PI.repository.ComponentRp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private ComponentRp componentRepository;

    @InjectMocks
    private StockService service;

    private Component testComponent;

    @BeforeEach
    void setUp() {
        testComponent = new Component();
        testComponent.setTrartArticle("COMP001");
        testComponent.setTrartDesignation("Test Component");
        testComponent.setTrartQuantite("100");
    }

    @Test
    void testDecrementComponentStockList_Success() {
        // Arrange
        List<Component> components = Collections.singletonList(testComponent);
        when(componentRepository.save(any(Component.class))).thenReturn(testComponent);

        // Act
        service.decrementComponentStock(components);

        // Assert
        assertEquals("99", testComponent.getTrartQuantite());
        verify(componentRepository, times(1)).save(testComponent);
    }

    @Test
    void testDecrementComponentStockList_MultipleComponents() {
        // Arrange
        Component component2 = new Component();
        component2.setTrartArticle("COMP002");
        component2.setTrartQuantite("50");

        List<Component> components = Arrays.asList(testComponent, component2);
        when(componentRepository.save(any(Component.class))).thenReturn(testComponent);

        // Act
        service.decrementComponentStock(components);

        // Assert
        assertEquals("99", testComponent.getTrartQuantite());
        assertEquals("49", component2.getTrartQuantite());
        verify(componentRepository, times(2)).save(any(Component.class));
    }

    @Test
    void testDecrementComponentStockList_ZeroQuantity() {
        // Arrange
        testComponent.setTrartQuantite("1");
        when(componentRepository.save(any(Component.class))).thenReturn(testComponent);

        // Act
        service.decrementComponentStock(Collections.singletonList(testComponent));

        // Assert
        assertEquals("0", testComponent.getTrartQuantite());
        verify(componentRepository, times(1)).save(testComponent);
    }

    @Test
    void testDecrementComponentStockList_AlreadyZero() {
        // Arrange
        testComponent.setTrartQuantite("0");
        when(componentRepository.save(any(Component.class))).thenReturn(testComponent);

        // Act
        service.decrementComponentStock(Collections.singletonList(testComponent));

        // Assert
        assertEquals("0", testComponent.getTrartQuantite()); // Should stay at 0
        verify(componentRepository, times(1)).save(testComponent);
    }

    @Test
    void testDecrementComponentStockList_NullQuantity() {
        // Arrange
        testComponent.setTrartQuantite(null);

        // Act
        service.decrementComponentStock(Collections.singletonList(testComponent));

        // Assert
        assertNull(testComponent.getTrartQuantite());
        verify(componentRepository, never()).save(any(Component.class));
    }

    @Test
    void testDecrementComponentStockList_EmptyQuantity() {
        // Arrange
        testComponent.setTrartQuantite("");

        // Act
        service.decrementComponentStock(Collections.singletonList(testComponent));

        // Assert
        assertEquals("", testComponent.getTrartQuantite());
        verify(componentRepository, never()).save(any(Component.class));
    }

    @Test
    void testDecrementComponentStockList_InvalidQuantity() {
        // Arrange
        testComponent.setTrartQuantite("invalid");

        // Act
        service.decrementComponentStock(Collections.singletonList(testComponent));

        // Assert
        assertEquals("invalid", testComponent.getTrartQuantite());
        verify(componentRepository, never()).save(any(Component.class));
    }

    @Test
    void testDecrementComponentStockWithQuantity_Success() {
        // Arrange
        when(componentRepository.save(any(Component.class))).thenReturn(testComponent);

        // Act
        service.decrementComponentStock(testComponent, 10);

        // Assert
        assertEquals("90", testComponent.getTrartQuantite());
        verify(componentRepository, times(1)).save(testComponent);
    }

    @Test
    void testDecrementComponentStockWithQuantity_ExceedsAvailable() {
        // Arrange
        when(componentRepository.save(any(Component.class))).thenReturn(testComponent);

        // Act
        service.decrementComponentStock(testComponent, 150);

        // Assert
        assertEquals("0", testComponent.getTrartQuantite()); // Should be 0, not negative
        verify(componentRepository, times(1)).save(testComponent);
    }

    @Test
    void testDecrementComponentStockWithQuantity_NullQuantity() {
        // Arrange
        testComponent.setTrartQuantite(null);

        // Act
        service.decrementComponentStock(testComponent, 10);

        // Assert
        assertNull(testComponent.getTrartQuantite());
        verify(componentRepository, never()).save(any(Component.class));
    }

    @Test
    void testDecrementComponentStockWithQuantity_InvalidQuantity() {
        // Arrange
        testComponent.setTrartQuantite("invalid");

        // Act
        service.decrementComponentStock(testComponent, 10);

        // Assert
        assertEquals("invalid", testComponent.getTrartQuantite());
        verify(componentRepository, never()).save(any(Component.class));
    }

    @Test
    void testHasEnoughStock_True() {
        // Act
        boolean result = service.hasEnoughStock(testComponent, 50);

        // Assert
        assertTrue(result);
    }

    @Test
    void testHasEnoughStock_False() {
        // Act
        boolean result = service.hasEnoughStock(testComponent, 150);

        // Assert
        assertFalse(result);
    }

    @Test
    void testHasEnoughStock_ExactAmount() {
        // Act
        boolean result = service.hasEnoughStock(testComponent, 100);

        // Assert
        assertTrue(result);
    }

    @ParameterizedTest(name = "hasEnoughStock with invalid quantity: {0}")
    @CsvSource({
        "null",
        "''",
        "invalid"
    })
    @NullSource
    void testHasEnoughStock_InvalidQuantities(String quantity) {
        // Arrange
        testComponent.setTrartQuantite("null".equals(quantity) ? null : quantity);

        // Act
        boolean result = service.hasEnoughStock(testComponent, 10);

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetCurrentStock_Success() {
        // Act
        int result = service.getCurrentStock(testComponent);

        // Assert
        assertEquals(100, result);
    }

    @ParameterizedTest(name = "getCurrentStock returns 0 for: {0}")
    @CsvSource({
        "0",
        "null",
        "''",
        "invalid"
    })
    @NullSource
    void testGetCurrentStock_ReturnsZeroForInvalidQuantities(String quantity) {
        // Arrange
        testComponent.setTrartQuantite("null".equals(quantity) ? null : quantity);

        // Act
        int result = service.getCurrentStock(testComponent);

        // Assert
        assertEquals(0, result);
    }
}
