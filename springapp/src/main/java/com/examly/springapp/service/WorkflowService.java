package com.examly.springapp.service;

import com.examly.springapp.model.Workflow;
import com.examly.springapp.model.Blog;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.WorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class WorkflowService {
    
    @Autowired
    private WorkflowRepository workflowRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    // Content workflow automation
    public Workflow createContentWorkflow(Blog blog, User initiator, String workflowType) {
        Workflow workflow = new Workflow();
        workflow.setBlog(blog);
        workflow.setInitiator(initiator);
        workflow.setType(workflowType);
        workflow.setName(generateWorkflowName(workflowType, blog.getTitle()));
        workflow.setCurrentStep("INITIAL_REVIEW");
        workflow.setPriority(determinePriority(blog, workflowType));
        
        // Set due date based on workflow type
        workflow.setDueDate(calculateDueDate(workflowType));
        
        // Auto-assign based on workload and expertise
        String assignee = autoAssignWorkflow(workflowType);
        workflow.setAssignedTo(assignee);
        
        Workflow savedWorkflow = workflowRepository.save(workflow);
        
        // Send notification to assignee
        notificationService.sendCommunityNotification(
            initiator, 
            "WORKFLOW_ASSIGNED", 
            "New " + workflowType + " workflow assigned: " + blog.getTitle()
        );
        
        return savedWorkflow;
    }
    
    // Editorial process management
    public void processEditorialWorkflow(Long workflowId, String action, String comments) {
        Optional<Workflow> workflowOpt = workflowRepository.findById(workflowId);
        if (!workflowOpt.isPresent()) return;
        
        Workflow workflow = workflowOpt.get();
        
        switch (action) {
            case "APPROVE":
                approveContent(workflow);
                break;
            case "REJECT":
                rejectContent(workflow, comments);
                break;
            case "REQUEST_CHANGES":
                requestChanges(workflow, comments);
                break;
            case "ESCALATE":
                escalateWorkflow(workflow, comments);
                break;
        }
        
        workflow.setUpdatedAt(LocalDateTime.now());
        workflow.setComments(comments);
        workflowRepository.save(workflow);
    }
    
    // Moderator assignment automation
    public String autoAssignModerator(String contentType, String reportReason) {
        // Logic to assign based on:
        // 1. Moderator expertise
        // 2. Current workload
        // 3. Availability
        // 4. Content type specialization
        
        Map<String, Integer> moderatorWorkload = getCurrentModeratorWorkload();
        List<String> availableModerators = getAvailableModerators(contentType);
        
        // Find moderator with lowest workload
        String assignedModerator = availableModerators.stream()
            .min(Comparator.comparing(mod -> moderatorWorkload.getOrDefault(mod, 0)))
            .orElse("default_moderator");
        
        return assignedModerator;
    }
    
    // Policy compliance enforcement
    public boolean checkPolicyCompliance(Blog blog) {
        Map<String, Boolean> complianceChecks = new HashMap<>();
        
        // Content length check
        complianceChecks.put("contentLength", blog.getContent().length() >= 100);
        
        // Inappropriate content check (simplified)
        complianceChecks.put("appropriateContent", !containsInappropriateContent(blog.getContent()));
        
        // Title requirements
        complianceChecks.put("titleRequirements", blog.getTitle().length() >= 10 && blog.getTitle().length() <= 200);
        
        // Copyright compliance (simplified)
        complianceChecks.put("copyrightCompliance", !containsCopyrightViolation(blog.getContent()));
        
        // All checks must pass
        boolean isCompliant = complianceChecks.values().stream().allMatch(Boolean::booleanValue);
        
        if (!isCompliant) {
            createComplianceWorkflow(blog, complianceChecks);
        }
        
        return isCompliant;
    }
    
    // Workflow tracking and analytics
    public Map<String, Object> getWorkflowAnalytics() {
        List<Workflow> allWorkflows = workflowRepository.findAll();
        
        Map<String, Object> analytics = new HashMap<>();
        
        // Completion rates by type
        Map<String, Double> completionRates = calculateCompletionRates(allWorkflows);
        analytics.put("completionRates", completionRates);
        
        // Average processing time
        Map<String, Double> avgProcessingTime = calculateAverageProcessingTime(allWorkflows);
        analytics.put("averageProcessingTime", avgProcessingTime);
        
        // Bottleneck analysis
        Map<String, Integer> bottlenecks = identifyBottlenecks(allWorkflows);
        analytics.put("bottlenecks", bottlenecks);
        
        // Workflow status distribution
        Map<String, Long> statusDistribution = allWorkflows.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Workflow::getStatus,
                java.util.stream.Collectors.counting()
            ));
        analytics.put("statusDistribution", statusDistribution);
        
        return analytics;
    }
    
    // Exception handling and escalation
    public void handleWorkflowException(Long workflowId, String exceptionType, String details) {
        Optional<Workflow> workflowOpt = workflowRepository.findById(workflowId);
        if (!workflowOpt.isPresent()) return;
        
        Workflow workflow = workflowOpt.get();
        
        switch (exceptionType) {
            case "TIMEOUT":
                handleTimeoutException(workflow);
                break;
            case "POLICY_EXCEPTION":
                handlePolicyException(workflow, details);
                break;
            case "TECHNICAL_ISSUE":
                handleTechnicalException(workflow, details);
                break;
            case "ESCALATION_REQUIRED":
                escalateWorkflow(workflow, details);
                break;
        }
    }
    
    // Content lifecycle automation
    public void manageContentLifecycle(Blog blog, String lifecycleEvent) {
        switch (lifecycleEvent) {
            case "PUBLISHED":
                handleContentPublished(blog);
                break;
            case "UPDATED":
                handleContentUpdated(blog);
                break;
            case "ARCHIVED":
                handleContentArchived(blog);
                break;
            case "DELETED":
                handleContentDeleted(blog);
                break;
        }
    }
    
    // Custom workflow configuration
    public Workflow createCustomWorkflow(String workflowName, String workflowType, 
                                       Map<String, Object> configuration) {
        Workflow workflow = new Workflow();
        workflow.setName(workflowName);
        workflow.setType(workflowType);
        workflow.setWorkflowData(convertConfigurationToJson(configuration));
        workflow.setStatus("CONFIGURED");
        
        return workflowRepository.save(workflow);
    }
    
    // Private helper methods
    private String generateWorkflowName(String type, String blogTitle) {
        return type + "_" + blogTitle.replaceAll("[^a-zA-Z0-9]", "_").substring(0, 
            Math.min(blogTitle.length(), 30)) + "_" + System.currentTimeMillis();
    }
    
    private String determinePriority(Blog blog, String workflowType) {
        // Determine priority based on content type, author reputation, etc.
        if (workflowType.equals("MODERATION")) return "HIGH";
        if (blog.getContent().length() > 5000) return "MEDIUM";
        return "LOW";
    }
    
    private LocalDateTime calculateDueDate(String workflowType) {
        LocalDateTime now = LocalDateTime.now();
        switch (workflowType) {
            case "URGENT_REVIEW": return now.plusHours(2);
            case "MODERATION": return now.plusHours(24);
            case "CONTENT_REVIEW": return now.plusDays(3);
            default: return now.plusDays(7);
        }
    }
    
    private String autoAssignWorkflow(String workflowType) {
        // Simple round-robin assignment
        List<String> availableAssignees = getAvailableAssignees(workflowType);
        if (availableAssignees.isEmpty()) return "default_assignee";
        
        int index = (int) (System.currentTimeMillis() % availableAssignees.size());
        return availableAssignees.get(index);
    }
    
    private List<String> getAvailableAssignees(String workflowType) {
        // Return list of available assignees based on workflow type
        switch (workflowType) {
            case "MODERATION": return Arrays.asList("moderator1", "moderator2", "moderator3");
            case "CONTENT_REVIEW": return Arrays.asList("editor1", "editor2", "editor3");
            default: return Arrays.asList("admin1", "admin2");
        }
    }
    
    private void approveContent(Workflow workflow) {
        workflow.setStatus("COMPLETED");
        workflow.setCurrentStep("APPROVED");
        
        // Notify content author
        notificationService.sendContentNotification(
            workflow.getInitiator(),
            "CONTENT_APPROVED",
            workflow.getBlog().getTitle()
        );
    }
    
    private void rejectContent(Workflow workflow, String reason) {
        workflow.setStatus("REJECTED");
        workflow.setCurrentStep("REJECTED");
        
        // Notify content author with rejection reason
        notificationService.sendContentNotification(
            workflow.getInitiator(),
            "CONTENT_REJECTED",
            workflow.getBlog().getTitle() + " - Reason: " + reason
        );
    }
    
    private void requestChanges(Workflow workflow, String changes) {
        workflow.setStatus("CHANGES_REQUESTED");
        workflow.setCurrentStep("AWAITING_CHANGES");
        
        // Notify content author about required changes
        notificationService.sendContentNotification(
            workflow.getInitiator(),
            "CHANGES_REQUESTED",
            workflow.getBlog().getTitle() + " - Changes: " + changes
        );
    }
    
    private void escalateWorkflow(Workflow workflow, String reason) {
        workflow.setPriority("URGENT");
        workflow.setAssignedTo("senior_moderator");
        workflow.setCurrentStep("ESCALATED");
        
        // Send escalation notification
        notificationService.sendCommunityNotification(
            workflow.getInitiator(),
            "WORKFLOW_ESCALATED",
            "Workflow escalated: " + workflow.getName() + " - " + reason
        );
    }
    
    private Map<String, Integer> getCurrentModeratorWorkload() {
        // Calculate current workload for each moderator
        Map<String, Integer> workload = new HashMap<>();
        List<Workflow> activeWorkflows = workflowRepository.findByStatus("IN_PROGRESS");
        
        activeWorkflows.forEach(w -> 
            workload.merge(w.getAssignedTo(), 1, Integer::sum));
        
        return workload;
    }
    
    private List<String> getAvailableModerators(String contentType) {
        // Return moderators specialized in content type
        return Arrays.asList("moderator1", "moderator2", "moderator3");
    }
    
    private boolean containsInappropriateContent(String content) {
        // Simplified inappropriate content detection
        String[] inappropriateWords = {"spam", "inappropriate", "violation"};
        String lowerContent = content.toLowerCase();
        
        return Arrays.stream(inappropriateWords)
            .anyMatch(lowerContent::contains);
    }
    
    private boolean containsCopyrightViolation(String content) {
        // Simplified copyright violation detection
        return content.toLowerCase().contains("copyright violation");
    }
    
    private void createComplianceWorkflow(Blog blog, Map<String, Boolean> checks) {
        Workflow complianceWorkflow = new Workflow();
        complianceWorkflow.setBlog(blog);
        complianceWorkflow.setType("COMPLIANCE_CHECK");
        complianceWorkflow.setName("Compliance_" + blog.getTitle());
        complianceWorkflow.setStatus("COMPLIANCE_REVIEW");
        complianceWorkflow.setWorkflowData(convertChecksToJson(checks));
        
        workflowRepository.save(complianceWorkflow);
    }
    
    private Map<String, Double> calculateCompletionRates(List<Workflow> workflows) {
        Map<String, Long> totalByType = workflows.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Workflow::getType,
                java.util.stream.Collectors.counting()
            ));
        
        Map<String, Long> completedByType = workflows.stream()
            .filter(w -> "COMPLETED".equals(w.getStatus()))
            .collect(java.util.stream.Collectors.groupingBy(
                Workflow::getType,
                java.util.stream.Collectors.counting()
            ));
        
        Map<String, Double> completionRates = new HashMap<>();
        totalByType.forEach((type, total) -> {
            long completed = completedByType.getOrDefault(type, 0L);
            completionRates.put(type, (double) completed / total * 100);
        });
        
        return completionRates;
    }
    
    private Map<String, Double> calculateAverageProcessingTime(List<Workflow> workflows) {
        // Calculate average processing time for completed workflows
        Map<String, Double> avgTimes = new HashMap<>();
        
        workflows.stream()
            .filter(w -> "COMPLETED".equals(w.getStatus()))
            .collect(java.util.stream.Collectors.groupingBy(Workflow::getType))
            .forEach((type, typeWorkflows) -> {
                double avgTime = typeWorkflows.stream()
                    .mapToLong(w -> java.time.Duration.between(w.getCreatedAt(), w.getUpdatedAt()).toHours())
                    .average()
                    .orElse(0.0);
                avgTimes.put(type, avgTime);
            });
        
        return avgTimes;
    }
    
    private Map<String, Integer> identifyBottlenecks(List<Workflow> workflows) {
        // Identify bottlenecks by current step
        return workflows.stream()
            .filter(w -> !"COMPLETED".equals(w.getStatus()))
            .collect(java.util.stream.Collectors.groupingBy(
                Workflow::getCurrentStep,
                java.util.stream.Collectors.summingInt(w -> 1)
            ));
    }
    
    private void handleTimeoutException(Workflow workflow) {
        workflow.setPriority("HIGH");
        escalateWorkflow(workflow, "Workflow timeout exceeded");
    }
    
    private void handlePolicyException(Workflow workflow, String details) {
        workflow.setCurrentStep("POLICY_REVIEW");
        workflow.setAssignedTo("policy_reviewer");
    }
    
    private void handleTechnicalException(Workflow workflow, String details) {
        workflow.setCurrentStep("TECHNICAL_REVIEW");
        workflow.setAssignedTo("technical_support");
    }
    
    private void handleContentPublished(Blog blog) {
        // Create post-publication workflow for monitoring
        createContentWorkflow(blog, blog.getAuthor(), "POST_PUBLICATION_MONITORING");
    }
    
    private void handleContentUpdated(Blog blog) {
        // Create update review workflow
        createContentWorkflow(blog, blog.getAuthor(), "UPDATE_REVIEW");
    }
    
    private void handleContentArchived(Blog blog) {
        // Handle archival workflow
        System.out.println("Content archived: " + blog.getTitle());
    }
    
    private void handleContentDeleted(Blog blog) {
        // Handle deletion workflow
        System.out.println("Content deleted: " + blog.getTitle());
    }
    
    private String convertConfigurationToJson(Map<String, Object> config) {
        // Convert configuration to JSON string
        StringBuilder json = new StringBuilder("{");
        config.forEach((key, value) -> 
            json.append("\"").append(key).append("\":\"").append(value).append("\","));
        if (json.length() > 1) {
            json.setLength(json.length() - 1);
        }
        json.append("}");
        return json.toString();
    }
    
    private String convertChecksToJson(Map<String, Boolean> checks) {
        StringBuilder json = new StringBuilder("{");
        checks.forEach((key, value) -> 
            json.append("\"").append(key).append("\":").append(value).append(","));
        if (json.length() > 1) {
            json.setLength(json.length() - 1);
        }
        json.append("}");
        return json.toString();
    }
}