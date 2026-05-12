package es.codeurjc.shopventory.service;

import es.codeurjc.shopventory.dto.DashboardStatsDTO;
import es.codeurjc.shopventory.model.OrderStatus;
import es.codeurjc.shopventory.model.OrderType;
import es.codeurjc.shopventory.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        Pageable all = PageRequest.of(0, Integer.MAX_VALUE);
        stats.setLowStockProducts(productRepository.findLowStockProducts(all).getTotalElements());
        stats.setTotalProviders(providerRepository.count());
        stats.setTotalUsers(userRepository.count());
        stats.setPendingApprovals(userRepository.findByApprovedFalse().size());
        stats.setTotalOrders(orderRepository.count());
        stats.setPendingSales(orderRepository.countByTypeAndStatus(OrderType.SALE, OrderStatus.PENDING));
        stats.setPendingPurchases(orderRepository.countByTypeAndStatus(OrderType.PURCHASE, OrderStatus.PENDING));

        // Total sales revenue from confirmed/delivered orders
        BigDecimal totalSales = orderRepository.findAll().stream()
                .filter(o -> o.getType() == OrderType.SALE
                        && (o.getStatus() == OrderStatus.CONFIRMED || o.getStatus() == OrderStatus.DELIVERED))
                .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalSalesAmount(totalSales);

        stats.setTopProducts(getTopProducts());
        stats.setCategoryDistribution(getCategoryDistribution());

        return stats;
    }

    private List<Map<String, Object>> getTopProducts() {
        List<Map<String, Object>> topProducts = new ArrayList<>();
        productRepository.findAll(PageRequest.of(0, 5, org.springframework.data.domain.Sort.by("stock").descending()))
                .forEach(p -> {
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
        Map<String, Long> counts = new HashMap<>();
        productRepository.findAll().forEach(product ->
                product.getCategories().forEach(cat ->
                        counts.merge(cat, 1L, Long::sum)));

        List<Map<String, Object>> result = new ArrayList<>();
        counts.forEach((cat, count) -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("category", cat);
            entry.put("count", count);
            result.add(entry);
        });
        return result;
    }
}
