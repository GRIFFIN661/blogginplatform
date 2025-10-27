package com.examly.springapp.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "content_metrics")
public class ContentMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "blog_id")
    private Blog blog;
    
    private Integer views;
    private Integer likes;
    private Integer shares;
    private Integer comments;
    private Double engagementRate;
    private Double completionRate;
    private Integer readTime;
    private String geoLocation;
    private LocalDateTime timestamp;
    
    // SEO Metrics
    private Integer organicViews;
    private String keywordRankings;
    private Double seoScore;
    
    public ContentMetrics() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Blog getBlog() { return blog; }
    public void setBlog(Blog blog) { this.blog = blog; }
    
    public Integer getViews() { return views; }
    public void setViews(Integer views) { this.views = views; }
    
    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }
    
    public Integer getShares() { return shares; }
    public void setShares(Integer shares) { this.shares = shares; }
    
    public Integer getComments() { return comments; }
    public void setComments(Integer comments) { this.comments = comments; }
    
    public Double getEngagementRate() { return engagementRate; }
    public void setEngagementRate(Double engagementRate) { this.engagementRate = engagementRate; }
    
    public Double getCompletionRate() { return completionRate; }
    public void setCompletionRate(Double completionRate) { this.completionRate = completionRate; }
    
    public Integer getReadTime() { return readTime; }
    public void setReadTime(Integer readTime) { this.readTime = readTime; }
    
    public String getGeoLocation() { return geoLocation; }
    public void setGeoLocation(String geoLocation) { this.geoLocation = geoLocation; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public Integer getOrganicViews() { return organicViews; }
    public void setOrganicViews(Integer organicViews) { this.organicViews = organicViews; }
    
    public String getKeywordRankings() { return keywordRankings; }
    public void setKeywordRankings(String keywordRankings) { this.keywordRankings = keywordRankings; }
    
    public Double getSeoScore() { return seoScore; }
    public void setSeoScore(Double seoScore) { this.seoScore = seoScore; }
}