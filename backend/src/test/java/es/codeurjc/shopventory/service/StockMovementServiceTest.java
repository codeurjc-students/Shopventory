package es.codeurjc.shopventory.service;

import es.codeurjc.shopventory.dto.StockUpdateDTO;
import es.codeurjc.shopventory.exception.BadRequestException;
import es.codeurjc.shopventory.exception.ResourceNotFoundException;
import es.codeurjc.shopventory.model.Product;
import es.codeurjc.shopventory.model.StockMovement;
import es.codeurjc.shopventory.model.User;
import es.codeurjc.shopventory.repository.ProductRepository;
import es.codeurjc.shopventory.repository.StockMovementRepository;
import es.codeurjc.shopventory.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockMovementServiceTest {

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private StockMovementService stockMovementService;

    private Product product;
    private User performer;

    @BeforeEach
    void setUp() {
        product = new Product("USB-C Hub", "USB-001", "desc", "short",
                new BigDecimal("19.99"), 50, 5, Set.of("Electronics"));
        product.setId(1L);
        performer = new User("admin@test.com", "pass", "Admin", "User");
    }

    private StockUpdateDTO dto(int quantity) {
        StockUpdateDTO d = new StockUpdateDTO();
        d.setQuantity(quantity);
        d.setReason("manual test");
        return d;
    }

    @Test
    void manualUpdate_positiveQuantity_setsExactStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(performer));
        when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));

        StockMovement movement = stockMovementService.manualUpdate(1L, dto(80), "admin@test.com");

        assertEquals(80, product.getStock());
        assertEquals(30, movement.getQuantity()); // delta = 80 - 50
        assertEquals(50, movement.getStockBefore());
        assertEquals(80, movement.getStockAfter());
        verify(productRepository).save(product);
        verify(emailService, never()).sendLowStockAlert(any(), any(), any(), anyInt(), anyInt());
    }

    @Test
    void manualUpdate_negativeQuantity_reducesStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(performer));
        when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));

        StockMovement movement = stockMovementService.manualUpdate(1L, dto(-10), "admin@test.com");

        assertEquals(40, product.getStock());
        assertEquals(-10, movement.getQuantity());
        verify(productRepository).save(product);
    }

    @Test
    void manualUpdate_insufficientStock_throwsBadRequest() {
        product.setStock(5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(performer));

        assertThrows(BadRequestException.class,
                () -> stockMovementService.manualUpdate(1L, dto(-10), "admin@test.com"));

        verify(productRepository, never()).save(any());
        verify(stockMovementRepository, never()).save(any());
    }

    @Test
    void manualUpdate_productNotFound_throwsResourceNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> stockMovementService.manualUpdate(999L, dto(10), "admin@test.com"));
    }

    @Test
    void manualUpdate_performerNotFound_throwsResourceNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> stockMovementService.manualUpdate(1L, dto(10), "ghost@test.com"));
    }

    @Test
    void manualUpdate_crossingThreshold_sendsLowStockAlert() {
        product.setStock(6); // above threshold of 5
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(performer));
        when(userRepository.findAllAdmins()).thenReturn(List.of(performer));
        when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));

        stockMovementService.manualUpdate(1L, dto(-2), "admin@test.com"); // 6 -> 4, crosses 5

        assertEquals(4, product.getStock());
        verify(emailService).sendLowStockAlert(
                List.of("admin@test.com"), "USB-C Hub", "USB-001", 4, 5);
    }

    @Test
    void manualUpdate_alreadyBelowThreshold_doesNotReAlert() {
        product.setStock(4); // already at/below threshold of 5
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(performer));
        when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));

        stockMovementService.manualUpdate(1L, dto(-1), "admin@test.com"); // 4 -> 3, no crossing

        assertEquals(3, product.getStock());
        verify(emailService, never()).sendLowStockAlert(any(), any(), any(), anyInt(), anyInt());
    }
}
