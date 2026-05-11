package es.codeurjc.shopventory.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.codeurjc.shopventory.model.Order;
import es.codeurjc.shopventory.model.OrderStatus;
import es.codeurjc.shopventory.model.OrderType;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByType(OrderType type, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Page<Order> findByCreatedById(Long userId, Pageable pageable);

    Page<Order> findByTypeAndStatus(OrderType type, OrderStatus status, Pageable pageable);

    Page<Order> findByProviderId(Long providerId, Pageable pageable);

    @Query("SELECT o FROM OrderTable o WHERE o.orderDate BETWEEN :start AND :end")
    Page<Order> findByOrderDateBetween(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end, Pageable pageable);

    @Query("SELECT COUNT(o) FROM OrderTable o WHERE o.type = :type AND o.status = :status")
    long countByTypeAndStatus(@Param("type") OrderType type, @Param("status") OrderStatus status);
}
