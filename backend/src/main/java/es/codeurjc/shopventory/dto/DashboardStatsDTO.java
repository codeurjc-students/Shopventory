package es.codeurjc.shopventory.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardStatsDTO {

    private long totalProducts;
    private long lowStockProducts;
    private long totalProviders;
    private long totalUsers;
    private long pendingApprovals;
    private long totalOrders;
    private long pendingSales;
    private long pendingPurchases;
    private BigDecimal totalSalesAmount;
    private List<Map<String, Object>> topProducts;
    private List<Map<String, Object>> lowestStockProducts;
    private List<Map<String, Object>> categoryDistribution;

    public long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }

    public long getLowStockProducts() { return lowStockProducts; }
    public void setLowStockProducts(long lowStockProducts) { this.lowStockProducts = lowStockProducts; }

    public long getTotalProviders() { return totalProviders; }
    public void setTotalProviders(long totalProviders) { this.totalProviders = totalProviders; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getPendingApprovals() { return pendingApprovals; }
    public void setPendingApprovals(long pendingApprovals) { this.pendingApprovals = pendingApprovals; }

    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }

    public long getPendingSales() { return pendingSales; }
    public void setPendingSales(long pendingSales) { this.pendingSales = pendingSales; }

    public long getPendingPurchases() { return pendingPurchases; }
    public void setPendingPurchases(long pendingPurchases) { this.pendingPurchases = pendingPurchases; }

    public BigDecimal getTotalSalesAmount() { return totalSalesAmount; }
    public void setTotalSalesAmount(BigDecimal totalSalesAmount) { this.totalSalesAmount = totalSalesAmount; }

    public List<Map<String, Object>> getTopProducts() { return topProducts; }
    public void setTopProducts(List<Map<String, Object>> topProducts) { this.topProducts = topProducts; }

    public List<Map<String, Object>> getLowestStockProducts() { return lowestStockProducts; }
    public void setLowestStockProducts(List<Map<String, Object>> lowestStockProducts) { this.lowestStockProducts = lowestStockProducts; }

    public List<Map<String, Object>> getCategoryDistribution() { return categoryDistribution; }
    public void setCategoryDistribution(List<Map<String, Object>> categoryDistribution) {
        this.categoryDistribution = categoryDistribution;
    }
}
