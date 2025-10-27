import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import ReactMarkdown from 'react-markdown';
import * as blogService from './services/blogService';
import * as commentService from './services/commentService';
import { blogEvents, BLOG_EVENTS } from './services/blogEvents';
import Home from './components/Home';
import Login from './components/Login';
import Signup from './components/Signup';
import ContentDashboard from './components/ContentDashboard';
import ContentEditor from './components/ContentEditor';
import CommentManagement from './components/CommentManagement';
import AnalyticsDashboard from './components/AnalyticsDashboard';
import NotificationCenter from './components/NotificationCenter';
import EnhancedAdminPanel from './components/EnhancedAdminPanel';
import BlogView from './components/BlogView';
import BlogList from './components/BlogList';
import './styles.css';

function AppContent() {
  const navigate = useNavigate();
  const [blogs, setBlogs] = useState([]);
  const [selectedBlog, setSelectedBlog] = useState(null);
  const [comments, setComments] = useState([]);
  const [newBlog, setNewBlog] = useState({ title: '', content: '' });
  const [newComment, setNewComment] = useState('');
  const [currentView, setCurrentView] = useState('home'); // home, login, signup, dashboard, editor, comments, analytics, admin, mobile
  const [currentUser, setCurrentUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    checkAuthStatus();
    fetchBlogs();
  }, []);

  const checkAuthStatus = () => {
    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('userData');
    if (token && userData) {
      try {
        const user = JSON.parse(userData);
        setCurrentUser(user);
        setIsAuthenticated(true);
      } catch (error) {
        console.error('Error parsing user data:', error);
        localStorage.removeItem('token');
        localStorage.removeItem('userData');
      }
    }
  };

  const handleLogin = (user) => {
    setCurrentUser(user);
    setIsAuthenticated(true);
    localStorage.setItem('userData', JSON.stringify(user));
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('userData');
    setCurrentUser(null);
    setIsAuthenticated(false);
    navigate('/');
  };

  const handleNavigate = (view) => {
    navigate(`/${view}`);
    // Trigger refresh when navigating to blogs
    if (view === 'blogs') {
      blogEvents.emit(BLOG_EVENTS.BLOGS_REFRESH);
    }
  };

  const fetchBlogs = async () => {
    const blogsData = await blogService.getAllBlogs();
    setBlogs(blogsData);
  };

  const handleBlogSelect = async (blog) => {
    setSelectedBlog(blog);
    const commentsData = await commentService.getComments(blog.id);
    setComments(commentsData);
  };

  const handleBlogSubmit = async (e) => {
    e.preventDefault();
    if (newBlog.title && newBlog.content) {
      await blogService.createBlog(newBlog);
      setNewBlog({ title: '', content: '' });
      fetchBlogs();
    }
  };

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    if (newComment && selectedBlog) {
      await commentService.postComment(selectedBlog.id, newComment);
      setNewComment('');
      const commentsData = await commentService.getComments(selectedBlog.id);
      setComments(commentsData);
    }
  };

  return (
    <div className="app">
      <header className="app-header">
        <h1 onClick={() => navigate('/')} style={{cursor: 'pointer'}}>Professional Blogging Platform</h1>
        
        {isAuthenticated ? (
          <>
            <nav className="main-nav">
              <button onClick={() => {
                navigate('/blogs');
                blogEvents.emit(BLOG_EVENTS.BLOGS_REFRESH);
              }}>📚 All Blogs</button>
              <button onClick={() => navigate('/dashboard')}>📊 Dashboard</button>
              <button onClick={() => navigate('/editor')}>📝 New Post</button>
              <button onClick={() => navigate('/analytics')}>📈 Analytics</button>
              {currentUser?.role === 'ROLE_ADMIN' && (
                <button onClick={() => navigate('/admin')}>⚙️ Admin</button>
              )}
            </nav>
            
            <div className="user-menu">
              <NotificationCenter userId={currentUser.id} />
              <span>Welcome, {currentUser.username}</span>
              <button className="btn outline" onClick={handleLogout}>Logout</button>
            </div>
          </>
        ) : (
          <nav className="auth-nav">
            <button onClick={() => navigate('/')}>Home</button>
            <button onClick={() => {
              navigate('/blogs');
              blogEvents.emit(BLOG_EVENTS.BLOGS_REFRESH);
            }}>📚 All Blogs</button>
            <button onClick={() => navigate('/login')}>Login</button>
            <button onClick={() => navigate('/signup')}>Sign Up</button>
          </nav>
        )}
      </header>

      <main className="app-main">
        <Routes>
          <Route path="/" element={<Home onNavigate={handleNavigate} />} />
          <Route path="/blogs" element={<BlogList />} />
          <Route path="/login" element={<Login onNavigate={handleNavigate} onLogin={handleLogin} />} />
          <Route path="/signup" element={<Signup onNavigate={handleNavigate} onLogin={handleLogin} />} />
          
          {isAuthenticated ? (
            <>
              <Route path="/dashboard" element={<ContentDashboard user={currentUser} onNavigate={(path) => navigate(`/${path}`)} />} />
              <Route path="/editor" element={<ContentEditor user={currentUser} onSave={() => navigate('/dashboard')} onCancel={() => navigate('/dashboard')} />} />
              <Route path="/editor/:id" element={<ContentEditor user={currentUser} onSave={() => navigate('/dashboard')} onCancel={() => navigate('/dashboard')} />} />
              <Route path="/blog/:id" element={<BlogView user={currentUser} />} />

              <Route path="/analytics" element={<AnalyticsDashboard />} />
              <Route path="/admin" element={<EnhancedAdminPanel />} />
              <Route path="*" element={<Navigate to="/dashboard" replace />} />
            </>
          ) : (
            <Route path="*" element={<Navigate to="/" replace />} />
          )}
        </Routes>
      </main>

      <footer className="app-footer">&copy; 2025 Blogging Platform</footer>
    </div>
  );
}

function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}

{/* Remove the old blog view code
*/
}

export default App;