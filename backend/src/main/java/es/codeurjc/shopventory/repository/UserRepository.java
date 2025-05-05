package es.codeurjc.shopventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.shopventory.model.User;



public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);

}
