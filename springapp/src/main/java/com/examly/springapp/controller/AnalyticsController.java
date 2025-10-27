package com.examly.springapp.controller;

import com.examly.springapp.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @GetMapping("/content/{blogId}")
    public ResponseEntity<Map<String, Object>> getContentPerformance(@PathVariable Long blogId) {
        Map<String, Object> metrics = analyticsService.getContentPerformanceMetrics(blogId);
        return ResponseEntity.ok(metrics);
    }
    
    @GetMapping("/geographic")
    public ResponseEntity<Map<String, Object>> getGeographicAnalytics() {
        Map<String, Object> geoAnalytics = analyticsService.getGeographicAnalytics();
        return ResponseEntity.ok(geoAnalytics);
    }
    
    @GetMapping("/trends")
    public ResponseEntity<Map<String, Object>> getTrendAnalysis(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        Map<String, Object> trends = analyticsService.getTrendAnalysis(start, end);
        return ResponseEntity.ok(trends);
    }
    
    @GetMapping("/seo")
    public ResponseEntity<Map<String, Object>> getSEOAnalytics() {
        Map<String, Object> seoData = analyticsService.getSEOAnalytics();
        return ResponseEntity.ok(seoData);
    }
    
    @GetMapping("/predictive/{blogId}")
    public ResponseEntity<Map<String, Object>> getPredictiveAnalytics(@PathVariable Long blogId) {
        Map<String, Object> predictions = analyticsService.getPredictiveAnalytics(blogId);
        return ResponseEntity.ok(predictions);
    }
    
    @GetMapping("/executive-dashboard")
    public ResponseEntity<Map<String, Object>> getExecutiveDashboard() {
        Map<String, Object> dashboard = analyticsService.getExecutiveDashboard();
        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/benchmarks/{blogId}")
    public ResponseEntity<Map<String, Object>> getPerformanceBenchmarks(@PathVariable Long blogId) {
        Map<String, Object> benchmarks = analyticsService.getPerformanceBenchmarks(blogId);
        return ResponseEntity.ok(benchmarks);
    }
}