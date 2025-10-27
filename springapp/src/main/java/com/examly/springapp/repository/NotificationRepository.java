package com.examly.springapp.repository;

import com.examly.springapp.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT n FROM Notification n WHERE n.type = :type AND n.isDelivered = false")
    List<Notification> findUndeliveredByType(@Param("type") String type);
    
    @Query("SELECT n FROM Notification n WHERE n.category = :category AND n.createdAt >= :since")
    List<Notification> findByCategoryAndCreatedAfter(@Param("category") String category, 
                                                   @Param("since") LocalDateTime since);
    
    @Query("SELECT n FROM Notification n WHERE n.priority = :priority AND n.isRead = false")
    List<Notification> findUnreadByPriority(@Param("priority") String priority);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    Long countUnreadByUserId(@Param("userId") Long userId);
}