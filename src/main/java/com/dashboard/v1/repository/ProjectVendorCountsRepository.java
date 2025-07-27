package com.dashboard.v1.repository;

import com.dashboard.v1.entity.ProjectVendorCounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectVendorCountsRepository extends JpaRepository<ProjectVendorCounts, Long> {


    Optional<ProjectVendorCounts> findByVendorUsernameAndProjectId(String vendorUsername, String projectId);

}
