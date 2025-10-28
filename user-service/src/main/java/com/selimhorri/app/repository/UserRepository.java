package com.selimhorri.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.selimhorri.app.domain.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    
    /**
     * Busca un usuario por su username en la tabla de credenciales.
     * Usa LEFT JOIN FETCH para evitar lazy loading y problemas de serialización.
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.credential c WHERE c.username = :username")
    Optional<User> findByCredentialUsername(@Param("username") String username);
    
}