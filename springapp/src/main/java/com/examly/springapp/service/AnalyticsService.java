package com.examly.springapp.service;

import com.examly.springapp.model.ContentMetrics;
import com.examly.springapp.model.Blog;
import com.examly.springapp.repository.ContentMetricsRepository;
import com.examly.springapp.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    
    @Autowired
    private ContentMetricsRepository metricsRepository;
    
    @Autowired
    private BlogRepository blogRepository;
    
    // Content Performance Analytics
    public Map<String, Object> getContentPerformanceMetrics(Long blogId) {
        List<ContentMetrics> metrics = metricsRepository.findByBlogId(blogId);
        
        Map<String, Object> performance = new HashMap<>();
        performance.put("totalViews", metrics.stream().mapToInt(ContentMetrics::getViews).sum());
        performance.put("totalLikes", metrics.stream().mapToInt(ContentMetrics::getLikes).sum());
        performance.put("totalShares", metrics.stream().mapToInt(ContentMetrics::getShares).sum());
        performance.put("avgEngagementRate", metrics.stream().mapToDouble(ContentMetrics::getEngagementRate).average().orElse(0.0));
        performance.put("avgCompletionRate", metrics.stream().mapToDouble(ContentMetrics::getCompletionRate).average().orElse(0.0));
        
        return performance;
    }
    
    // Geographic Analysis
    public Map<String, Object> getGeographicAnalytics() {
        List<ContentMetrics> allMetrics = metricsRepository.findAll();
        
        Map<String, Integer> locationViews = allMetrics.stream()
            .collect(Collectors.groupingBy(
                ContentMetrics::getGeoLocation,
                Collectors.summingInt(ContentMetrics::getViews)
            ));
        
        Map<String, Double> locationEngagement = allMetrics.stream()
            .collect(Collectors.groupingBy(
                ContentMetrics::getGeoLocation,
                Collectors.averagingDouble(ContentMetrics::getEngagementRate)
            ));
        
        Map<String, Object> geoAnalytics = new HashMap<>();
        geoAnalytics.put("viewsByLocation", locationViews);
        geoAnalytics.put("engagementByLocation", locationEngagement);
        
        return geoAnalytics;
    }
    
    // Trend Analysis
    public Map<String, Object> getTrendAnalysis(LocalDateTime startDate, LocalDateTime endDate) {
        List<ContentMetrics> metrics = metricsRepository.findByDateRange(startDate, endDate);
        
        Map<String, Object> trends = new HashMap<>();
        
        // Daily engagement trends
        Map<String, Double> dailyEngagement = metrics.stream()
            .collect(Collectors.groupingBy(
                m -> m.getTimestamp().toLocalDate().toString(),
                Collectors.averagingDouble(ContentMetrics::getEngagementRate)
            ));
        
        // Content performance trends
        Map<String, Integer> dailyViews = metrics.stream()
            .collect(Collectors.groupingBy(
                m -> m.getTimestamp().toLocalDate().toString(),
                Collectors.summingInt(ContentMetrics::getViews)
            ));
        
        trends.put("dailyEngagement", dailyEngagement);
        trends.put("dailyViews", dailyViews);
        trends.put("growthRate", calculateGrowthRate(dailyViews));
        
        return trends;
    }
    
    // SEO Analytics
    public Map<String, Object> getSEOAnalytics() {
        List<ContentMetrics> metrics = metricsRepository.findAll();
        
        Map<String, Object> seoData = new HashMap<>();
        seoData.put("avgSeoScore", metrics.stream().mapToDouble(ContentMetrics::getSeoScore).average().orElse(0.0));
        seoData.put("totalOrganicViews", metrics.stream().mapToInt(ContentMetrics::getOrganicViews).sum());
        seoData.put("organicTrafficPercentage", calculateOrganicTrafficPercentage(metrics));
        
        return seoData;
    }
    
    // Predictive Analytics
    public Map<String, Object> getPredictiveAnalytics(Long blogId) {
        List<ContentMetrics> historicalData = metricsRepository.findByBlogId(blogId);
        
        Map<String, Object> predictions = new HashMap<>();
        
        // Simple trend-based prediction
        if (historicalData.size() >= 3) {
            double avgGrowth = calculateAverageGrowthRate(historicalData);
            int currentViews = historicalData.get(historicalData.size() - 1).getViews();
            
            predictions.put("predictedViews7Days", (int)(currentViews * (1 + avgGrowth * 7)));
            predictions.put("predictedViews30Days", (int)(currentViews * (1 + avgGrowth * 30)));
            predictions.put("viralPotential", calculateViralPotential(historicalData));
        }
        
        return predictions;
    }
    
    // Executive Dashboard Data
    public Map<String, Object> getExecutiveDashboard() {
        List<ContentMetrics> allMetrics = metricsRepository.findAll();
        List<Blog> allBlogs = blogRepository.findAll();
        
        Map<String, Object> dashboard = new HashMap<>();
        
        // Platform Overview
        dashboard.put("totalBlogs", allBlogs.size());
        dashboard.put("totalViews", allMetrics.stream().mapToInt(ContentMetrics::getViews).sum());
        dashboard.put("avgEngagementRate", allMetrics.stream().mapToDouble(ContentMetrics::getEngagementRate).average().orElse(0.0));
        
        // Growth Metrics
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
        List<ContentMetrics> recentMetrics = metricsRepository.findByDateRange(lastWeek, LocalDateTime.now());
        dashboard.put("weeklyGrowth", calculateWeeklyGrowth(recentMetrics, allMetrics));
        
        // Top Performing Content
        dashboard.put("topContent", getTopPerformingContent(5));
        
        return dashboard;
    }
    
    // Performance Benchmarking
    public Map<String, Object> getPerformanceBenchmarks(Long blogId) {
        ContentMetrics blogMetrics = metricsRepository.findByBlogId(blogId).stream()
            .findFirst().orElse(new ContentMetrics());
        
        List<ContentMetrics> allMetrics = metricsRepository.findAll();
        
        Map<String, Object> benchmarks = new HashMap<>();
        
        double industryAvgEngagement = allMetrics.stream()
            .mapToDouble(ContentMetrics::getEngagementRate).average().orElse(0.0);
        
        benchmarks.put("engagementVsIndustry", blogMetrics.getEngagementRate() / industryAvgEngagement);
        benchmarks.put("viewsPercentile", calculatePercentile(blogMetrics.getViews(), 
            allMetrics.stream().mapToInt(ContentMetrics::getViews).toArray()));
        
        return benchmarks;
    }
    
    // Helper Methods
    private double calculateGrowthRate(Map<String, Integer> dailyViews) {
        List<Integer> values = new ArrayList<>(dailyViews.values());
        if (values.size() < 2) return 0.0;
        
        int first = values.get(0);
        int last = values.get(values.size() - 1);
        return first == 0 ? 0.0 : ((double)(last - first) / first) * 100;
    }
    
    private double calculateOrganicTrafficPercentage(List<ContentMetrics> metrics) {
        int totalViews = metrics.stream().mapToInt(ContentMetrics::getViews).sum();
        int organicViews = metrics.stream().mapToInt(ContentMetrics::getOrganicViews).sum();
        return totalViews == 0 ? 0.0 : ((double)organicViews / totalViews) * 100;
    }
    
    private double calculateAverageGrowthRate(List<ContentMetrics> data) {
        if (data.size() < 2) return 0.0;
        
        double totalGrowth = 0.0;
        for (int i = 1; i < data.size(); i++) {
            int prev = data.get(i-1).getViews();
            int curr = data.get(i).getViews();
            if (prev > 0) {
                totalGrowth += ((double)(curr - prev) / prev);
            }
        }
        return totalGrowth / (data.size() - 1);
    }
    
    private double calculateViralPotential(List<ContentMetrics> data) {
        double avgShares = data.stream().mapToDouble(ContentMetrics::getShares).average().orElse(0.0);
        double avgEngagement = data.stream().mapToDouble(ContentMetrics::getEngagementRate).average().orElse(0.0);
        return (avgShares * avgEngagement) / 100.0; // Normalized viral score
    }
    
    private double calculateWeeklyGrowth(List<ContentMetrics> recent, List<ContentMetrics> all) {
        int recentViews = recent.stream().mapToInt(ContentMetrics::getViews).sum();
        int totalViews = all.stream().mapToInt(ContentMetrics::getViews).sum();
        return totalViews == 0 ? 0.0 : ((double)recentViews / totalViews) * 100;
    }
    
    private List<Map<String, Object>> getTopPerformingContent(int limit) {
        return metricsRepository.findTopPerformingContent().stream()
            .limit(limit)
            .map(m -> {
                Map<String, Object> content = new HashMap<>();
                content.put("blogId", m.getBlog().getId());
                content.put("title", m.getBlog().getTitle());
                content.put("engagementRate", m.getEngagementRate());
                content.put("views", m.getViews());
                return content;
            })
            .collect(Collectors.toList());
    }
    
    private double calculatePercentile(int value, int[] allValues) {
        Arrays.sort(allValues);
        int count = 0;
        for (int v : allValues) {
            if (v <= value) count++;
        }
        return ((double)count / allValues.length) * 100;
    }
}