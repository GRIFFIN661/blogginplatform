import React, { useState, useEffect } from 'react';
import * as blogService from '../services/blogService';

export default function Home({ onNavigate }) {
  const [featuredBlogs, setFeaturedBlogs] = useState([]);
  const [stats, setStats] = useState({ totalBlogs: 0, totalUsers: 0 });

  useEffect(() => {
    fetchFeaturedContent();
  }, []);

  const fetchFeaturedContent = async () => {
    try {
      const blogs = await blogService.getAllBlogs();
      setFeaturedBlogs(blogs.slice(0, 3));
      setStats({ totalBlogs: blogs.length, totalUsers: 150 });
    } catch (error) {
      console.error('Error fetching featured content:', error);
    }
  };

  return (
    <div className="home">
      <section className="hero">
        <h1>Professional Blogging Platform</h1>
        <p>Create, share, and discover amazing content with our comprehensive blogging solution</p>
        <div className="cta">
          <button className="btn primary" onClick={() => onNavigate('signup')}>Get Started</button>
          <button className="btn outline" onClick={() => onNavigate('login')}>Login</button>
        </div>
      </section>

      <section className="stats">
        <div className="stat-item">
          <h3>{stats.totalBlogs}</h3>
          <p>Blog Posts</p>
        </div>
        <div className="stat-item">
          <h3>{stats.totalUsers}</h3>
          <p>Active Users</p>
        </div>
        <div className="stat-item">
          <h3>24/7</h3>
          <p>Support</p>
        </div>
      </section>

      <section className="featured">
        <h2>Featured Content</h2>
        <div className="featured-grid">
          {featuredBlogs.map(blog => (
            <div key={blog.id} className="featured-card" onClick={() => onNavigate(`blog/${blog.id}`)} style={{cursor: 'pointer'}}>
              <h3>{blog.title}</h3>
              <p>{blog.content ? blog.content.substring(0, 100) + '...' : 'No content preview available'}</p>
              <span className="read-more">Read More →</span>
            </div>
          ))}
        </div>
      </section>

      <section className="features">
        <h2>Platform Features</h2>
        <div className="features-grid">
          <div className="feature">
            <h3>📝 Rich Editor</h3>
            <p>Advanced text editor with media support</p>
          </div>
          <div className="feature">
            <h3>📊 Analytics</h3>
            <p>Track your content performance</p>
          </div>
          <div className="feature">
            <h3>💬 Comments</h3>
            <p>Engage with your audience</p>
          </div>
          <div className="feature">
            <h3>🔒 Security</h3>
            <p>Enterprise-grade security</p>
          </div>
        </div>
      </section>
    </div>
  );
}
