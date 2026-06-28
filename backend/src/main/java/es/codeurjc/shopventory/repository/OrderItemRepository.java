package es.codeurjc.shopventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.codeurjc.shopventory.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT oi.product.id, SUM(oi.quantity) as totalQty FROM OrderItemTable oi " +
           "JOIN oi.order o WHERE o.type = 'SALE' AND o.status = 'DELIVERED' " +
           "GROUP BY oi.product.id ORDER BY totalQty DESC")
    List<Object[]> findTopSellingProductIds(@Param("limit") int limit);
}
