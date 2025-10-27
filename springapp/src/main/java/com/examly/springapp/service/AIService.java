package com.examly.springapp.service;

import com.examly.springapp.model.Blog;
import com.examly.springapp.model.ContentMetrics;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AIService {
    
    // Automated content analysis
    public Map<String, Object> analyzeContentQuality(Blog blog) {
        Map<String, Object> analysis = new HashMap<>();
        
        String content = blog.getContent();
        String title = blog.getTitle();
        
        // Content quality metrics
        analysis.put("readabilityScore", calculateReadabilityScore(content));
        analysis.put("seoScore", calculateSEOScore(title, content));
        analysis.put("engagementPrediction", predictEngagement(content));
        analysis.put("contentLength", content.length());
        analysis.put("wordCount", countWords(content));
        analysis.put("sentenceCount", countSentences(content));
        
        // Content structure analysis
        analysis.put("hasHeadings", hasHeadings(content));
        analysis.put("hasBulletPoints", hasBulletPoints(content));
        analysis.put("hasImages", hasImages(content));
        analysis.put("hasLinks", hasLinks(content));
        
        // Recommendations
        analysis.put("recommendations", generateContentRecommendations(analysis));
        
        return analysis;
    }
    
    // Intelligent content recommendations
    public List<Map<String, Object>> generateContentRecommendations(String userInterests, 
                                                                  List<ContentMetrics> userHistory) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        // Analyze user preferences
        Map<String, Double> topicPreferences = analyzeTopicPreferences(userHistory);
        
        // Generate topic suggestions
        List<String> suggestedTopics = generateTopicSuggestions(userInterests, topicPreferences);
        
        for (String topic : suggestedTopics) {
            Map<String, Object> recommendation = new HashMap<>();
            recommendation.put("topic", topic);
            recommendation.put("relevanceScore", calculateTopicRelevance(topic, userInterests));
            recommendation.put("trendingScore", calculateTrendingScore(topic));
            recommendation.put("suggestedKeywords", generateKeywords(topic));
            recommendations.add(recommendation);
        }
        
        return recommendations.stream()
            .sorted((a, b) -> Double.compare((Double) b.get("relevanceScore"), (Double) a.get("relevanceScore")))
            .limit(10)
            .collect(Collectors.toList());
    }
    
    // Machine learning for engagement prediction
    public double predictContentSuccess(Blog blog, List<ContentMetrics> historicalData) {
        Map<String, Object> contentFeatures = extractContentFeatures(blog);
        
        // Simple ML model simulation
        double baseScore = 0.5;
        
        // Title length factor
        int titleLength = blog.getTitle().length();
        if (titleLength >= 30 && titleLength <= 60) {
            baseScore += 0.1;
        }
        
        // Content length factor
        int contentLength = blog.getContent().length();
        if (contentLength >= 1000 && contentLength <= 3000) {
            baseScore += 0.15;
        }
        
        // Readability factor
        double readability = calculateReadabilityScore(blog.getContent());
        baseScore += (readability / 100) * 0.2;
        
        // Historical performance factor
        if (!historicalData.isEmpty()) {
            double avgEngagement = historicalData.stream()
                .mapToDouble(ContentMetrics::getEngagementRate)
                .average()
                .orElse(0.0);
            baseScore += (avgEngagement / 100) * 0.15;
        }
        
        return Math.min(1.0, Math.max(0.0, baseScore));
    }
    
    // Natural language processing for content categorization
    public Map<String, Object> categorizeContent(String content) {
        Map<String, Object> categorization = new HashMap<>();
        
        // Extract keywords
        List<String> keywords = extractKeywords(content);
        categorization.put("keywords", keywords);
        
        // Determine categories
        List<String> categories = determineCategories(keywords, content);
        categorization.put("categories", categories);
        
        // Sentiment analysis
        String sentiment = analyzeSentiment(content);
        categorization.put("sentiment", sentiment);
        
        // Topic modeling
        String primaryTopic = identifyPrimaryTopic(content);
        categorization.put("primaryTopic", primaryTopic);
        
        // Content type classification
        String contentType = classifyContentType(content);
        categorization.put("contentType", contentType);
        
        return categorization;
    }
    
    // Automated moderation
    public Map<String, Object> moderateContent(String content) {
        Map<String, Object> moderation = new HashMap<>();
        
        // Spam detection
        boolean isSpam = detectSpam(content);
        moderation.put("isSpam", isSpam);
        moderation.put("spamScore", calculateSpamScore(content));
        
        // Inappropriate content detection
        boolean isInappropriate = detectInappropriateContent(content);
        moderation.put("isInappropriate", isInappropriate);
        moderation.put("inappropriateScore", calculateInappropriateScore(content));
        
        // Policy violation detection
        List<String> policyViolations = detectPolicyViolations(content);
        moderation.put("policyViolations", policyViolations);
        
        // Overall safety score
        double safetyScore = calculateSafetyScore(isSpam, isInappropriate, policyViolations);
        moderation.put("safetyScore", safetyScore);
        
        // Moderation recommendation
        String recommendation = generateModerationRecommendation(safetyScore, policyViolations);
        moderation.put("recommendation", recommendation);
        
        return moderation;
    }
    
    // Smart notification system
    public Map<String, Object> optimizeNotification(String userId, String notificationType, 
                                                   Map<String, Object> userBehavior) {
        Map<String, Object> optimization = new HashMap<>();
        
        // Determine optimal timing
        String optimalTime = calculateOptimalNotificationTime(userId, userBehavior);
        optimization.put("optimalTime", optimalTime);
        
        // Determine priority
        String priority = calculateNotificationPriority(notificationType, userBehavior);
        optimization.put("priority", priority);
        
        // Determine delivery channel
        String optimalChannel = selectOptimalDeliveryChannel(userId, notificationType, userBehavior);
        optimization.put("optimalChannel", optimalChannel);
        
        // Personalize message
        String personalizedMessage = personalizeNotificationMessage(userId, notificationType, userBehavior);
        optimization.put("personalizedMessage", personalizedMessage);
        
        return optimization;
    }
    
    // Content personalization
    public List<Map<String, Object>> personalizeContent(String userId, List<Blog> availableContent, 
                                                       Map<String, Object> userProfile) {
        return availableContent.stream()
            .map(blog -> {
                Map<String, Object> personalizedItem = new HashMap<>();
                personalizedItem.put("blog", blog);
                personalizedItem.put("relevanceScore", calculatePersonalRelevance(blog, userProfile));
                personalizedItem.put("recommendationReason", generateRecommendationReason(blog, userProfile));
                return personalizedItem;
            })
            .sorted((a, b) -> Double.compare((Double) b.get("relevanceScore"), (Double) a.get("relevanceScore")))
            .limit(20)
            .collect(Collectors.toList());
    }
    
    // Continuous learning capabilities
    public void updateMLModel(List<ContentMetrics> newData) {
        // Simulate model updates with new engagement data
        System.out.println("Updating ML model with " + newData.size() + " new data points");
        
        // In a real implementation, this would:
        // 1. Preprocess new data
        // 2. Retrain or update model parameters
        // 3. Validate model performance
        // 4. Deploy updated model
    }
    
    // Private helper methods
    private double calculateReadabilityScore(String content) {
        // Simplified Flesch Reading Ease calculation
        int sentences = countSentences(content);
        int words = countWords(content);
        int syllables = countSyllables(content);
        
        if (sentences == 0 || words == 0) return 0;
        
        double avgSentenceLength = (double) words / sentences;
        double avgSyllablesPerWord = (double) syllables / words;
        
        return 206.835 - (1.015 * avgSentenceLength) - (84.6 * avgSyllablesPerWord);
    }
    
    private double calculateSEOScore(String title, String content) {
        double score = 0;
        
        // Title length (30-60 characters is optimal)
        int titleLength = title.length();
        if (titleLength >= 30 && titleLength <= 60) score += 20;
        
        // Content length (1000+ words is good)
        if (content.length() >= 1000) score += 20;
        
        // Headings presence
        if (hasHeadings(content)) score += 15;
        
        // Keyword density (simplified)
        String[] titleWords = title.toLowerCase().split("\\s+");
        for (String word : titleWords) {
            if (word.length() > 3 && content.toLowerCase().contains(word)) {
                score += 5;
            }
        }
        
        // Meta elements (simplified check)
        if (content.contains("http")) score += 10; // Has links
        
        return Math.min(100, score);
    }
    
    private double predictEngagement(String content) {
        double engagement = 0.5; // Base engagement
        
        // Content length factor
        int length = content.length();
        if (length >= 500 && length <= 2000) engagement += 0.2;
        
        // Question marks (engagement trigger)
        long questionCount = content.chars().filter(c -> c == '?').count();
        engagement += Math.min(0.1, questionCount * 0.02);
        
        // Emotional words
        String[] emotionalWords = {"amazing", "incredible", "shocking", "surprising", "love", "hate"};
        for (String word : emotionalWords) {
            if (content.toLowerCase().contains(word)) {
                engagement += 0.05;
            }
        }
        
        return Math.min(1.0, engagement);
    }
    
    private int countWords(String content) {
        return content.trim().isEmpty() ? 0 : content.trim().split("\\s+").length;
    }
    
    private int countSentences(String content) {
        return content.split("[.!?]+").length;
    }
    
    private int countSyllables(String content) {
        // Simplified syllable counting
        return content.toLowerCase().replaceAll("[^aeiou]", "").length();
    }
    
    private boolean hasHeadings(String content) {
        return content.contains("#") || content.contains("<h");
    }
    
    private boolean hasBulletPoints(String content) {
        return content.contains("*") || content.contains("-") || content.contains("<li>");
    }
    
    private boolean hasImages(String content) {
        return content.contains("![") || content.contains("<img");
    }
    
    private boolean hasLinks(String content) {
        return content.contains("http") || content.contains("[");
    }
    
    private List<String> generateContentRecommendations(Map<String, Object> analysis) {
        List<String> recommendations = new ArrayList<>();
        
        double readability = (Double) analysis.get("readabilityScore");
        if (readability < 60) {
            recommendations.add("Improve readability by using shorter sentences and simpler words");
        }
        
        double seoScore = (Double) analysis.get("seoScore");
        if (seoScore < 70) {
            recommendations.add("Optimize for SEO by adding relevant keywords and improving structure");
        }
        
        if (!(Boolean) analysis.get("hasHeadings")) {
            recommendations.add("Add headings to improve content structure and readability");
        }
        
        if ((Integer) analysis.get("wordCount") < 300) {
            recommendations.add("Consider expanding content for better engagement and SEO");
        }
        
        return recommendations;
    }
    
    private Map<String, Double> analyzeTopicPreferences(List<ContentMetrics> userHistory) {
        // Analyze user's historical engagement with different topics
        Map<String, Double> preferences = new HashMap<>();
        preferences.put("technology", 0.8);
        preferences.put("lifestyle", 0.6);
        preferences.put("business", 0.7);
        return preferences;
    }
    
    private List<String> generateTopicSuggestions(String userInterests, Map<String, Double> preferences) {
        List<String> topics = Arrays.asList(
            "AI and Machine Learning", "Web Development", "Digital Marketing",
            "Productivity Tips", "Health and Wellness", "Financial Planning",
            "Travel Guides", "Technology Reviews", "Career Development"
        );
        
        return topics.stream()
            .filter(topic -> preferences.getOrDefault(topic.toLowerCase(), 0.0) > 0.5)
            .collect(Collectors.toList());
    }
    
    private double calculateTopicRelevance(String topic, String userInterests) {
        // Simple relevance calculation based on keyword matching
        String[] interests = userInterests.toLowerCase().split(",");
        String topicLower = topic.toLowerCase();
        
        return Arrays.stream(interests)
            .mapToDouble(interest -> topicLower.contains(interest.trim()) ? 1.0 : 0.0)
            .average()
            .orElse(0.5);
    }
    
    private double calculateTrendingScore(String topic) {
        // Mock trending score - in production, this would use real trend data
        return Math.random();
    }
    
    private List<String> generateKeywords(String topic) {
        // Generate relevant keywords for the topic
        Map<String, List<String>> topicKeywords = new HashMap<>();
        topicKeywords.put("AI and Machine Learning", Arrays.asList("artificial intelligence", "neural networks", "deep learning"));
        topicKeywords.put("Web Development", Arrays.asList("javascript", "react", "nodejs", "frontend", "backend"));
        
        return topicKeywords.getOrDefault(topic, Arrays.asList("general", "content", "blog"));
    }
    
    private Map<String, Object> extractContentFeatures(Blog blog) {
        Map<String, Object> features = new HashMap<>();
        features.put("titleLength", blog.getTitle().length());
        features.put("contentLength", blog.getContent().length());
        features.put("wordCount", countWords(blog.getContent()));
        features.put("hasHeadings", hasHeadings(blog.getContent()));
        return features;
    }
    
    private List<String> extractKeywords(String content) {
        // Simple keyword extraction
        return Arrays.stream(content.toLowerCase().split("\\W+"))
            .filter(word -> word.length() > 4)
            .distinct()
            .limit(10)
            .collect(Collectors.toList());
    }
    
    private List<String> determineCategories(List<String> keywords, String content) {
        List<String> categories = new ArrayList<>();
        
        if (keywords.stream().anyMatch(k -> Arrays.asList("tech", "technology", "software").contains(k))) {
            categories.add("Technology");
        }
        if (keywords.stream().anyMatch(k -> Arrays.asList("health", "fitness", "wellness").contains(k))) {
            categories.add("Health");
        }
        if (keywords.stream().anyMatch(k -> Arrays.asList("business", "finance", "money").contains(k))) {
            categories.add("Business");
        }
        
        return categories.isEmpty() ? Arrays.asList("General") : categories;
    }
    
    private String analyzeSentiment(String content) {
        // Simple sentiment analysis
        String[] positiveWords = {"good", "great", "excellent", "amazing", "wonderful", "love"};
        String[] negativeWords = {"bad", "terrible", "awful", "hate", "horrible", "worst"};
        
        long positiveCount = Arrays.stream(positiveWords)
            .mapToLong(word -> content.toLowerCase().split(word).length - 1)
            .sum();
        
        long negativeCount = Arrays.stream(negativeWords)
            .mapToLong(word -> content.toLowerCase().split(word).length - 1)
            .sum();
        
        if (positiveCount > negativeCount) return "POSITIVE";
        if (negativeCount > positiveCount) return "NEGATIVE";
        return "NEUTRAL";
    }
    
    private String identifyPrimaryTopic(String content) {
        // Simple topic identification
        String lowerContent = content.toLowerCase();
        
        if (lowerContent.contains("technology") || lowerContent.contains("software")) return "Technology";
        if (lowerContent.contains("health") || lowerContent.contains("fitness")) return "Health";
        if (lowerContent.contains("business") || lowerContent.contains("finance")) return "Business";
        if (lowerContent.contains("travel") || lowerContent.contains("vacation")) return "Travel";
        
        return "General";
    }
    
    private String classifyContentType(String content) {
        if (content.contains("how to") || content.contains("tutorial")) return "Tutorial";
        if (content.contains("review") || content.contains("rating")) return "Review";
        if (content.contains("news") || content.contains("breaking")) return "News";
        if (content.contains("opinion") || content.contains("think")) return "Opinion";
        
        return "Article";
    }
    
    private boolean detectSpam(String content) {
        // Simple spam detection
        String lowerContent = content.toLowerCase();
        String[] spamIndicators = {"buy now", "click here", "free money", "guaranteed", "limited time"};
        
        return Arrays.stream(spamIndicators).anyMatch(lowerContent::contains);
    }
    
    private double calculateSpamScore(String content) {
        // Calculate spam probability (0-1)
        double score = 0.0;
        
        // Check for excessive capitalization
        long upperCount = content.chars().filter(Character::isUpperCase).count();
        if (upperCount > content.length() * 0.3) score += 0.3;
        
        // Check for excessive punctuation
        long punctCount = content.chars().filter(c -> "!@#$%".indexOf(c) >= 0).count();
        if (punctCount > content.length() * 0.1) score += 0.2;
        
        // Check for spam keywords
        if (detectSpam(content)) score += 0.5;
        
        return Math.min(1.0, score);
    }
    
    private boolean detectInappropriateContent(String content) {
        String[] inappropriateWords = {"hate", "violence", "discrimination", "harassment"};
        String lowerContent = content.toLowerCase();
        
        return Arrays.stream(inappropriateWords).anyMatch(lowerContent::contains);
    }
    
    private double calculateInappropriateScore(String content) {
        return detectInappropriateContent(content) ? 0.8 : 0.1;
    }
    
    private List<String> detectPolicyViolations(String content) {
        List<String> violations = new ArrayList<>();
        
        if (detectSpam(content)) violations.add("SPAM_CONTENT");
        if (detectInappropriateContent(content)) violations.add("INAPPROPRIATE_CONTENT");
        if (content.length() < 50) violations.add("INSUFFICIENT_CONTENT");
        
        return violations;
    }
    
    private double calculateSafetyScore(boolean isSpam, boolean isInappropriate, List<String> violations) {
        double score = 1.0;
        
        if (isSpam) score -= 0.4;
        if (isInappropriate) score -= 0.5;
        score -= violations.size() * 0.1;
        
        return Math.max(0.0, score);
    }
    
    private String generateModerationRecommendation(double safetyScore, List<String> violations) {
        if (safetyScore < 0.3) return "REJECT";
        if (safetyScore < 0.6) return "REVIEW_REQUIRED";
        if (!violations.isEmpty()) return "FLAG_FOR_REVIEW";
        return "APPROVE";
    }
    
    private String calculateOptimalNotificationTime(String userId, Map<String, Object> userBehavior) {
        // Analyze user's activity patterns
        return "09:00"; // Mock optimal time
    }
    
    private String calculateNotificationPriority(String notificationType, Map<String, Object> userBehavior) {
        switch (notificationType) {
            case "EMERGENCY": return "URGENT";
            case "CONTENT_PUBLISHED": return "MEDIUM";
            case "COMMENT_RECEIVED": return "LOW";
            default: return "MEDIUM";
        }
    }
    
    private String selectOptimalDeliveryChannel(String userId, String notificationType, Map<String, Object> userBehavior) {
        // Select based on user preferences and notification type
        if ("EMERGENCY".equals(notificationType)) return "EMAIL";
        return "IN_APP";
    }
    
    private String personalizeNotificationMessage(String userId, String notificationType, Map<String, Object> userBehavior) {
        // Generate personalized message based on user data
        return "Personalized notification message for user " + userId;
    }
    
    private double calculatePersonalRelevance(Blog blog, Map<String, Object> userProfile) {
        // Calculate how relevant the blog is to the user
        return Math.random(); // Mock relevance score
    }
    
    private String generateRecommendationReason(Blog blog, Map<String, Object> userProfile) {
        return "Recommended based on your interest in " + blog.getTags();
    }
}