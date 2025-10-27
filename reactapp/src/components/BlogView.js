import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import ReactMarkdown from 'react-markdown';
import * as blogService from '../services/blogService';
import * as commentService from '../services/commentService';

export default function BlogView({ user }) {
  const { id } = useParams();
  const navigate = useNavigate();
  const [blog, setBlog] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [loading, setLoading] = useState(true);
  const [viewCounted, setViewCounted] = useState(false);

  useEffect(() => {
    fetchBlog();
    fetchComments();
  }, [id]);

  // Separate useEffect for view counting
  useEffect(() => {
    if (blog && !viewCounted) {
      incrementViewCount();
      setViewCounted(true);
    }
  }, [blog, viewCounted]);

  const fetchBlog = async () => {
    try {
      const blogData = await blogService.getBlogById(id);
      setBlog(blogData);
    } catch (error) {
      console.error('Error fetching blog:', error);
    } finally {
      setLoading(false);
    }
  };

  const incrementViewCount = async () => {
    try {
      await blogService.incrementView(id);
      // Update local blog state with incremented view count
      setBlog(prevBlog => ({
        ...prevBlog,
        views: (prevBlog.views || 0) + 1
      }));
    } catch (error) {
      console.error('Error incrementing view count:', error);
    }
  };

  const fetchComments = async () => {
    try {
      const commentsData = await commentService.getComments(id);
      setComments(Array.isArray(commentsData) ? commentsData : []);
    } catch (error) {
      console.error('Error fetching comments:', error);
    }
  };

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    if (newComment.trim()) {
      try {
        await commentService.postComment(id, newComment, user?.username || 'Anonymous', user?.id);
        setNewComment('');
        fetchComments();
      } catch (error) {
        console.error('Error posting comment:', error);
      }
    }
  };

  if (loading) {
    return <div className="loading">Loading blog...</div>;
  }

  if (!blog) {
    return (
      <div className="blog-not-found">
        <h2>Blog not found</h2>
        <button className="btn" onClick={() => navigate('/dashboard')}>
          Back to Dashboard
        </button>
      </div>
    );
  }

  return (
    <div className="blog-view">
      <div className="blog-header">
        <button className="btn outline" onClick={() => navigate('/dashboard')}>
          ← Back to Dashboard
        </button>
        {user && blog.author && user.id === blog.author.id && (
          <button className="btn" onClick={() => navigate(`/editor/${id}`)}>
            Edit Post
          </button>
        )}
      </div>

      <article className="blog-article">
        <header className="article-header">
          <h1>{blog.title}</h1>
          <div className="article-meta">
            <span>By {blog.author?.username || 'Anonymous'}</span>
            <span>•</span>
            <span>{blog.createdAt ? new Date(blog.createdAt).toLocaleDateString() : 'Unknown date'}</span>
            <span>•</span>
            <span>{blog.views || 0} views</span>
          </div>
          {blog.tags && blog.tags.length > 0 && (
            <div className="article-tags">
              {blog.tags.map((tag, index) => (
                <span key={index} className="tag">{tag}</span>
              ))}
            </div>
          )}
        </header>

        <div className="article-content">
          <ReactMarkdown>{blog.content}</ReactMarkdown>
        </div>
      </article>

      <section className="comments-section">
        <h3>Comments ({comments.length})</h3>
        
        {user && (
          <form onSubmit={handleCommentSubmit} className="comment-form">
            <textarea
              placeholder="Write a comment..."
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              rows={3}
            />
            <button type="submit" className="btn">Post Comment</button>
          </form>
        )}

        <div className="comments-list">
          {comments.length > 0 ? (
            comments.map((comment) => (
              <div key={comment.id} className="comment">
                <div className="comment-header">
                  <strong>👤 {comment.author || comment.user?.username || 'Anonymous'}</strong>
                  <span className="comment-date">
                    📅 {comment.createdDate ? new Date(comment.createdDate).toLocaleDateString() : ''}
                  </span>
                </div>
                <p className="comment-text">{comment.text}</p>
              </div>
            ))
          ) : (
            <p className="no-comments">No comments yet. Be the first to comment!</p>
          )}
        </div>
      </section>
    </div>
  );
}