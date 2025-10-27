package com.examly.springapp.controller;

import com.examly.springapp.model.Notification;
import com.examly.springapp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(unreadNotifications);
    }
    
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/platform-announcement")
    public ResponseEntity<Void> sendPlatformAnnouncement(@RequestBody Map<String, String> announcement) {
        notificationService.sendPlatformAnnouncement(
            announcement.get("title"),
            announcement.get("message"),
            announcement.get("priority")
        );
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/emergency")
    public ResponseEntity<Void> sendEmergencyNotification(@RequestBody Map<String, String> emergency) {
        notificationService.sendEmergencyNotification(
            emergency.get("title"),
            emergency.get("message")
        );
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getNotificationAnalytics() {
        Map<String, Object> analytics = notificationService.getNotificationAnalytics();
        return ResponseEntity.ok(analytics);
    }
    
    @PostMapping("/process-triggers")
    public ResponseEntity<Void> processAutomatedTriggers() {
        notificationService.processAutomatedTriggers();
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/preferences/{userId}")
    public ResponseEntity<Void> updateNotificationPreferences(
            @PathVariable Long userId,
            @RequestBody Map<String, Boolean> preferences) {
        notificationService.updateNotificationPreferences(userId, preferences);
        return ResponseEntity.ok().build();
    }
}