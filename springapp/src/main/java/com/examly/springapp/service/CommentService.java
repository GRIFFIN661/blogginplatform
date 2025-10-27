package com.examly.springapp.service;

import com.examly.springapp.model.Blog;
import com.examly.springapp.model.Comment;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.BlogRepository;
import com.examly.springapp.repository.CommentRepository;
import com.examly.springapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private BlogRepository blogRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Comment> getCommentsByBlogId(Long blogId) {
        return commentRepository.findByBlogId(blogId);
    }
    
    public Comment createComment(Long blogId, String text, String author, Long userId) {
        Blog blog = blogRepository.findById(blogId).orElse(null);
        if (blog != null) {
            Comment comment = new Comment();
            comment.setBlog(blog);
            comment.setText(text);
            comment.setAuthor(author);
            comment.setCreatedDate(LocalDateTime.now());
            
            if (userId != null) {
                User user = userRepository.findById(userId).orElse(null);
                comment.setUser(user);
            }
            
            return commentRepository.save(comment);
        }
        return null;
    }
    
    public Comment createComment(Long blogId, String text) {
        return createComment(blogId, text, "Anonymous", null);
    }
    
    public Comment addComment(Long blogId, Comment comment) {
        Blog blog = blogRepository.findById(blogId).orElse(null);
        if (blog != null) {
            comment.setBlog(blog);
            return commentRepository.save(comment);
        }
        return null;
    }
}