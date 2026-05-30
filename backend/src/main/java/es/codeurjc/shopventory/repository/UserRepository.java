package es.codeurjc.shopventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.shopventory.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByApproved(boolean approved, Pageable pageable);

    List<User> findByApprovedFalse();

    @org.springframework.data.jpa.repository.Query("SELECT u FROM UserTable u JOIN u.roles r WHERE r = 'ADMIN'")
    List<User> findAllAdmins();

    Page<User> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String surname, String email, Pageable pageable);
}
