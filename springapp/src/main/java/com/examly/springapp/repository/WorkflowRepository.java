package com.examly.springapp.repository;

import com.examly.springapp.model.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    
    List<Workflow> findByStatus(String status);
    
    List<Workflow> findByType(String type);
    
    List<Workflow> findByAssignedTo(String assignedTo);
    
    @Query("SELECT w FROM Workflow w WHERE w.dueDate < :date AND w.status != 'COMPLETED'")
    List<Workflow> findOverdueWorkflows(@Param("date") LocalDateTime date);
    
    @Query("SELECT w FROM Workflow w WHERE w.priority = :priority AND w.status = 'PENDING'")
    List<Workflow> findPendingByPriority(@Param("priority") String priority);
    
    @Query("SELECT w FROM Workflow w WHERE w.blog.id = :blogId")
    List<Workflow> findByBlogId(@Param("blogId") Long blogId);
    
    @Query("SELECT w FROM Workflow w WHERE w.initiator.id = :userId")
    List<Workflow> findByInitiatorId(@Param("userId") Long userId);
}