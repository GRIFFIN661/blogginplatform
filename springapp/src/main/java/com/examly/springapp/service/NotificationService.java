package com.examly.springapp.service;

import com.examly.springapp.model.Notification;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    // Multi-channel notification delivery
    public void sendNotification(User user, String type, String category, String title, String message, String priority) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setCategory(category);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setPriority(priority);
        
        // Deliver based on user preferences and notification type
        deliverNotification(notification);
        
        notificationRepository.save(notification);
    }
    
    // Content notifications
    public void sendContentNotification(User user, String eventType, String contentTitle) {
        String title = "";
        String message = "";
        
        switch (eventType) {
            case "NEW_BLOG":
                title = "New Blog Published";
                message = "A new blog '" + contentTitle + "' has been published";
                break;
            case "NEW_COMMENT":
                title = "New Comment";
                message = "Someone commented on '" + contentTitle + "'";
                break;
            case "BLOG_LIKED":
                title = "Blog Liked";
                message = "Your blog '" + contentTitle + "' received a new like";
                break;
        }
        
        sendNotification(user, "IN_APP", "CONTENT", title, message, "MEDIUM");
    }
    
    // Community notifications
    public void sendCommunityNotification(User user, String eventType, String details) {
        String title = "";
        String message = "";
        String priority = "MEDIUM";
        
        switch (eventType) {
            case "CONTENT_REPORTED":
                title = "Content Reported";
                message = "Content has been reported: " + details;
                priority = "HIGH";
                break;
            case "POLICY_VIOLATION":
                title = "Policy Violation";
                message = "Policy violation detected: " + details;
                priority = "HIGH";
                break;
            case "MODERATION_REQUIRED":
                title = "Moderation Required";
                message = "Content requires moderation: " + details;
                priority = "HIGH";
                break;
        }
        
        sendNotification(user, "EMAIL", "COMMUNITY", title, message, priority);
    }
    
    // Platform announcements
    public void sendPlatformAnnouncement(String title, String message, String priority) {
        // Send to all users or specific user groups
        // This would typically involve querying all users and sending notifications
        Notification announcement = new Notification();
        announcement.setType("PLATFORM");
        announcement.setCategory("PLATFORM");
        announcement.setTitle(title);
        announcement.setMessage(message);
        announcement.setPriority(priority);
        
        // Broadcast logic would go here
        notificationRepository.save(announcement);
    }
    
    // Emergency notifications
    public void sendEmergencyNotification(String title, String message) {
        sendPlatformAnnouncement(title, message, "URGENT");
        
        // Additional emergency delivery channels
        deliverEmergencyNotification(title, message);
    }
    
    // Get user notifications
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    // Get unread notifications
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId);
    }
    
    // Mark notification as read
    public void markAsRead(Long notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isPresent()) {
            Notification n = notification.get();
            n.setIsRead(true);
            n.setReadAt(LocalDateTime.now());
            notificationRepository.save(n);
        }
    }
    
    // Get notification analytics
    public Map<String, Object> getNotificationAnalytics() {
        List<Notification> allNotifications = notificationRepository.findAll();
        
        Map<String, Object> analytics = new HashMap<>();
        
        // Delivery success rate
        long delivered = allNotifications.stream().mapToLong(n -> n.getIsDelivered() ? 1 : 0).sum();
        analytics.put("deliveryRate", (double)delivered / allNotifications.size() * 100);
        
        // Read rate
        long read = allNotifications.stream().mapToLong(n -> n.getIsRead() ? 1 : 0).sum();
        analytics.put("readRate", (double)read / allNotifications.size() * 100);
        
        // Notifications by type
        Map<String, Long> byType = new HashMap<>();
        allNotifications.forEach(n -> byType.merge(n.getType(), 1L, Long::sum));
        analytics.put("notificationsByType", byType);
        
        // Notifications by priority
        Map<String, Long> byPriority = new HashMap<>();
        allNotifications.forEach(n -> byPriority.merge(n.getPriority(), 1L, Long::sum));
        analytics.put("notificationsByPriority", byPriority);
        
        return analytics;
    }
    
    // Automated notification triggers
    public void processAutomatedTriggers() {
        // Check for milestone achievements
        checkMilestones();
        
        // Check for engagement thresholds
        checkEngagementThresholds();
        
        // Check for inactive users
        checkInactiveUsers();
    }
    
    // Notification preferences management
    public void updateNotificationPreferences(Long userId, Map<String, Boolean> preferences) {
        // This would typically update user preferences in the User model
        // For now, we'll store it as metadata in notifications
        String preferencesJson = convertPreferencesToJson(preferences);
        
        // Update user's notification preferences
        // Implementation would depend on User model structure
    }
    
    // Private helper methods
    private void deliverNotification(Notification notification) {
        switch (notification.getType()) {
            case "EMAIL":
                deliverEmailNotification(notification);
                break;
            case "PUSH":
                deliverPushNotification(notification);
                break;
            case "IN_APP":
                deliverInAppNotification(notification);
                break;
            case "RSS":
                deliverRSSNotification(notification);
                break;
        }
        
        notification.setIsDelivered(true);
        notification.setDeliveredAt(LocalDateTime.now());
    }
    
    private void deliverEmailNotification(Notification notification) {
        // Email delivery logic
        System.out.println("Sending email notification: " + notification.getTitle());
    }
    
    private void deliverPushNotification(Notification notification) {
        // Push notification delivery logic
        System.out.println("Sending push notification: " + notification.getTitle());
    }
    
    private void deliverInAppNotification(Notification notification) {
        // In-app notification delivery logic
        System.out.println("Delivering in-app notification: " + notification.getTitle());
    }
    
    private void deliverRSSNotification(Notification notification) {
        // RSS feed update logic
        System.out.println("Updating RSS feed: " + notification.getTitle());
    }
    
    private void deliverEmergencyNotification(String title, String message) {
        // Emergency delivery through all channels
        System.out.println("EMERGENCY: " + title + " - " + message);
    }
    
    private void checkMilestones() {
        // Check for content milestones (views, likes, etc.)
        // Send congratulatory notifications
    }
    
    private void checkEngagementThresholds() {
        // Check for engagement milestones
        // Send engagement notifications
    }
    
    private void checkInactiveUsers() {
        // Check for inactive users
        // Send re-engagement notifications
    }
    
    private String convertPreferencesToJson(Map<String, Boolean> preferences) {
        // Convert preferences map to JSON string
        StringBuilder json = new StringBuilder("{");
        preferences.forEach((key, value) -> 
            json.append("\"").append(key).append("\":").append(value).append(","));
        if (json.length() > 1) {
            json.setLength(json.length() - 1); // Remove last comma
        }
        json.append("}");
        return json.toString();
    }
}