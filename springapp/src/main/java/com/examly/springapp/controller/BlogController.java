package com.examly.springapp.controller;

import com.examly.springapp.model.Blog;
import com.examly.springapp.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class BlogController {
    
    @Autowired
    private BlogService blogService;
    


    @GetMapping
    public ResponseEntity<List<Blog>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAllBlogs());
    }
    
    @PostMapping
    public ResponseEntity<Blog> createBlog(@RequestBody Blog blog) {
        try {
            System.out.println("Received blog creation request: " + blog.getTitle());
            Blog createdBlog = blogService.createBlog(blog);
            System.out.println("Blog created successfully with ID: " + createdBlog.getId());
            return ResponseEntity.ok(createdBlog);
        } catch (Exception e) {
            System.err.println("Error creating blog: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable Long id) {
        Blog blog = blogService.getBlogById(id);
        if (blog == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(blog);
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementView(@PathVariable Long id) {
        blogService.incrementViews(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Blog> updateBlog(@PathVariable Long id, @RequestBody Blog blogDetails) {
        Blog updatedBlog = blogService.updateBlog(id, blogDetails);
        if (updatedBlog == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedBlog);
    }

    @DeleteMapping("/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        try {
            System.out.println("=== DELETE REQUEST RECEIVED ===");
            System.out.println("Blog ID to delete: " + id);
            System.out.println("ID type: " + id.getClass().getSimpleName());
            
            boolean deleted = blogService.deleteBlog(id);
            System.out.println("Delete operation result: " + deleted);
            
            if (!deleted) {
                System.out.println("Blog not found or could not be deleted");
                return ResponseEntity.notFound().build();
            }
            
            System.out.println("Blog deleted successfully - returning 204");
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("ERROR in deleteBlog endpoint: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}