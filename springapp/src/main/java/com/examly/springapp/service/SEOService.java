package com.examly.springapp.service;

import com.examly.springapp.model.Blog;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SEOService {

    public Map<String, Object> analyzeSEO(Blog blog) {
        Map<String, Object> analysis = new HashMap<>();
        
        // Title analysis
        analysis.put("titleLength", blog.getTitle() != null ? blog.getTitle().length() : 0);
        analysis.put("titleOptimal", isTitleOptimal(blog.getTitle()));
        
        // Content analysis
        analysis.put("contentLength", blog.getContent() != null ? blog.getContent().length() : 0);
        analysis.put("wordCount", getWordCount(blog.getContent()));
        analysis.put("readingTime", calculateReadingTime(blog.getContent()));
        
        // SEO meta analysis
        analysis.put("hasMetaDescription", blog.getSeoDescription() != null && !blog.getSeoDescription().isEmpty());
        analysis.put("metaDescriptionLength", blog.getSeoDescription() != null ? blog.getSeoDescription().length() : 0);
        analysis.put("metaDescriptionOptimal", isMetaDescriptionOptimal(blog.getSeoDescription()));
        
        // Keyword analysis
        analysis.put("keywordDensity", calculateKeywordDensity(blog.getContent(), blog.getTitle()));
        
        // Overall SEO score
        analysis.put("seoScore", calculateSEOScore(analysis));
        
        // Recommendations
        analysis.put("recommendations", generateRecommendations(analysis, blog));
        
        return analysis;
    }

    private boolean isTitleOptimal(String title) {
        if (title == null) return false;
        int length = title.length();
        return length >= 30 && length <= 60;
    }

    private int getWordCount(String content) {
        if (content == null || content.isEmpty()) return 0;
        return content.trim().split("\\s+").length;
    }

    private int calculateReadingTime(String content) {
        int wordCount = getWordCount(content);
        return Math.max(1, wordCount / 200); // Average reading speed: 200 words per minute
    }

    private boolean isMetaDescriptionOptimal(String metaDescription) {
        if (metaDescription == null) return false;
        int length = metaDescription.length();
        return length >= 120 && length <= 160;
    }

    private Map<String, Double> calculateKeywordDensity(String content, String title) {
        Map<String, Double> density = new HashMap<>();
        
        if (content == null || title == null) return density;
        
        String[] titleWords = title.toLowerCase().split("\\s+");
        String contentLower = content.toLowerCase();
        int totalWords = getWordCount(content);
        
        for (String keyword : titleWords) {
            if (keyword.length() > 3) { // Only consider words longer than 3 characters
                Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b");
                Matcher matcher = pattern.matcher(contentLower);
                int count = 0;
                while (matcher.find()) {
                    count++;
                }
                double densityPercent = totalWords > 0 ? (count * 100.0) / totalWords : 0;
                density.put(keyword, densityPercent);
            }
        }
        
        return density;
    }

    private int calculateSEOScore(Map<String, Object> analysis) {
        int score = 0;
        
        // Title score (20 points)
        if ((Boolean) analysis.get("titleOptimal")) {
            score += 20;
        } else if ((Integer) analysis.get("titleLength") > 0) {
            score += 10;
        }
        
        // Content score (30 points)
        int wordCount = (Integer) analysis.get("wordCount");
        if (wordCount >= 300) {
            score += 30;
        } else if (wordCount >= 150) {
            score += 20;
        } else if (wordCount > 0) {
            score += 10;
        }
        
        // Meta description score (20 points)
        if ((Boolean) analysis.get("metaDescriptionOptimal")) {
            score += 20;
        } else if ((Boolean) analysis.get("hasMetaDescription")) {
            score += 10;
        }
        
        // Keyword density score (15 points)
        @SuppressWarnings("unchecked")
        Map<String, Double> keywordDensity = (Map<String, Double>) analysis.get("keywordDensity");
        if (!keywordDensity.isEmpty()) {
            double avgDensity = keywordDensity.values().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            if (avgDensity >= 1 && avgDensity <= 3) {
                score += 15;
            } else if (avgDensity > 0) {
                score += 8;
            }
        }
        
        // Reading time score (15 points)
        int readingTime = (Integer) analysis.get("readingTime");
        if (readingTime >= 2 && readingTime <= 10) {
            score += 15;
        } else if (readingTime > 0) {
            score += 8;
        }
        
        return Math.min(100, score);
    }

    private Map<String, String> generateRecommendations(Map<String, Object> analysis, Blog blog) {
        Map<String, String> recommendations = new HashMap<>();
        
        // Title recommendations
        if (!(Boolean) analysis.get("titleOptimal")) {
            int titleLength = (Integer) analysis.get("titleLength");
            if (titleLength < 30) {
                recommendations.put("title", "Consider making your title longer (30-60 characters) for better SEO");
            } else if (titleLength > 60) {
                recommendations.put("title", "Consider shortening your title (30-60 characters) for better SEO");
            }
        }
        
        // Content recommendations
        int wordCount = (Integer) analysis.get("wordCount");
        if (wordCount < 300) {
            recommendations.put("content", "Consider adding more content. Articles with 300+ words tend to rank better");
        }
        
        // Meta description recommendations
        if (!(Boolean) analysis.get("hasMetaDescription")) {
            recommendations.put("metaDescription", "Add a meta description to improve search engine visibility");
        } else if (!(Boolean) analysis.get("metaDescriptionOptimal")) {
            int metaLength = (Integer) analysis.get("metaDescriptionLength");
            if (metaLength < 120) {
                recommendations.put("metaDescription", "Consider making your meta description longer (120-160 characters)");
            } else if (metaLength > 160) {
                recommendations.put("metaDescription", "Consider shortening your meta description (120-160 characters)");
            }
        }
        
        return recommendations;
    }

    public String generateSlug(String title) {
        if (title == null || title.isEmpty()) {
            return "untitled";
        }
        
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}