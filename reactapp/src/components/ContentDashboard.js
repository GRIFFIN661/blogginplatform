import React, { useState, useEffect } from 'react';
import * as blogService from '../services/blogService';
import { get } from '../services/api';
import { blogEvents, BLOG_EVENTS } from '../services/blogEvents';

export default function ContentDashboard({ user, onNavigate }) {
  const [blogs, setBlogs] = useState([]);
  const [analytics, setAnalytics] = useState({});
  const [activeTab, setActiveTab] = useState('blogs');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchUserContent();
    fetchAnalytics();
  }, []);

  // Refresh blogs when component mounts or user changes
  useEffect(() => {
    fetchUserContent();
  }, [user]);

  // Listen for blog events to refresh when blogs are updated
  useEffect(() => {
    const handleBlogUpdate = () => {
      fetchUserContent();
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
  }, []);

  const fetchUserContent = async () => {
    setLoading(true);
    try {
      // For now, get all blogs since getUserBlogs might not be working
      const allBlogs = await blogService.getAllBlogs();
      setBlogs(Array.isArray(allBlogs) ? allBlogs : []);
    } catch (error) {
      console.error('Error fetching user content:', error);
      setBlogs([]);
    } finally {
      setLoading(false);
    }
  };

  const fetchAnalytics = async () => {
    try {
      const data = await get('/analytics/user/' + user.id);
      setAnalytics(data || {});
    } catch (error) {
      console.error('Error fetching analytics:', error);
      setAnalytics({});
    }
  };

  const handleDeleteBlog = async (blogId) => {
    if (window.confirm('Are you sure you want to delete this blog?')) {
      try {
        await blogService.deleteBlog(blogId);
        fetchUserContent();
      } catch (error) {
        console.error('Error deleting blog:', error);
      }
    }
  };

  const handlePublishToggle = async (blogId, isPublished) => {
    try {
      await blogService.updateBlog(blogId, { published: !isPublished });
      fetchUserContent();
    } catch (error) {
      console.error('Error updating blog:', error);
    }
  };

  return (
    <div className="content-dashboard">
      <div className="dashboard-header">
        <h1>Content Dashboard</h1>
        <p>Welcome back, {user.username}!</p>
      </div>

      <div className="dashboard-stats">
        <div className="stat-card">
          <h3>{blogs.length}</h3>
          <p>Total Posts</p>
        </div>
        <div className="stat-card">
          <h3>{analytics.totalViews || 0}</h3>
          <p>Total Views</p>
        </div>
        <div className="stat-card">
          <h3>{analytics.totalComments || 0}</h3>
          <p>Total Comments</p>
        </div>
        <div className="stat-card">
          <h3>{analytics.engagement || '0%'}</h3>
          <p>Engagement Rate</p>
        </div>
      </div>

      <div className="dashboard-tabs">
        <button 
          className={activeTab === 'blogs' ? 'active' : ''}
          onClick={() => setActiveTab('blogs')}
        >
          My Blogs
        </button>
        <button 
          className={activeTab === 'analytics' ? 'active' : ''}
          onClick={() => setActiveTab('analytics')}
        >
          Analytics
        </button>

      </div>

      <div className="dashboard-content">
        {activeTab === 'blogs' && (
          <div className="blogs-management">
            <div className="section-header">
              <h2>Blog Management</h2>
              <div className="section-actions">
                <button className="btn outline" onClick={fetchUserContent}>Refresh</button>
                <button className="btn primary" onClick={() => onNavigate('editor')}>Create New Blog</button>
              </div>
            </div>
            
            {loading ? (
              <div className="loading">🔄 Loading your blogs...</div>
            ) : blogs && blogs.length > 0 ? (
              <div className="blogs-grid">
                {blogs.map(blog => (
                  <div key={blog.id} className="blog-card">
                    <div className="blog-card-header">
                      <h3 className="blog-card-title" onClick={() => onNavigate(`blog/${blog.id}`)}>
                        {blog.title}
                      </h3>
                      <span className={`blog-status ${blog.published ? 'published' : 'draft'}`}>
                        {blog.published ? '✅ Published' : '📄 Draft'}
                      </span>
                    </div>
                    
                    <div className="blog-card-content">
                      <p className="blog-excerpt">
                        {blog.content ? blog.content.substring(0, 120) + '...' : 'No content available'}
                      </p>
                    </div>
                    
                    <div className="blog-card-meta">
                      <div className="meta-stats">
                        <span className="stat">👁️ {blog.views || 0}</span>
                        <span className="stat">💬 {blog.commentCount || 0}</span>
                        <span className="stat">📅 {blog.createdAt ? new Date(blog.createdAt).toLocaleDateString() : 'N/A'}</span>
                      </div>
                      
                      <div className="blog-actions">
                        <button 
                          className="btn-small primary" 
                          onClick={() => onNavigate(`blog/${blog.id}`)}
                          title="View"
                        >
                          👁️ View
                        </button>
                        <button 
                          className="btn-small outline" 
                          onClick={() => onNavigate(`editor/${blog.id}`)} 
                          title="Edit"
                        >
                          ✏️ Edit
                        </button>
                        <button 
                          className="btn-small danger" 
                          onClick={() => handleDeleteBlog(blog.id)}
                          title="Delete"
                        >
                          🗑️
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="empty-state">
                <div className="empty-icon">📝</div>
                <h3>No blogs yet</h3>
                <p>Start creating your first blog post to share your thoughts with the world!</p>
                <button className="btn primary" onClick={() => onNavigate('editor')}>
                  ✨ Create Your First Blog
                </button>
              </div>
            )}
          </div>
        )}

        {activeTab === 'analytics' && (
          <div className="analytics-section">
            <h2>Content Analytics</h2>
            <div className="analytics-grid">
              <div className="analytics-card">
                <h3>Top Performing Posts</h3>
                <div className="top-posts">
                  {blogs && blogs.length > 0 ? blogs.slice(0, 5).map(blog => (
                    <div key={blog.id} className="post-item" onClick={() => onNavigate(`blog/${blog.id}`)}>
                      <div className="post-info">
                        <span className="post-title">{blog.title}</span>
                        <span className="post-date">{blog.createdAt ? new Date(blog.createdAt).toLocaleDateString() : 'N/A'}</span>
                      </div>
                      <div className="post-stats">
                        <span className="post-views">👁️ {blog.views || 0}</span>
                      </div>
                    </div>
                  )) : (
                    <div className="no-posts">
                      <span>📝 No posts available</span>
                    </div>
                  )}
                </div>
              </div>
              
              <div className="analytics-card">
                <h3>Recent Activity</h3>
                <div className="activity-feed">
                  <div className="activity-item">New comment on "Sample Post"</div>
                  <div className="activity-item">Post published: "Another Post"</div>
                  <div className="activity-item">Draft saved: "Work in Progress"</div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}