package es.codeurjc.shopventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.shopventory.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Employee> findByUserId(Long userId);

    Page<Employee> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(
            String name, String surname, Pageable pageable);
}
