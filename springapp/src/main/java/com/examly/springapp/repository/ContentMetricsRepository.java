package com.examly.springapp.repository;

import com.examly.springapp.model.ContentMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContentMetricsRepository extends JpaRepository<ContentMetrics, Long> {
    
    List<ContentMetrics> findByBlogId(Long blogId);
    
    @Query("SELECT cm FROM ContentMetrics cm WHERE cm.timestamp BETWEEN :startDate AND :endDate")
    List<ContentMetrics> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT cm FROM ContentMetrics cm WHERE cm.geoLocation = :location")
    List<ContentMetrics> findByGeoLocation(@Param("location") String location);
    
    @Query("SELECT AVG(cm.engagementRate) FROM ContentMetrics cm WHERE cm.blog.id = :blogId")
    Double getAverageEngagementRate(@Param("blogId") Long blogId);
    
    @Query("SELECT SUM(cm.views) FROM ContentMetrics cm WHERE cm.blog.id = :blogId")
    Long getTotalViews(@Param("blogId") Long blogId);
    
    @Query("SELECT cm FROM ContentMetrics cm ORDER BY cm.engagementRate DESC")
    List<ContentMetrics> findTopPerformingContent();
}