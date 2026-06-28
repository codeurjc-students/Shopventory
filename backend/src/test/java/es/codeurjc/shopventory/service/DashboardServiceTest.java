package es.codeurjc.shopventory.service;

import es.codeurjc.shopventory.dto.DashboardStatsDTO;
import es.codeurjc.shopventory.model.OrderStatus;
import es.codeurjc.shopventory.model.OrderType;
import es.codeurjc.shopventory.model.Product;
import es.codeurjc.shopventory.model.User;
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
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProviderRepository providerRepository;
    @Mock private UserRepository userRepository;
    @Mock private OrderRepository orderRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private Product topProduct;
    private Product lowProduct;

    @BeforeEach
    void setUp() {
        topProduct = new Product("Top", "TOP-1", null, null, new BigDecimal("9.99"), 100, 5, Set.of());
        topProduct.setId(1L);
        lowProduct = new Product("Low", "LOW-1", null, null, new BigDecimal("4.99"), 1, 5, Set.of());
        lowProduct.setId(2L);
    }

    /** Stubs every repository call getStats() relies on, except the sales sum. */
    private void stubCommon() {
        when(productRepository.count()).thenReturn(10L);
        when(productRepository.countLowStockProducts()).thenReturn(3L);
        when(providerRepository.count()).thenReturn(4L);
        when(userRepository.count()).thenReturn(7L);
        when(userRepository.findByApprovedFalse()).thenReturn(List.of(new User(), new User()));
        when(orderRepository.count()).thenReturn(20L);
        when(orderRepository.countByTypeAndStatus(OrderType.SALE, OrderStatus.PENDING)).thenReturn(5L);
        when(orderRepository.countByTypeAndStatus(OrderType.PURCHASE, OrderStatus.PENDING)).thenReturn(2L);
        when(productRepository.findTop5ByOrderByStockDesc()).thenReturn(List.of(topProduct));
        when(productRepository.findTop5ByOrderByStockAsc()).thenReturn(List.of(lowProduct));
        when(productRepository.findCategoryDistribution())
                .thenReturn(List.<Object[]>of(new Object[]{"Electronics", 5L}));
    }

    @Test
    void getStats_aggregatesAllMetrics() {
        stubCommon();
        when(orderRepository.sumTotalAmountByTypeAndStatusIn(eq(OrderType.SALE), anyList()))
                .thenReturn(new BigDecimal("1500.00"));

        DashboardStatsDTO stats = dashboardService.getStats();

        assertEquals(10, stats.getTotalProducts());
        assertEquals(3, stats.getLowStockProducts());
        assertEquals(4, stats.getTotalProviders());
        assertEquals(7, stats.getTotalUsers());
        assertEquals(2, stats.getPendingApprovals());
        assertEquals(20, stats.getTotalOrders());
        assertEquals(5, stats.getPendingSales());
        assertEquals(2, stats.getPendingPurchases());
        assertEquals(0, new BigDecimal("1500.00").compareTo(stats.getTotalSalesAmount()));
    }

    @Test
    void getStats_nullSalesSum_defaultsToZero() {
        stubCommon();
        when(orderRepository.sumTotalAmountByTypeAndStatusIn(eq(OrderType.SALE), anyList()))
                .thenReturn(null);

        DashboardStatsDTO stats = dashboardService.getStats();

        assertEquals(0, BigDecimal.ZERO.compareTo(stats.getTotalSalesAmount()));
    }

    @Test
    void getStats_mapsTopAndLowestProducts() {
        stubCommon();
        when(orderRepository.sumTotalAmountByTypeAndStatusIn(eq(OrderType.SALE), anyList()))
                .thenReturn(BigDecimal.TEN);

        DashboardStatsDTO stats = dashboardService.getStats();

        assertEquals(1, stats.getTopProducts().size());
        Map<String, Object> top = stats.getTopProducts().get(0);
        assertEquals(1L, top.get("id"));
        assertEquals("Top", top.get("name"));
        assertEquals(100, top.get("stock"));
        assertEquals(false, top.get("lowStock")); // 100 > 5

        Map<String, Object> low = stats.getLowestStockProducts().get(0);
        assertEquals("Low", low.get("name"));
        assertEquals(true, low.get("lowStock")); // 1 <= 5
    }

    @Test
    void getStats_mapsCategoryDistribution() {
        stubCommon();
        when(orderRepository.sumTotalAmountByTypeAndStatusIn(eq(OrderType.SALE), anyList()))
                .thenReturn(BigDecimal.ZERO);

        DashboardStatsDTO stats = dashboardService.getStats();

        assertEquals(1, stats.getCategoryDistribution().size());
        Map<String, Object> entry = stats.getCategoryDistribution().get(0);
        assertEquals("Electronics", entry.get("category"));
        assertEquals(5L, entry.get("count"));
    }
}
