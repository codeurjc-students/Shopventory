package es.codeurjc.shopventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.shopventory.model.Product;


public interface ProductRepository  extends JpaRepository<Product, Long>{

    Optional<Product> findByName(String name);
    Page<Product> findAllOrderByName(Pageable pageable);
    
}
