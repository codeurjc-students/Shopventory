package es.codeurjc.shopventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.shopventory.model.Provider;



public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Optional<Provider> findByName(String name);
    Page<Provider> findAllOrderByName(Pageable pageable);

    
}
