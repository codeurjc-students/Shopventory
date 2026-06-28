package es.codeurjc.shopventory.service;

import es.codeurjc.shopventory.dto.OrderDTO;
import es.codeurjc.shopventory.dto.OrderItemDTO;
import es.codeurjc.shopventory.exception.BadRequestException;
import es.codeurjc.shopventory.exception.ResourceNotFoundException;
import es.codeurjc.shopventory.model.*;
import es.codeurjc.shopventory.repository.OrderRepository;
import es.codeurjc.shopventory.repository.ProductRepository;
import es.codeurjc.shopventory.repository.ProviderRepository;
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
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private ProviderRepository providerRepository;
    @Mock private UserRepository userRepository;
    @Mock private StockMovementService stockMovementService;
    @Mock private EmailService emailService;

    @InjectMocks
    private OrderService orderService;

    private User creator;
    private Product product;

    @BeforeEach
    void setUp() {
        creator = new User("admin@test.com", "pass", "Admin", "User");
        product = new Product("USB-C Hub", "USB-001", "desc", "short",
                new BigDecimal("19.99"), 50, 5, Set.of("Electronics"));
        product.setId(10L);
    }

    private OrderDTO orderDto(OrderType type, Long productId, int qty) {
        OrderDTO dto = new OrderDTO();
        dto.setType(type);
        OrderItemDTO item = new OrderItemDTO();
        item.setProductId(productId);
        item.setQuantity(qty);
        dto.setItems(List.of(item));
        return dto;
    }

    private Order pendingOrder(OrderType type, Product p, int qty) {
        Order order = new Order(type, creator);
        order.setId(1L);
        order.getItems().add(new OrderItem(order, p, qty, p.getPrice()));
        return order;
    }

    // ---------- create ----------

    @Test
    void create_saleOrder_success() {
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(creator));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.create(orderDto(OrderType.SALE, 10L, 5), "admin@test.com");

        assertEquals(OrderType.SALE, result.getType());
        assertEquals(1, result.getItems().size());
        assertEquals(0, new BigDecimal("99.95").compareTo(result.getTotalAmount())); // 19.99 * 5
    }

    @Test
    void create_saleInsufficientStock_throwsBadRequest() {
        product.setStock(2);
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(creator));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        assertThrows(BadRequestException.class,
                () -> orderService.create(orderDto(OrderType.SALE, 10L, 5), "admin@test.com"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void create_purchaseOrder_skipsStockCheck() {
        product.setStock(2);
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(creator));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.create(orderDto(OrderType.PURCHASE, 10L, 100), "admin@test.com");

        assertEquals(OrderType.PURCHASE, result.getType());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void create_creatorNotFound_throwsResourceNotFound() {
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.create(orderDto(OrderType.SALE, 10L, 1), "ghost@test.com"));
    }

    @Test
    void create_productNotFound_throwsResourceNotFound() {
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(creator));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.create(orderDto(OrderType.SALE, 99L, 1), "admin@test.com"));
    }

    @Test
    void create_withProvider_resolvesProvider() {
        Provider provider = new Provider("Acme", null, null, null, null, null, Set.of());
        provider.setId(5L);
        OrderDTO dto = orderDto(OrderType.PURCHASE, 10L, 3);
        dto.setProviderId(5L);
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(creator));
        when(providerRepository.findById(5L)).thenReturn(Optional.of(provider));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.create(dto, "admin@test.com");

        assertSame(provider, result.getProvider());
    }

    // ---------- confirm ----------

    @Test
    void confirm_saleOrder_deductsStock() {
        Order order = pendingOrder(OrderType.SALE, product, 10);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(creator));
        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.confirm(1L, "admin@test.com");

        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        assertEquals(40, product.getStock()); // 50 - 10
        verify(stockMovementService).recordMovement(eq(product), eq(-10), eq(50), eq(40),
                eq(StockMovementType.SALE_OUT), anyString(), eq(creator));
        verify(emailService, never()).sendLowStockAlert(any(), any(), any(), anyInt(), anyInt());
    }

    @Test
    void confirm_purchaseOrder_addsStock() {
        product.setStock(2);
        Order order = pendingOrder(OrderType.PURCHASE, product, 100);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(creator));
        when(orderRepository.save(order)).thenReturn(order);

        orderService.confirm(1L, "admin@test.com");

        assertEquals(102, product.getStock()); // 2 + 100
        verify(stockMovementService).recordMovement(eq(product), eq(100), eq(2), eq(102),
                eq(StockMovementType.PURCHASE_IN), anyString(), eq(creator));
    }

    @Test
    void confirm_notPending_throwsBadRequest() {
        Order order = pendingOrder(OrderType.SALE, product, 5);
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> orderService.confirm(1L, "admin@test.com"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void confirm_saleInsufficientStockAtConfirmation_throwsBadRequest() {
        product.setStock(5);
        Order order = pendingOrder(OrderType.SALE, product, 10);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(creator));
        when(orderRepository.save(order)).thenReturn(order);

        assertThrows(BadRequestException.class, () -> orderService.confirm(1L, "admin@test.com"));
        verify(productRepository, never()).save(any());
        verify(stockMovementService, never()).recordMovement(any(), anyInt(), anyInt(), anyInt(), any(), any(), any());
    }

    @Test
    void confirm_saleCrossingThreshold_sendsLowStockAlert() {
        product.setStock(6); // above threshold 5
        Order order = pendingOrder(OrderType.SALE, product, 2); // 6 -> 4
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(creator));
        when(orderRepository.save(order)).thenReturn(order);
        when(userRepository.findAllAdmins()).thenReturn(List.of(creator));

        orderService.confirm(1L, "admin@test.com");

        assertEquals(4, product.getStock());
        verify(emailService).sendLowStockAlert(
                List.of("admin@test.com"), "USB-C Hub", "USB-001", 4, 5);
    }

    @Test
    void confirm_orderNotFound_throwsResourceNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.confirm(999L, "admin@test.com"));
    }

    // ---------- deliver / cancel / delete ----------

    @Test
    void deliver_confirmedOrder_success() {
        Order order = pendingOrder(OrderType.SALE, product, 1);
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.deliver(1L);

        assertEquals(OrderStatus.DELIVERED, result.getStatus());
        assertNotNull(result.getDeliveryDate());
    }

    @Test
    void deliver_notConfirmed_throwsBadRequest() {
        Order order = pendingOrder(OrderType.SALE, product, 1); // PENDING
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> orderService.deliver(1L));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void cancel_pendingOrder_success() {
        Order order = pendingOrder(OrderType.SALE, product, 1);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.cancel(1L);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
    }

    @Test
    void cancel_deliveredOrder_throwsBadRequest() {
        Order order = pendingOrder(OrderType.SALE, product, 1);
        order.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> orderService.cancel(1L));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void delete_notFound_throwsResourceNotFound() {
        when(orderRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> orderService.delete(999L));
        verify(orderRepository, never()).deleteById(any());
    }

    @Test
    void delete_existingOrder_success() {
        when(orderRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> orderService.delete(1L));
        verify(orderRepository).deleteById(1L);
    }
}
