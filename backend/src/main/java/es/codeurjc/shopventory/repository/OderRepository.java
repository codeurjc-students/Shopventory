package es.codeurjc.shopventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.shopventory.model.Order;



public interface OderRepository extends JpaRepository<Order, Long>{

    Page<Order> findAllOrderByOrderDate(Pageable pageable);
    Optional<Order> findBy(Long id);

}
