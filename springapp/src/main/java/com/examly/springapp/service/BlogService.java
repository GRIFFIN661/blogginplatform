package com.examly.springapp.service;

import com.examly.springapp.model.Blog;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BlogService {
    
    @Autowired
    private BlogRepository blogRepository;
    
    @Autowired
    private SEOService seoService;
    
    public Blog createBlog(Blog blog) {
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());
        
        if (blog.getSlug() == null || blog.getSlug().isEmpty()) {
            blog.setSlug(seoService.generateSlug(blog.getTitle()));
        }
        
        // Set default values
        if (blog.getViews() == null) {
            blog.setViews(0L);
        }
        if (blog.getStatus() == null) {
            blog.setStatus("PUBLISHED");
        }
        
        Blog savedBlog = blogRepository.save(blog);
        System.out.println("Blog created with ID: " + savedBlog.getId());
        return savedBlog;
    }
    
    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }
    
    public List<Blog> getBlogsByAuthor(User author) {
        return blogRepository.findByAuthor(author);
    }

    public Blog getBlogById(Long id) {
        Optional<Blog> blog = blogRepository.findById(id);
        if (blog.isPresent()) {
            Blog b = blog.get();
            b.setViews(b.getViews() + 1);
            blogRepository.save(b);
            return b;
        }
        return null;
    }

    public Blog updateBlog(Long id, Blog blogDetails) {
        return blogRepository.findById(id).map(blog -> {
            if (blogDetails.getTitle() != null) {
                blog.setTitle(blogDetails.getTitle());
                blog.setSlug(seoService.generateSlug(blogDetails.getTitle()));
            }
            if (blogDetails.getContent() != null) {
                blog.setContent(blogDetails.getContent());
            }
            if (blogDetails.getTags() != null) {
                blog.setTags(blogDetails.getTags());
            }
            if (blogDetails.getCategory() != null) {
                blog.setCategory(blogDetails.getCategory());
            }
            if (blogDetails.getSeoTitle() != null) {
                blog.setSeoTitle(blogDetails.getSeoTitle());
            }
            if (blogDetails.getSeoDescription() != null) {
                blog.setSeoDescription(blogDetails.getSeoDescription());
            }
            
            blog.setPublished(blogDetails.isPublished());
            blog.setUpdatedAt(LocalDateTime.now());
            
            return blogRepository.save(blog);
        }).orElse(null);
    }

    public boolean deleteBlog(Long id) {
        try {
            System.out.println("BlogService: Checking if blog exists with ID: " + id);
            boolean exists = blogRepository.existsById(id);
            System.out.println("BlogService: Blog exists: " + exists);
            
            if (exists) {
                System.out.println("BlogService: Deleting blog with ID: " + id);
                blogRepository.deleteById(id);
                System.out.println("BlogService: Blog deleted successfully");
                return true;
            } else {
                System.out.println("BlogService: Blog not found with ID: " + id);
                return false;
            }
        } catch (Exception e) {
            System.err.println("BlogService: Error deleting blog: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public long getTotalBlogCount() {
        return blogRepository.count();
    }
    
    public void incrementViews(Long blogId) {
        Optional<Blog> blogOpt = blogRepository.findById(blogId);
        if (blogOpt.isPresent()) {
            Blog blog = blogOpt.get();
            blog.setViews((blog.getViews() != null ? blog.getViews() : 0) + 1);
            blogRepository.save(blog);
        }
    }
}