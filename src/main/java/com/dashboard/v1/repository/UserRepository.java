package com.dashboard.v1.repository;


import com.dashboard.v1.entity.Role;
import com.dashboard.v1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<List<User>> findByRole(Role role);
    boolean existsByUsername(String username);
    @Query("SELECT u FROM User u WHERE u.role = 'VENDOR'")
    List<User> findAllVendors();

    @Query("SELECT u FROM User u WHERE u.userToken = :token")
    Optional<User> findByToken(@Param("token") String token);

}
