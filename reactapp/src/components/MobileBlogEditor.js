import React, { useState, useEffect } from 'react';

const MobileBlogEditor = ({ onSave, onCancel, initialData = null }) => {
  const [blog, setBlog] = useState({
    title: '',
    content: '',
    tags: [],
    category: '',
    status: 'DRAFT'
  });
  const [isOffline, setIsOffline] = useState(!navigator.onLine);
  const [savedDrafts, setSavedDrafts] = useState([]);
  const [showPreview, setShowPreview] = useState(false);

  useEffect(() => {
    // Load initial data if editing
    if (initialData) {
      setBlog(initialData);
    }

    // Load saved drafts from localStorage
    loadSavedDrafts();

    // Listen for online/offline events
    const handleOnline = () => setIsOffline(false);
    const handleOffline = () => setIsOffline(true);

    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, [initialData]);

  // Auto-save draft every 30 seconds
  useEffect(() => {
    const interval = setInterval(() => {
      if (blog.title || blog.content) {
        saveDraft();
      }
    }, 30000);

    return () => clearInterval(interval);
  }, [blog]);

  const loadSavedDrafts = () => {
    const drafts = JSON.parse(localStorage.getItem('blogDrafts') || '[]');
    setSavedDrafts(drafts);
  };

  const saveDraft = () => {
    const drafts = JSON.parse(localStorage.getItem('blogDrafts') || '[]');
    const draftId = initialData?.id || `draft_${Date.now()}`;
    
    const updatedDrafts = drafts.filter(d => d.id !== draftId);
    updatedDrafts.push({
      ...blog,
      id: draftId,
      savedAt: new Date().toISOString(),
      isOfflineDraft: isOffline
    });

    localStorage.setItem('blogDrafts', JSON.stringify(updatedDrafts));
    setSavedDrafts(updatedDrafts);
  };

  const handleSave = async () => {
    if (isOffline) {
      saveDraft();
      alert('Saved as draft. Will sync when online.');
      return;
    }

    try {
      await onSave(blog);
      // Remove from drafts after successful save
      const drafts = savedDrafts.filter(d => d.id !== blog.id);
      localStorage.setItem('blogDrafts', JSON.stringify(drafts));
    } catch (error) {
      console.error('Error saving blog:', error);
      saveDraft();
    }
  };

  const addTag = (tag) => {
    if (tag && !blog.tags.includes(tag)) {
      setBlog(prev => ({
        ...prev,
        tags: [...prev.tags, tag]
      }));
    }
  };

  const removeTag = (tagToRemove) => {
    setBlog(prev => ({
      ...prev,
      tags: prev.tags.filter(tag => tag !== tagToRemove)
    }));
  };

  const getWordCount = () => {
    return blog.content.trim().split(/\s+/).length;
  };

  const getReadingTime = () => {
    const wordsPerMinute = 200;
    const words = getWordCount();
    return Math.ceil(words / wordsPerMinute);
  };

  return (
    <div className="mobile-blog-editor">
      {/* Header */}
      <div className="mobile-editor-header">
        <button className="header-btn" onClick={onCancel}>
          ← Cancel
        </button>
        <div className="editor-status">
          {isOffline && <span className="offline-indicator">📴 Offline</span>}
          <span className="word-count">{getWordCount()} words</span>
        </div>
        <button className="header-btn save-btn" onClick={handleSave}>
          💾 Save
        </button>
      </div>

      {/* Editor Tabs */}
      <div className="editor-tabs">
        <button 
          className={!showPreview ? 'active' : ''}
          onClick={() => setShowPreview(false)}
        >
          ✏️ Edit
        </button>
        <button 
          className={showPreview ? 'active' : ''}
          onClick={() => setShowPreview(true)}
        >
          👁️ Preview
        </button>
      </div>

      {!showPreview ? (
        <div className="editor-content">
          {/* Title Input */}
          <div className="input-group">
            <input
              type="text"
              placeholder="Blog Title"
              value={blog.title}
              onChange={(e) => setBlog(prev => ({ ...prev, title: e.target.value }))}
              className="title-input"
            />
          </div>

          {/* Category Selection */}
          <div className="input-group">
            <select
              value={blog.category}
              onChange={(e) => setBlog(prev => ({ ...prev, category: e.target.value }))}
              className="category-select"
            >
              <option value="">Select Category</option>
              <option value="technology">Technology</option>
              <option value="lifestyle">Lifestyle</option>
              <option value="business">Business</option>
              <option value="health">Health</option>
              <option value="travel">Travel</option>
            </select>
          </div>

          {/* Tags Input */}
          <div className="input-group">
            <div className="tags-container">
              <div className="tags-list">
                {blog.tags.map((tag, index) => (
                  <span key={index} className="tag">
                    {tag}
                    <button onClick={() => removeTag(tag)}>×</button>
                  </span>
                ))}
              </div>
              <input
                type="text"
                placeholder="Add tags (press Enter)"
                onKeyPress={(e) => {
                  if (e.key === 'Enter') {
                    addTag(e.target.value.trim());
                    e.target.value = '';
                  }
                }}
                className="tag-input"
              />
            </div>
          </div>

          {/* Content Editor */}
          <div className="input-group">
            <textarea
              placeholder="Write your blog content in Markdown..."
              value={blog.content}
              onChange={(e) => setBlog(prev => ({ ...prev, content: e.target.value }))}
              className="content-textarea"
              rows={15}
            />
          </div>

          {/* Editor Stats */}
          <div className="editor-stats">
            <span>📖 {getReadingTime()} min read</span>
            <span>📝 {blog.content.length} characters</span>
            <span>🏷️ {blog.tags.length} tags</span>
          </div>

          {/* Quick Actions */}
          <div className="quick-actions">
            <button onClick={() => setBlog(prev => ({ ...prev, status: 'DRAFT' }))}>
              📄 Save as Draft
            </button>
            <button onClick={() => setBlog(prev => ({ ...prev, status: 'PUBLISHED' }))}>
              🚀 Publish
            </button>
            <button onClick={saveDraft}>
              💾 Save Offline
            </button>
          </div>
        </div>
      ) : (
        <div className="preview-content">
          <div className="preview-header">
            <h1>{blog.title || 'Untitled Blog'}</h1>
            <div className="preview-meta">
              <span>Category: {blog.category || 'Uncategorized'}</span>
              <span>Reading time: {getReadingTime()} minutes</span>
            </div>
            <div className="preview-tags">
              {blog.tags.map((tag, index) => (
                <span key={index} className="preview-tag">#{tag}</span>
              ))}
            </div>
          </div>
          <div className="preview-body">
            {blog.content.split('\n').map((paragraph, index) => (
              <p key={index}>{paragraph}</p>
            ))}
          </div>
        </div>
      )}

      {/* Saved Drafts Panel */}
      {savedDrafts.length > 0 && (
        <div className="drafts-panel">
          <h3>Saved Drafts ({savedDrafts.length})</h3>
          <div className="drafts-list">
            {savedDrafts.slice(0, 3).map((draft) => (
              <div key={draft.id} className="draft-item">
                <span className="draft-title">{draft.title || 'Untitled'}</span>
                <span className="draft-date">
                  {new Date(draft.savedAt).toLocaleDateString()}
                </span>
                <button onClick={() => setBlog(draft)}>Load</button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Offline Sync Status */}
      {isOffline && (
        <div className="offline-banner">
          📴 You're offline. Changes will be saved locally and synced when you're back online.
        </div>
      )}
    </div>
  );
};

export default MobileBlogEditor;