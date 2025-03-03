package com.dashboard.v1.repository;

import com.dashboard.v1.entity.VendorProjectLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface VendorProjectLinkRepository extends JpaRepository<VendorProjectLink, Long> {

    @Query("SELECT v FROM VendorProjectLink v WHERE v.vendorId = :username")
    List<VendorProjectLink> findByVendorUsername(@Param("username") String username);
}
