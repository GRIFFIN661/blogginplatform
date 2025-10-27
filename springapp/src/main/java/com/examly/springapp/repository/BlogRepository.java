package com.examly.springapp.repository;

import com.examly.springapp.model.Blog;
import com.examly.springapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByAuthor(User author);
    List<Blog> findByPublishedTrue();
    List<Blog> findByCategory(String category);
    Optional<Blog> findBySlug(String slug);
    List<Blog> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);
    long countByPublishedTrue();
}