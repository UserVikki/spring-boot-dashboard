package com.dashboard.v1.repository;

import com.dashboard.v1.entity.SurveyResponse;
import com.dashboard.v1.entity.SurveyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

    @Query("SELECT s FROM SurveyResponse s WHERE s.uId = :uId")
    Optional<SurveyResponse> findByUId(String uId);

    @Query("SELECT s FROM SurveyResponse s WHERE s.projectId = :pId")
    Optional<List<SurveyResponse>> findByProjectId(String pId);

    List<SurveyResponse> findByProjectIdIn(List<String> pIds);


    @Query("SELECT s FROM SurveyResponse s " +
            "WHERE (:pId IS NULL OR s.projectId = :pId) " +
            "AND (:uId IS NULL OR s.uId = :uId) " +
            "AND (:vendorUsername IS NULL OR s.vendorUsername = :vendorUsername) " +
            "AND (:status IS NULL OR s.status = :status) " +
            "AND (:startTime IS NULL OR s.timeStart >= :startTime) " +
            "AND (:endTime IS NULL OR s.timeStart <= :endTime)")
    Page<SurveyResponse> findFilteredSurveys(
            @Param("pId") String pId,
            @Param("uId") String uId,
            @Param("vendorUsername") String vendorUsername,
            @Param("status") SurveyStatus status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable
    );

    @Query("SELECT s FROM SurveyResponse s WHERE s.vendorUsername = :vendorUsername")
    Optional<List<SurveyResponse>> findByVendorUsername(@Param("vendorUsername") String vendorUsername);

    List<SurveyResponse> findByIpAddress(String ipAddress);

}
