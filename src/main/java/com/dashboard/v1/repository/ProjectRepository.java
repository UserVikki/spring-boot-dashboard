package com.dashboard.v1.repository;

import com.dashboard.v1.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // ✅ Filter projects with pagination
    @Query("SELECT p FROM Project p WHERE " +
            "(:projectId IS NULL OR p.projectIdentifier = :projectId) AND " +
            "(:quota IS NULL OR p.quota = :quota) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:counts IS NULL OR p.counts = :counts) AND " +
            "(:ir IS NULL OR p.ir = :ir)")
    Page<Project> filterProjects(
            @Param("projectId") String projectId,
            @Param("quota") String quota,
            @Param("status") String status,
            @Param("counts") String counts,
            @Param("ir") String ir,
            Pageable pageable
    );

    // ✅ Filter vendor's assigned projects with pagination
    @Query("SELECT p FROM Project p WHERE " +
            ":vendorUsername IS NULL OR :vendorUsername MEMBER OF p.vendorsUsername AND " +
            "(:projectId IS NULL OR p.projectIdentifier = :projectId) AND " +
            "(:quota IS NULL OR p.quota = :quota) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:counts IS NULL OR p.counts = :counts) AND " +
            "(:ir IS NULL OR p.ir = :ir)")
    Page<Project> filterVendorProjects(
            @Param("vendorUsername") String vendorUsername,
            @Param("projectId") String projectId,
            @Param("quota") String quota,
            @Param("status") String status,
            @Param("counts") String counts,
            @Param("ir") String ir,
            Pageable pageable
    );



    Optional<Project> findByProjectIdentifier(String pId);

    @Query("SELECT p FROM Project p order by p.createdAt")
    List<Project> findAll();

    @Query("SELECT p FROM Project p JOIN p.vendorsUsername v WHERE v = :vendorUsername")
    List<Project> findProjectsByVendorUsername(@Param("vendorUsername") String vendorUsername);

}
