package com.examly.springapp.controller;

import com.examly.springapp.model.Workflow;
import com.examly.springapp.model.Blog;
import com.examly.springapp.model.User;
import com.examly.springapp.service.WorkflowService;
import com.examly.springapp.repository.BlogRepository;
import com.examly.springapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workflows")
@CrossOrigin(origins = "*")
public class WorkflowController {
    
    @Autowired
    private WorkflowService workflowService;
    
    @Autowired
    private BlogRepository blogRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/content/{blogId}")
    public ResponseEntity<Workflow> createContentWorkflow(
            @PathVariable Long blogId,
            @RequestParam Long initiatorId,
            @RequestParam String workflowType) {
        
        Blog blog = blogRepository.findById(blogId).orElse(null);
        User initiator = userRepository.findById(initiatorId).orElse(null);
        
        if (blog == null || initiator == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Workflow workflow = workflowService.createContentWorkflow(blog, initiator, workflowType);
        return ResponseEntity.ok(workflow);
    }
    
    @PutMapping("/{workflowId}/process")
    public ResponseEntity<Void> processWorkflow(
            @PathVariable Long workflowId,
            @RequestParam String action,
            @RequestBody(required = false) String comments) {
        
        workflowService.processEditorialWorkflow(workflowId, action, comments);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/assign-moderator")
    public ResponseEntity<String> assignModerator(
            @RequestParam String contentType,
            @RequestParam String reportReason) {
        
        String assignedModerator = workflowService.autoAssignModerator(contentType, reportReason);
        return ResponseEntity.ok(assignedModerator);
    }
    
    @PostMapping("/compliance-check/{blogId}")
    public ResponseEntity<Boolean> checkCompliance(@PathVariable Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElse(null);
        if (blog == null) {
            return ResponseEntity.badRequest().build();
        }
        
        boolean isCompliant = workflowService.checkPolicyCompliance(blog);
        return ResponseEntity.ok(isCompliant);
    }
    
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getWorkflowAnalytics() {
        Map<String, Object> analytics = workflowService.getWorkflowAnalytics();
        return ResponseEntity.ok(analytics);
    }
    
    @PostMapping("/{workflowId}/exception")
    public ResponseEntity<Void> handleException(
            @PathVariable Long workflowId,
            @RequestParam String exceptionType,
            @RequestBody String details) {
        
        workflowService.handleWorkflowException(workflowId, exceptionType, details);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/lifecycle/{blogId}")
    public ResponseEntity<Void> manageContentLifecycle(
            @PathVariable Long blogId,
            @RequestParam String lifecycleEvent) {
        
        Blog blog = blogRepository.findById(blogId).orElse(null);
        if (blog == null) {
            return ResponseEntity.badRequest().build();
        }
        
        workflowService.manageContentLifecycle(blog, lifecycleEvent);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/custom")
    public ResponseEntity<Workflow> createCustomWorkflow(
            @RequestParam String workflowName,
            @RequestParam String workflowType,
            @RequestBody Map<String, Object> configuration) {
        
        Workflow workflow = workflowService.createCustomWorkflow(workflowName, workflowType, configuration);
        return ResponseEntity.ok(workflow);
    }
}