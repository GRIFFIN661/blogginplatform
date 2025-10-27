package com.examly.springapp.controller;

import com.examly.springapp.model.Comment;
import com.examly.springapp.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @GetMapping("/{blogId}")
    public ResponseEntity<List<Comment>> getCommentsByBlogId(@PathVariable Long blogId) {
        return ResponseEntity.ok(commentService.getCommentsByBlogId(blogId));
    }
    
    @PostMapping("/{blogId}")
    public ResponseEntity<Comment> createComment(@PathVariable Long blogId, @RequestBody Map<String, String> request) {
        String text = request.get("text");
        String author = request.get("author");
        Long userId = request.get("userId") != null ? Long.parseLong(request.get("userId")) : null;
        Comment comment = commentService.createComment(blogId, text, author, userId);
        return ResponseEntity.ok(comment);
    }
}