package com.examly.springapp.repository;

import com.examly.springapp.model.ReaderAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReaderAnalyticsRepository extends JpaRepository<ReaderAnalytics, Long> {
    
    List<ReaderAnalytics> findByUserId(Long userId);
    
    @Query("SELECT ra FROM ReaderAnalytics ra WHERE ra.lastActivity >= :since")
    List<ReaderAnalytics> findActiveReaders(@Param("since") LocalDateTime since);
    
    @Query("SELECT AVG(ra.engagementLevel) FROM ReaderAnalytics ra")
    Double getAverageEngagementLevel();
    
    @Query("SELECT AVG(ra.loyaltyScore) FROM ReaderAnalytics ra")
    Double getAverageLoyaltyScore();
    
    @Query("SELECT ra FROM ReaderAnalytics ra ORDER BY ra.loyaltyScore DESC")
    List<ReaderAnalytics> findTopLoyalReaders();
    
    @Query("SELECT ra.deviceType, COUNT(ra) FROM ReaderAnalytics ra GROUP BY ra.deviceType")
    List<Object[]> getDeviceTypeDistribution();
}