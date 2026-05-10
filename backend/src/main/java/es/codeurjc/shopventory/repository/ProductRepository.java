package es.codeurjc.shopventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.codeurjc.shopventory.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    Page<Product> findByNameContainingIgnoreCaseOrDescriptionShortContainingIgnoreCase(
            String name, String description, Pageable pageable);

    Page<Product> findByCategoriesContaining(String category, Pageable pageable);

    @Query("SELECT p FROM ProductTable p WHERE p.stock <= p.minStockThreshold")
    Page<Product> findLowStockProducts(Pageable pageable);

    Page<Product> findByProviderId(Long providerId, Pageable pageable);
}
