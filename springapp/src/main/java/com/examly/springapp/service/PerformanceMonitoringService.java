package com.examly.springapp.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PerformanceMonitoringService {
    
    private final Map<String, List<Long>> responseTimeMetrics = new ConcurrentHashMap<>();
    private final Map<String, Integer> concurrentUserCount = new ConcurrentHashMap<>();
    private final List<String> performanceLogs = new ArrayList<>();
    
    // Performance Requirements Monitoring
    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Authentication Performance (< 1 second for 95% of requests)
        metrics.put("authenticationPerformance", calculatePerformanceStats("authentication"));
        
        // Content Management (< 3 seconds for 90% of requests)
        metrics.put("contentManagementPerformance", calculatePerformanceStats("content"));
        
        // Search Operations (< 2 seconds for complex queries)
        metrics.put("searchPerformance", calculatePerformanceStats("search"));
        
        // Comment System (< 1.5 seconds for 95% of requests)
        metrics.put("commentPerformance", calculatePerformanceStats("comments"));
        
        // Concurrent Users Support
        metrics.put("concurrentUsers", getCurrentConcurrentUsers());
        
        // Database Performance
        metrics.put("databasePerformance", getDatabasePerformanceMetrics());
        
        // Mobile Performance
        metrics.put("mobilePerformance", getMobilePerformanceMetrics());
        
        // Analytics Performance (< 2 second latency)
        metrics.put("analyticsPerformance", calculatePerformanceStats("analytics"));
        
        return metrics;
    }
    
    // Record response time for different operations
    public void recordResponseTime(String operation, long responseTimeMs) {
        responseTimeMetrics.computeIfAbsent(operation, k -> new ArrayList<>()).add(responseTimeMs);
        
        // Keep only last 1000 measurements per operation
        List<Long> times = responseTimeMetrics.get(operation);
        if (times.size() > 1000) {
            times.remove(0);
        }
        
        // Log performance issues
        checkPerformanceThresholds(operation, responseTimeMs);
    }
    
    // Monitor concurrent users
    public void updateConcurrentUsers(String sessionId, boolean isActive) {
        if (isActive) {
            concurrentUserCount.put(sessionId, 1);
        } else {
            concurrentUserCount.remove(sessionId);
        }
        
        // Check if we're approaching the 1000+ concurrent user limit
        int currentUsers = concurrentUserCount.size();
        if (currentUsers > 800) {
            logPerformanceAlert("HIGH_CONCURRENT_USERS", 
                "Approaching concurrent user limit: " + currentUsers);
        }
    }
    
    // System Health Check
    public Map<String, Object> performSystemHealthCheck() {
        Map<String, Object> healthCheck = new HashMap<>();
        
        // Memory Usage
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        healthCheck.put("memoryUsage", Map.of(
            "total", totalMemory / (1024 * 1024) + " MB",
            "used", usedMemory / (1024 * 1024) + " MB",
            "free", freeMemory / (1024 * 1024) + " MB",
            "usagePercentage", (double) usedMemory / totalMemory * 100
        ));
        
        // CPU Usage (simplified)
        healthCheck.put("cpuUsage", getCPUUsage());
        
        // Database Connection Health
        healthCheck.put("databaseHealth", checkDatabaseHealth());
        
        // Response Time Health
        healthCheck.put("responseTimeHealth", checkResponseTimeHealth());
        
        // Concurrent User Health
        healthCheck.put("concurrentUserHealth", checkConcurrentUserHealth());
        
        // Overall System Status
        healthCheck.put("overallStatus", calculateOverallSystemStatus(healthCheck));
        
        return healthCheck;
    }
    
    // Performance Optimization Recommendations
    public List<String> getPerformanceRecommendations() {
        List<String> recommendations = new ArrayList<>();
        
        // Check authentication performance
        Map<String, Object> authStats = calculatePerformanceStats("authentication");
        if ((Double) authStats.get("percentile95") > 1000) {
            recommendations.add("Authentication response time exceeds 1 second. Consider optimizing JWT processing or database queries.");
        }
        
        // Check content management performance
        Map<String, Object> contentStats = calculatePerformanceStats("content");
        if ((Double) contentStats.get("percentile90") > 3000) {
            recommendations.add("Content management operations are slow. Consider implementing caching or optimizing database queries.");
        }
        
        // Check search performance
        Map<String, Object> searchStats = calculatePerformanceStats("search");
        if ((Double) searchStats.get("average") > 2000) {
            recommendations.add("Search operations are slow. Consider implementing search indexing or using Elasticsearch.");
        }
        
        // Check concurrent users
        int currentUsers = concurrentUserCount.size();
        if (currentUsers > 800) {
            recommendations.add("High concurrent user load detected. Consider implementing load balancing or scaling horizontally.");
        }
        
        // Check memory usage
        Runtime runtime = Runtime.getRuntime();
        double memoryUsage = (double) (runtime.totalMemory() - runtime.freeMemory()) / runtime.totalMemory() * 100;
        if (memoryUsage > 80) {
            recommendations.add("High memory usage detected (" + String.format("%.1f", memoryUsage) + "%). Consider increasing heap size or optimizing memory usage.");
        }
        
        return recommendations;
    }
    
    // Real-time Performance Alerts
    public void checkPerformanceAlerts() {
        // Check for performance degradation
        Map<String, Object> currentMetrics = getPerformanceMetrics();
        
        // Alert on authentication performance
        Map<String, Object> authPerf = (Map<String, Object>) currentMetrics.get("authenticationPerformance");
        if ((Double) authPerf.get("percentile95") > 1000) {
            triggerPerformanceAlert("AUTHENTICATION_SLOW", "Authentication performance degraded");
        }
        
        // Alert on high concurrent users
        if (concurrentUserCount.size() > 900) {
            triggerPerformanceAlert("HIGH_LOAD", "Concurrent users approaching limit: " + concurrentUserCount.size());
        }
        
        // Alert on memory usage
        Runtime runtime = Runtime.getRuntime();
        double memoryUsage = (double) (runtime.totalMemory() - runtime.freeMemory()) / runtime.totalMemory() * 100;
        if (memoryUsage > 90) {
            triggerPerformanceAlert("HIGH_MEMORY", "Memory usage critical: " + String.format("%.1f", memoryUsage) + "%");
        }
    }
    
    // Performance Trend Analysis
    public Map<String, Object> getPerformanceTrends() {
        Map<String, Object> trends = new HashMap<>();
        
        // Calculate trends for each operation type
        for (String operation : responseTimeMetrics.keySet()) {
            List<Long> times = responseTimeMetrics.get(operation);
            if (times.size() >= 10) {
                trends.put(operation + "Trend", calculateTrend(times));
            }
        }
        
        // Concurrent user trends
        trends.put("concurrentUserTrend", analyzeConcurrentUserTrend());
        
        return trends;
    }
    
    // Private helper methods
    private Map<String, Object> calculatePerformanceStats(String operation) {
        List<Long> times = responseTimeMetrics.getOrDefault(operation, new ArrayList<>());
        
        if (times.isEmpty()) {
            return Map.of(
                "average", 0.0,
                "percentile90", 0.0,
                "percentile95", 0.0,
                "min", 0L,
                "max", 0L,
                "count", 0
            );
        }
        
        List<Long> sortedTimes = new ArrayList<>(times);
        Collections.sort(sortedTimes);
        
        double average = times.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long min = sortedTimes.get(0);
        long max = sortedTimes.get(sortedTimes.size() - 1);
        
        int p90Index = (int) (sortedTimes.size() * 0.9);
        int p95Index = (int) (sortedTimes.size() * 0.95);
        
        double percentile90 = sortedTimes.get(Math.min(p90Index, sortedTimes.size() - 1));
        double percentile95 = sortedTimes.get(Math.min(p95Index, sortedTimes.size() - 1));
        
        return Map.of(
            "average", average,
            "percentile90", percentile90,
            "percentile95", percentile95,
            "min", min,
            "max", max,
            "count", times.size()
        );
    }
    
    private int getCurrentConcurrentUsers() {
        return concurrentUserCount.size();
    }
    
    private Map<String, Object> getDatabasePerformanceMetrics() {
        // Simulate database performance metrics
        return Map.of(
            "connectionPoolSize", 20,
            "activeConnections", 8,
            "averageQueryTime", 45.5,
            "slowQueries", 2,
            "status", "HEALTHY"
        );
    }
    
    private Map<String, Object> getMobilePerformanceMetrics() {
        // Simulate mobile performance metrics
        return Map.of(
            "averageLoadTime", 2.8, // seconds
            "3GPerformance", "GOOD",
            "4GPerformance", "EXCELLENT",
            "offlineCapability", "ENABLED"
        );
    }
    
    private void checkPerformanceThresholds(String operation, long responseTimeMs) {
        Map<String, Long> thresholds = Map.of(
            "authentication", 1000L,
            "content", 3000L,
            "search", 2000L,
            "comments", 1500L,
            "analytics", 2000L
        );
        
        Long threshold = thresholds.get(operation);
        if (threshold != null && responseTimeMs > threshold) {
            logPerformanceAlert("SLOW_RESPONSE", 
                operation + " operation took " + responseTimeMs + "ms (threshold: " + threshold + "ms)");
        }
    }
    
    private void logPerformanceAlert(String alertType, String message) {
        String logEntry = String.format("[%s] %s: %s", 
            LocalDateTime.now(), alertType, message);
        performanceLogs.add(logEntry);
        
        // Keep only last 1000 log entries
        if (performanceLogs.size() > 1000) {
            performanceLogs.remove(0);
        }
        
        System.out.println("PERFORMANCE ALERT: " + logEntry);
    }
    
    private double getCPUUsage() {
        // Simplified CPU usage calculation
        return Math.random() * 100; // Mock implementation
    }
    
    private String checkDatabaseHealth() {
        // Simulate database health check
        return "HEALTHY";
    }
    
    private String checkResponseTimeHealth() {
        Map<String, Object> authStats = calculatePerformanceStats("authentication");
        double authP95 = (Double) authStats.get("percentile95");
        
        if (authP95 > 1000) return "DEGRADED";
        if (authP95 > 500) return "WARNING";
        return "HEALTHY";
    }
    
    private String checkConcurrentUserHealth() {
        int users = concurrentUserCount.size();
        if (users > 900) return "CRITICAL";
        if (users > 700) return "WARNING";
        return "HEALTHY";
    }
    
    private String calculateOverallSystemStatus(Map<String, Object> healthCheck) {
        // Simple logic to determine overall status
        String dbHealth = (String) healthCheck.get("databaseHealth");
        String responseHealth = (String) healthCheck.get("responseTimeHealth");
        String userHealth = (String) healthCheck.get("concurrentUserHealth");
        
        if ("CRITICAL".equals(userHealth) || "DEGRADED".equals(responseHealth)) {
            return "CRITICAL";
        }
        if ("WARNING".equals(responseHealth) || "WARNING".equals(userHealth)) {
            return "WARNING";
        }
        return "HEALTHY";
    }
    
    private void triggerPerformanceAlert(String alertType, String message) {
        logPerformanceAlert(alertType, message);
        
        // In a real implementation, this would:
        // 1. Send notifications to administrators
        // 2. Trigger automated scaling if configured
        // 3. Log to monitoring systems (e.g., Prometheus, Grafana)
        // 4. Send alerts to external services (e.g., PagerDuty, Slack)
    }
    
    private String calculateTrend(List<Long> times) {
        if (times.size() < 10) return "INSUFFICIENT_DATA";
        
        // Simple trend calculation using first and last 5 measurements
        List<Long> recent = times.subList(times.size() - 5, times.size());
        List<Long> older = times.subList(0, 5);
        
        double recentAvg = recent.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double olderAvg = older.stream().mapToLong(Long::longValue).average().orElse(0.0);
        
        double change = (recentAvg - olderAvg) / olderAvg * 100;
        
        if (change > 10) return "DEGRADING";
        if (change < -10) return "IMPROVING";
        return "STABLE";
    }
    
    private String analyzeConcurrentUserTrend() {
        // Simplified trend analysis for concurrent users
        int currentUsers = concurrentUserCount.size();
        
        if (currentUsers > 800) return "INCREASING_HIGH";
        if (currentUsers > 500) return "INCREASING_MODERATE";
        if (currentUsers < 100) return "LOW";
        return "STABLE";
    }
}