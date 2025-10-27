import React, { useState, useEffect } from 'react';
import * as commentService from '../services/commentService';

export default function CommentManagement({ user }) {
  const [comments, setComments] = useState([]);
  const [filter, setFilter] = useState('all'); // all, pending, approved, spam
  const [loading, setLoading] = useState(false);
  const [selectedComments, setSelectedComments] = useState([]);

  useEffect(() => {
    fetchComments();
  }, [filter]);

  const fetchComments = async () => {
    setLoading(true);
    try {
      const commentsData = await commentService.getCommentsByUser(user.id, filter);
      setComments(Array.isArray(commentsData) ? commentsData : []);
    } catch (error) {
      console.error('Error fetching comments:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCommentAction = async (commentId, action) => {
    try {
      await commentService.moderateComment(commentId, action);
      fetchComments();
    } catch (error) {
      console.error('Error moderating comment:', error);
    }
  };

  const handleBulkAction = async (action) => {
    if (selectedComments.length === 0) return;
    
    try {
      await Promise.all(
        selectedComments.map(id => commentService.moderateComment(id, action))
      );
      setSelectedComments([]);
      fetchComments();
    } catch (error) {
      console.error('Error performing bulk action:', error);
    }
  };

  const toggleCommentSelection = (commentId) => {
    setSelectedComments(prev => 
      prev.includes(commentId) 
        ? prev.filter(id => id !== commentId)
        : [...prev, commentId]
    );
  };

  const selectAllComments = () => {
    setSelectedComments(
      selectedComments.length === comments.length 
        ? [] 
        : comments.map(c => c.id)
    );
  };

  return (
    <div className="comment-management">
      <div className="management-header">
        <h2>Comment Management</h2>
        <div className="comment-stats">
          <span>Total: {comments.length}</span>
          <span>Pending: {comments.filter(c => c.status === 'pending').length}</span>
          <span>Approved: {comments.filter(c => c.status === 'approved').length}</span>
        </div>
      </div>

      <div className="management-controls">
        <div className="filter-tabs">
          <button 
            className={filter === 'all' ? 'active' : ''}
            onClick={() => setFilter('all')}
          >
            All Comments
          </button>
          <button 
            className={filter === 'pending' ? 'active' : ''}
            onClick={() => setFilter('pending')}
          >
            Pending ({comments.filter(c => c.status === 'pending').length})
          </button>
          <button 
            className={filter === 'approved' ? 'active' : ''}
            onClick={() => setFilter('approved')}
          >
            Approved
          </button>
          <button 
            className={filter === 'spam' ? 'active' : ''}
            onClick={() => setFilter('spam')}
          >
            Spam
          </button>
        </div>

        {selectedComments.length > 0 && (
          <div className="bulk-actions">
            <span>{selectedComments.length} selected</span>
            <button 
              className="btn outline"
              onClick={() => handleBulkAction('approve')}
            >
              Approve
            </button>
            <button 
              className="btn outline"
              onClick={() => handleBulkAction('spam')}
            >
              Mark as Spam
            </button>
            <button 
              className="btn outline danger"
              onClick={() => handleBulkAction('delete')}
            >
              Delete
            </button>
          </div>
        )}
      </div>

      <div className="comments-table">
        <table>
          <thead>
            <tr>
              <th>
                <input
                  type="checkbox"
                  checked={selectedComments.length === comments.length && comments.length > 0}
                  onChange={selectAllComments}
                />
              </th>
              <th>Author</th>
              <th>Comment</th>
              <th>Post</th>
              <th>Date</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="7" className="loading">Loading comments...</td>
              </tr>
            ) : !comments || comments.length === 0 ? (
              <tr>
                <td colSpan="7" className="no-data">No comments found</td>
              </tr>
            ) : (
              comments && comments.map(comment => (
                <tr key={comment.id} className={`comment-row ${comment.status}`}>
                  <td>
                    <input
                      type="checkbox"
                      checked={selectedComments.includes(comment.id)}
                      onChange={() => toggleCommentSelection(comment.id)}
                    />
                  </td>
                  <td>
                    <div className="author-info">
                      <strong>{comment.authorName}</strong>
                      <small>{comment.authorEmail}</small>
                    </div>
                  </td>
                  <td>
                    <div className="comment-text">
                      {comment.text.length > 100 
                        ? comment.text.substring(0, 100) + '...'
                        : comment.text
                      }
                    </div>
                  </td>
                  <td>
                    <a href={`/blog/${comment.blogId}`} className="post-link">
                      {comment.blogTitle}
                    </a>
                  </td>
                  <td>{new Date(comment.createdDate).toLocaleDateString()}</td>
                  <td>
                    <span className={`status-badge ${comment.status}`}>
                      {comment.status}
                    </span>
                  </td>
                  <td className="actions">
                    {comment.status === 'pending' && (
                      <>
                        <button 
                          className="btn-icon approve"
                          onClick={() => handleCommentAction(comment.id, 'approve')}
                          title="Approve"
                        >
                          ✓
                        </button>
                        <button 
                          className="btn-icon spam"
                          onClick={() => handleCommentAction(comment.id, 'spam')}
                          title="Mark as Spam"
                        >
                          🚫
                        </button>
                      </>
                    )}
                    
                    {comment.status === 'approved' && (
                      <button 
                        className="btn-icon unapprove"
                        onClick={() => handleCommentAction(comment.id, 'unapprove')}
                        title="Unapprove"
                      >
                        ↩️
                      </button>
                    )}
                    
                    <button 
                      className="btn-icon reply"
                      onClick={() => {/* Handle reply */}}
                      title="Reply"
                    >
                      💬
                    </button>
                    
                    <button 
                      className="btn-icon delete"
                      onClick={() => handleCommentAction(comment.id, 'delete')}
                      title="Delete"
                    >
                      🗑️
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      <div className="comment-interaction">
        <h3>Quick Response Templates</h3>
        <div className="templates">
          <button className="template-btn">Thank you for your comment!</button>
          <button className="template-btn">Thanks for reading!</button>
          <button className="template-btn">Great question! Let me explain...</button>
        </div>
      </div>
    </div>
  );
}