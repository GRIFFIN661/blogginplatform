package com.examly.springapp.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reader_analytics")
public class ReaderAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private String audienceProfile;
    private String readingPreferences;
    private Double engagementLevel;
    private Double loyaltyScore;
    private Integer sessionDuration;
    private Integer pagesPerSession;
    private String deviceType;
    private String browserType;
    private String referralSource;
    private LocalDateTime lastActivity;
    
    public ReaderAnalytics() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getAudienceProfile() { return audienceProfile; }
    public void setAudienceProfile(String audienceProfile) { this.audienceProfile = audienceProfile; }
    
    public String getReadingPreferences() { return readingPreferences; }
    public void setReadingPreferences(String readingPreferences) { this.readingPreferences = readingPreferences; }
    
    public Double getEngagementLevel() { return engagementLevel; }
    public void setEngagementLevel(Double engagementLevel) { this.engagementLevel = engagementLevel; }
    
    public Double getLoyaltyScore() { return loyaltyScore; }
    public void setLoyaltyScore(Double loyaltyScore) { this.loyaltyScore = loyaltyScore; }
    
    public Integer getSessionDuration() { return sessionDuration; }
    public void setSessionDuration(Integer sessionDuration) { this.sessionDuration = sessionDuration; }
    
    public Integer getPagesPerSession() { return pagesPerSession; }
    public void setPagesPerSession(Integer pagesPerSession) { this.pagesPerSession = pagesPerSession; }
    
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    
    public String getBrowserType() { return browserType; }
    public void setBrowserType(String browserType) { this.browserType = browserType; }
    
    public String getReferralSource() { return referralSource; }
    public void setReferralSource(String referralSource) { this.referralSource = referralSource; }
    
    public LocalDateTime getLastActivity() { return lastActivity; }
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }
}