package com.examly.springapp.model;

import javax.persistence.*;

@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String contentType;
    private Long contentId;
    private String reason;

    public Report() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public Long getContentId() { return contentId; }
    public void setContentId(Long contentId) { this.contentId = contentId; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}