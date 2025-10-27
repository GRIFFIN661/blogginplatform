import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import * as blogService from '../services/blogService';
import { blogEvents, BLOG_EVENTS } from '../services/blogEvents';

export default function BlogList() {
  const [blogs, setBlogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();

  const fetchBlogs = useCallback(async () => {
    setLoading(true);
    try {
      const blogsData = await blogService.getAllBlogs();
      console.log('Fetched blogs from database:', blogsData);
      setBlogs(Array.isArray(blogsData) ? blogsData.filter(blog => blog.published !== false) : []);
    } catch (error) {
      console.error('Error fetching blogs:', error);
      setBlogs([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchBlogs();
  }, [fetchBlogs]);

  // Auto-refresh every 30 seconds to catch new blogs
  useEffect(() => {
    const interval = setInterval(() => {
      fetchBlogs();
    }, 30000);

    return () => clearInterval(interval);
  }, [fetchBlogs]);

  // Listen for focus events to refresh when user returns to tab
  useEffect(() => {
    const handleFocus = () => {
      fetchBlogs();
    };

    window.addEventListener('focus', handleFocus);
    return () => window.removeEventListener('focus', handleFocus);
  }, [fetchBlogs]);

  // Listen for blog events to refresh when blogs are updated
  useEffect(() => {
    const handleBlogUpdate = () => {
      fetchBlogs();
    };

    blogEvents.on(BLOG_EVENTS.BLOG_CREATED, handleBlogUpdate);
    blogEvents.on(BLOG_EVENTS.BLOG_UPDATED, handleBlogUpdate);
    blogEvents.on(BLOG_EVENTS.BLOG_DELETED, handleBlogUpdate);
    blogEvents.on(BLOG_EVENTS.BLOGS_REFRESH, handleBlogUpdate);

    return () => {
      blogEvents.off(BLOG_EVENTS.BLOG_CREATED, handleBlogUpdate);
      blogEvents.off(BLOG_EVENTS.BLOG_UPDATED, handleBlogUpdate);
      blogEvents.off(BLOG_EVENTS.BLOG_DELETED, handleBlogUpdate);
      blogEvents.off(BLOG_EVENTS.BLOGS_REFRESH, handleBlogUpdate);
    };
  }, [fetchBlogs]);

  const filteredBlogs = blogs.filter(blog => {
    if (!searchTerm || !searchTerm.trim()) return true;
    const searchLower = searchTerm.toLowerCase().trim();
    
    // Search in title
    if (blog.title && blog.title.toLowerCase().includes(searchLower)) return true;
    
    // Search in content
    if (blog.content && blog.content.toLowerCase().includes(searchLower)) return true;
    
    // Search in author username
    if (blog.author && typeof blog.author === 'object' && blog.author.username && 
        blog.author.username.toLowerCase().includes(searchLower)) return true;
    
    // Search in author if it's a string
    if (blog.author && typeof blog.author === 'string' && 
        blog.author.toLowerCase().includes(searchLower)) return true;
    
    // Search in category
    if (blog.category && blog.category.toLowerCase().includes(searchLower)) return true;
    
    // Search in tags
    if (blog.tags) {
      if (Array.isArray(blog.tags)) {
        return blog.tags.some(tag => tag && tag.toLowerCase().includes(searchLower));
      } else if (typeof blog.tags === 'string') {
        return blog.tags.toLowerCase().includes(searchLower);
      }
    }
    
    return false;
  });

  return (
    <div className="blog-list-page">
      <div className="blog-list-header">
        <h1>📚 All Blog Posts</h1>
        <p>Discover amazing content from our community</p>
        
        <div className="search-section">
          <input
            type="text"
            placeholder="🔍 Search blogs..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input"
          />
          <button 
            className="refresh-btn" 
            onClick={fetchBlogs}
            disabled={loading}
            title="Refresh blogs"
          >
            {loading ? '🔄' : '↻'} Refresh
          </button>
        </div>
        
        <div className="blog-stats">
          <span className="stat-item">📝 {blogs.length} Total Posts</span>
          <span className="stat-item">👀 {blogs.reduce((sum, blog) => sum + (blog.views || 0), 0)} Total Views</span>
        </div>
      </div>

      {loading ? (
        <div className="loading">🔄 Loading blogs...</div>
      ) : filteredBlogs.length > 0 ? (
        <div className="blogs-grid">
          {filteredBlogs.map(blog => (
            <div key={blog.id} className="blog-card" onClick={() => navigate(`/blog/${blog.id}`)}>
              <div className="blog-card-header">
                <h3 className="blog-card-title">{blog.title}</h3>
                <div className="blog-meta">
                  <span className="blog-author">👤 {blog.author?.username || blog.author || 'Anonymous'}</span>
                  <span className="blog-date">📅 {blog.createdAt ? new Date(blog.createdAt).toLocaleDateString() : 'N/A'}</span>
                </div>
              </div>
              
              <div className="blog-card-content">
                <p className="blog-excerpt">
                  {blog.content ? blog.content.substring(0, 150) + '...' : 'No content available'}
                </p>
              </div>
              
              <div className="blog-card-footer">
                <div className="blog-tags">
                  {blog.category && (
                    <span className="blog-category">🏷️ {blog.category}</span>
                  )}
                </div>
                <div className="blog-stats">
                  <span className="stat">👁️ {blog.views || 0}</span>
                  <span className="stat">💬 {blog.commentCount || 0}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="empty-state">
          <div className="empty-icon">📝</div>
          <h3>No blogs found</h3>
          <p>{searchTerm ? 'Try a different search term' : 'No published blogs available yet'}</p>
        </div>
      )}
    </div>
  );
}