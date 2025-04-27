package com.dashboard.v1.repository;

import com.dashboard.v1.entity.Client;
import com.dashboard.v1.entity.IsRemoved;
import com.dashboard.v1.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByUsername(String username);


    @Query("SELECT c FROM Client c WHERE c.isShown = :status")
    List<Client> findAll(@Param("status") IsRemoved status);

    @Query("SELECT c FROM Client c WHERE c.username = :username")
    Optional<Client> findByUsername(@Param("username") String username);

}
