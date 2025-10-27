import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import * as blogService from '../services/blogService';

export default function ContentEditor({ user, onSave, onCancel }) {
  const { id: blogId } = useParams();
  const [blog, setBlog] = useState({
    title: '',
    content: '',
    tags: '',
    category: '',
    seoTitle: '',
    seoDescription: '',
    published: false
  });
  const [loading, setLoading] = useState(false);
  const [previewMode, setPreviewMode] = useState(false);
  const [mediaFiles, setMediaFiles] = useState([]);

  useEffect(() => {
    if (blogId) {
      fetchBlog();
    }
  }, [blogId]);

  const fetchBlog = async () => {
    try {
      const blogData = await blogService.getBlog(blogId);
      setBlog(blogData);
    } catch (error) {
      console.error('Error fetching blog:', error);
    }
  };

  const handleSave = async (publish = true) => {
    if (!blog.title.trim() || !blog.content.trim()) {
      alert('Please fill in both title and content');
      return;
    }
    
    setLoading(true);
    try {
      const blogData = { 
        title: blog.title.trim(),
        content: blog.content.trim(),
        category: blog.category || 'General',
        tags: Array.isArray(blog.tags) ? blog.tags : (blog.tags ? blog.tags.split(',').map(t => t.trim()) : []),
        seoTitle: blog.seoTitle || blog.title,
        seoDescription: blog.seoDescription || blog.content.substring(0, 150),
        published: publish,
        status: publish ? 'PUBLISHED' : 'DRAFT',
        author: {
          id: user?.id,
          username: user?.username || 'Anonymous'
        }
      };
      
      let result;
      if (blogId) {
        result = await blogService.updateBlog(blogId, blogData);
      } else {
        result = await blogService.createBlog(blogData);
      }
      
      if (result) {
        alert('Blog published successfully!');
        // Reset form
        setBlog({ title: '', content: '', tags: '', category: '', seoTitle: '', seoDescription: '', published: false });
        onSave && onSave();
      } else {
        alert('Failed to save blog. Please try again.');
      }
    } catch (error) {
      console.error('Error saving blog:', error);
      alert('Error saving blog: ' + (error.message || 'Please try again'));
    } finally {
      setLoading(false);
    }
  };

  const handleMediaUpload = async (event) => {
    const file = event.target.files[0];
    if (file) {
      const formData = new FormData();
      formData.append('file', file);
      
      try {
        // Mock upload - replace with actual upload service
        const mediaUrl = URL.createObjectURL(file);
        setMediaFiles([...mediaFiles, { name: file.name, url: mediaUrl }]);
        
        // Insert media into content
        const mediaMarkdown = file.type.startsWith('image/') 
          ? `![${file.name}](${mediaUrl})`
          : `[${file.name}](${mediaUrl})`;
        
        setBlog({
          ...blog,
          content: blog.content + '\n\n' + mediaMarkdown
        });
      } catch (error) {
        console.error('Error uploading media:', error);
      }
    }
  };

  const insertFormatting = (format) => {
    const textarea = document.getElementById('content-editor');
    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const selectedText = blog.content.substring(start, end);
    
    let formattedText = '';
    switch (format) {
      case 'bold':
        formattedText = `**${selectedText}**`;
        break;
      case 'italic':
        formattedText = `*${selectedText}*`;
        break;
      case 'heading':
        formattedText = `## ${selectedText}`;
        break;
      case 'link':
        formattedText = `[${selectedText}](url)`;
        break;
      case 'code':
        formattedText = `\`${selectedText}\``;
        break;
      default:
        formattedText = selectedText;
    }
    
    const newContent = blog.content.substring(0, start) + formattedText + blog.content.substring(end);
    setBlog({ ...blog, content: newContent });
  };

  return (
    <div className="content-editor">
      <div className="editor-header">
        <div className="editor-title">
          <h2>{blogId ? '✏️ Edit Blog' : '✨ Create New Blog'}</h2>
          <p className="editor-subtitle">Share your thoughts with the world</p>
        </div>
        <div className="editor-actions">
          <button 
            className={`btn tab-btn ${!previewMode ? 'active' : ''}`}
            onClick={() => setPreviewMode(false)}
          >
            📝 Write
          </button>
          <button 
            className={`btn tab-btn ${previewMode ? 'active' : ''}`}
            onClick={() => setPreviewMode(true)}
          >
            👁️ Preview
          </button>
          <button 
            className="btn outline"
            onClick={onCancel}
          >
            Cancel
          </button>
          <button 
            className="btn primary publish-btn"
            onClick={() => handleSave(true)}
            disabled={loading || !blog.title || !blog.content}
          >
            {loading ? '⏳ Publishing...' : '🚀 Publish Blog'}
          </button>
        </div>
      </div>

      {!previewMode ? (
        <div className="editor-content">
          <div className="editor-main-area">
            <div className="title-section">
              <input
                type="text"
                placeholder="✨ Enter your blog title..."
                value={blog.title}
                onChange={(e) => setBlog({ ...blog, title: e.target.value })}
                className="title-input"
              />
              <div className="title-counter">{blog.title.length}/100</div>
            </div>

            <div className="content-section">
              <div className="formatting-toolbar">
                <div className="toolbar-group">
                  <button onClick={() => insertFormatting('bold')} title="Bold">𝐁</button>
                  <button onClick={() => insertFormatting('italic')} title="Italic">𝐼</button>
                  <button onClick={() => insertFormatting('heading')} title="Heading">H₁</button>
                </div>
                <div className="toolbar-group">
                  <button onClick={() => insertFormatting('link')} title="Link">🔗</button>
                  <button onClick={() => insertFormatting('code')} title="Code">{'</>'}</button>
                  <button onClick={() => document.getElementById('media-upload').click()} title="Upload Image">🖼️</button>
                </div>
                <div className="word-count">{blog.content.split(' ').filter(w => w).length} words</div>
              </div>
              
              <textarea
                id="content-editor"
                placeholder="📝 Start writing your amazing blog post...\n\nTip: You can use Markdown formatting:\n- **bold text**\n- *italic text*\n- # Heading\n- [link](url)\n- `code`"
                value={blog.content}
                onChange={(e) => setBlog({ ...blog, content: e.target.value })}
                className="content-textarea"
              />
            </div>
          </div>

          <div className="editor-sidebar">
            <div className="editor-section">
              <h3>📂 Categories & Tags</h3>
              <select
                value={blog.category}
                onChange={(e) => setBlog({ ...blog, category: e.target.value })}
                className="category-select"
              >
                <option value="">Select Category</option>
                <option value="Technology">💻 Technology</option>
                <option value="Lifestyle">🌟 Lifestyle</option>
                <option value="Travel">✈️ Travel</option>
                <option value="Food">🍕 Food</option>
                <option value="Business">💼 Business</option>
                <option value="Health">🏥 Health</option>
                <option value="Education">📚 Education</option>
              </select>
              
              <input
                type="text"
                placeholder="🏷️ Add tags (comma separated)"
                value={Array.isArray(blog.tags) ? blog.tags.join(', ') : blog.tags || ''}
                onChange={(e) => setBlog({ ...blog, tags: e.target.value.split(',').map(tag => tag.trim()) })}
                className="tags-input"
              />
            </div>

            <div className="editor-section">
              <h3>🔍 SEO Optimization</h3>
              <input
                type="text"
                placeholder="SEO Title (optional)"
                value={blog.seoTitle}
                onChange={(e) => setBlog({ ...blog, seoTitle: e.target.value })}
                className="seo-input"
              />
              
              <textarea
                placeholder="SEO Description (optional)"
                value={blog.seoDescription}
                onChange={(e) => setBlog({ ...blog, seoDescription: e.target.value })}
                rows={3}
                className="seo-textarea"
              />
              <div className="seo-counter">{(blog.seoDescription || '').length}/160</div>
            </div>

            <div className="editor-section">
              <h3>📎 Media</h3>
              <input
                type="file"
                accept="image/*,video/*,audio/*,.pdf,.doc,.docx"
                onChange={handleMediaUpload}
                style={{ display: 'none' }}
                id="media-upload"
              />
              <button 
                className="btn outline media-btn"
                onClick={() => document.getElementById('media-upload').click()}
              >
                📁 Upload Media
              </button>
              
              {mediaFiles.length > 0 && (
                <div className="media-list">
                  {mediaFiles.map((file, index) => (
                    <div key={index} className="media-item">
                      <span>📄 {file.name}</span>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      ) : (
        <div className="preview-content">
          <div className="preview-header">
            <h1 className="preview-title">{blog.title || 'Untitled Blog Post'}</h1>
            <div className="preview-meta">
              <span className="meta-item">📂 {blog.category || 'Uncategorized'}</span>
              <span className="meta-item">👤 {user?.username || 'Anonymous'}</span>
              <span className="meta-item">📅 {new Date().toLocaleDateString()}</span>
            </div>
            {blog.tags && blog.tags.length > 0 && (
              <div className="preview-tags">
                {(Array.isArray(blog.tags) ? blog.tags : blog.tags.split(',')).map((tag, index) => (
                  <span key={index} className="preview-tag">🏷️ {tag.trim()}</span>
                ))}
              </div>
            )}
          </div>
          <div className="preview-body">
            {blog.content ? (
              blog.content.split('\n').map((paragraph, index) => (
                <p key={index} className="preview-paragraph">{paragraph}</p>
              ))
            ) : (
              <p className="preview-placeholder">Start writing to see your preview...</p>
            )}
          </div>
        </div>
      )}
    </div>
  );
}