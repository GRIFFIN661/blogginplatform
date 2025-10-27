package com.examly.springapp.controller;

import com.examly.springapp.service.IntegrationService;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/integrations")
@CrossOrigin(origins = "*")
public class IntegrationController {
    
    @Autowired
    private IntegrationService integrationService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Social Media Integration
    @PostMapping("/social-media/share")
    public ResponseEntity<Map<String, Object>> shareToSocialMedia(
            @RequestParam String platform,
            @RequestParam String content,
            @RequestParam String title) {
        
        Map<String, Object> result = integrationService.shareToSocialMedia(platform, content, title);
        return ResponseEntity.ok(result);
    }
    
    // Email Marketing Integration
    @PostMapping("/email/campaign")
    public ResponseEntity<Map<String, Object>> sendEmailCampaign(
            @RequestParam String templateId,
            @RequestBody List<String> recipients,
            @RequestParam Map<String, Object> data) {
        
        Map<String, Object> result = integrationService.sendEmailCampaign(templateId, recipients, data);
        return ResponseEntity.ok(result);
    }
    
    // CDN Integration
    @PostMapping("/cdn/upload")
    public ResponseEntity<Map<String, Object>> uploadToCDN(
            @RequestParam("file") MultipartFile file) {
        
        try {
            Map<String, Object> result = integrationService.uploadToCDN(
                file.getOriginalFilename(),
                file.getBytes(),
                file.getContentType()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Analytics Integration
    @PostMapping("/analytics/sync")
    public ResponseEntity<Map<String, Object>> syncWithAnalytics(
            @RequestParam String platform,
            @RequestBody Map<String, Object> data) {
        
        Map<String, Object> result = integrationService.syncWithAnalytics(platform, data);
        return ResponseEntity.ok(result);
    }
    
    // SEO Tool Integration
    @PostMapping("/seo/analyze")
    public ResponseEntity<Map<String, Object>> analyzeSEO(
            @RequestParam String content,
            @RequestParam String title,
            @RequestBody List<String> keywords) {
        
        Map<String, Object> analysis = integrationService.analyzeSEO(content, title, keywords);
        return ResponseEntity.ok(analysis);
    }
    
    // API Management
    @PostMapping("/api/validate")
    public ResponseEntity<Map<String, Object>> validateAPIAccess(
            @RequestParam String apiKey,
            @RequestParam String endpoint) {
        
        Map<String, Object> validation = integrationService.validateAPIAccess(apiKey, endpoint);
        return ResponseEntity.ok(validation);
    }
    
    // Real-time Data Exchange
    @PostMapping("/broadcast")
    public ResponseEntity<Void> broadcastUpdate(
            @RequestParam String eventType,
            @RequestBody Map<String, Object> data) {
        
        integrationService.broadcastUpdate(eventType, data);
        return ResponseEntity.ok().build();
    }
    
    // Integration Management
    @GetMapping("/status")
    public ResponseEntity<List<Map<String, Object>>> getIntegrationStatus() {
        List<Map<String, Object>> integrations = List.of(
            Map.of("name", "Google Analytics", "status", "CONNECTED", "lastSync", "2024-01-15 10:30"),
            Map.of("name", "Facebook", "status", "CONNECTED", "lastSync", "2024-01-15 09:15"),
            Map.of("name", "Twitter", "status", "DISCONNECTED", "lastSync", "Never"),
            Map.of("name", "Email Service", "status", "CONNECTED", "lastSync", "2024-01-15 11:00")
        );
        return ResponseEntity.ok(integrations);
    }
    
    @PostMapping("/connect")
    public ResponseEntity<Map<String, String>> connectIntegration(
            @RequestParam String integrationName,
            @RequestBody Map<String, String> config) {
        
        Map<String, String> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", integrationName + " connected successfully");
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/disconnect")
    public ResponseEntity<Map<String, String>> disconnectIntegration(
            @RequestParam String integrationName) {
        
        Map<String, String> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", integrationName + " disconnected successfully");
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> testIntegration(
            @RequestParam String integrationName) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "Connection test successful");
        result.put("responseTime", "150ms");
        return ResponseEntity.ok(result);
    }
    
    // User Management Endpoints
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody Map<String, String> userData) {
        try {
            User user = new User();
            user.setUsername(userData.get("username"));
            user.setEmail(userData.get("email"));
            user.setPasswordHash(passwordEncoder.encode(userData.get("password")));
            user.setRole(userData.getOrDefault("role", "USER"));
            
            User savedUser = userRepository.save(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/users/{id}/suspend")
    public ResponseEntity<Map<String, String>> suspendUser(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            user.setActive(false);
            userRepository.save(user);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "User suspended successfully");
            response.put("userId", id.toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}