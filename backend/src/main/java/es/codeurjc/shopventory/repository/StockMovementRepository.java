package es.codeurjc.shopventory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.shopventory.model.StockMovement;
import es.codeurjc.shopventory.model.StockMovementType;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    Page<StockMovement> findByProductId(Long productId, Pageable pageable);

    Page<StockMovement> findByMovementType(StockMovementType type, Pageable pageable);

    Page<StockMovement> findByProductIdOrderByDateDesc(Long productId, Pageable pageable);
}
