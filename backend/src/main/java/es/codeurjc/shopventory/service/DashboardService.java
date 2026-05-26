package es.codeurjc.shopventory.service;

import es.codeurjc.shopventory.dto.DashboardStatsDTO;
import es.codeurjc.shopventory.model.OrderStatus;
import es.codeurjc.shopventory.model.OrderType;
import es.codeurjc.shopventory.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final ProductRepository productRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public DashboardService(ProductRepository productRepository, ProviderRepository providerRepository,
                            UserRepository userRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.providerRepository = providerRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public DashboardStatsDTO getStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        stats.setTotalProducts(productRepository.count());
        stats.setLowStockProducts(productRepository.countLowStockProducts());
        stats.setTotalProviders(providerRepository.count());
        stats.setTotalUsers(userRepository.count());
        stats.setPendingApprovals(userRepository.findByApprovedFalse().size());
        stats.setTotalOrders(orderRepository.count());
        stats.setPendingSales(orderRepository.countByTypeAndStatus(OrderType.SALE, OrderStatus.PENDING));
        stats.setPendingPurchases(orderRepository.countByTypeAndStatus(OrderType.PURCHASE, OrderStatus.PENDING));

        BigDecimal totalSales = orderRepository.sumTotalAmountByTypeAndStatusIn(
                OrderType.SALE, List.of(OrderStatus.CONFIRMED, OrderStatus.DELIVERED));
        stats.setTotalSalesAmount(totalSales != null ? totalSales : BigDecimal.ZERO);

        stats.setTopProducts(getTopProducts());
        stats.setCategoryDistribution(getCategoryDistribution());

        return stats;
    }

    private List<Map<String, Object>> getTopProducts() {
        List<Map<String, Object>> topProducts = new ArrayList<>();
        productRepository.findTop5ByOrderByStockDesc().forEach(p -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", p.getId());
            entry.put("name", p.getName());
            entry.put("stock", p.getStock());
            entry.put("price", p.getPrice());
            entry.put("lowStock", p.isLowStock());
            topProducts.add(entry);
        });
        return topProducts;
    }

    private List<Map<String, Object>> getCategoryDistribution() {
        List<Map<String, Object>> result = new ArrayList<>();
        productRepository.findCategoryDistribution().forEach(row -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("category", row[0]);
            entry.put("count", row[1]);
            result.add(entry);
        });
        return result;
    }
}
