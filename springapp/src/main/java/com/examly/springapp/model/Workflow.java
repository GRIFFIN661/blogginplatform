package com.examly.springapp.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workflows")
public class Workflow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String type; // CONTENT_REVIEW, MODERATION, PUBLICATION
    private String status; // PENDING, IN_PROGRESS, COMPLETED, REJECTED
    private String currentStep;
    private String assignedTo;
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    
    @ManyToOne
    @JoinColumn(name = "blog_id")
    private Blog blog;
    
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
    private String workflowData; // JSON for workflow-specific data
    private String comments;
    
    public Workflow() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCurrentStep() { return currentStep; }
    public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
    
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public Blog getBlog() { return blog; }
    public void setBlog(Blog blog) { this.blog = blog; }
    
    public User getInitiator() { return initiator; }
    public void setInitiator(User initiator) { this.initiator = initiator; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    
    public String getWorkflowData() { return workflowData; }
    public void setWorkflowData(String workflowData) { this.workflowData = workflowData; }
    
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}