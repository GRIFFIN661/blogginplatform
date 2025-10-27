import React, { useState } from 'react';
import { login } from '../services/authService';

export default function Login({ onNavigate, onLogin }) {
  const [formData, setFormData] = useState({ username: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    const result = await login(formData);
    
    if (result.error) {
      setError(result.error);
    } else {
      localStorage.setItem('token', result.token);
      const userData = result.user || { id: Date.now(), username: formData.username, role: 'ROLE_USER' };
      onLogin(userData);
      window.location.href = '/dashboard';
    }
    
    setLoading(false);
  };

  return (
    <div className="auth-container">
      <div className="auth-form">
        <h2>Welcome Back</h2>
        <p>Sign in to your account</p>
        
        {error && <div className="error-message">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <input 
            type="text" 
            name="username" 
            placeholder="Username or Email"
            value={formData.username}
            onChange={handleChange}
            required 
          />
          
          <input 
            type="password" 
            name="password" 
            placeholder="Password"
            value={formData.password}
            onChange={handleChange}
            required 
          />
          
          <div className="form-options">
            <label className="checkbox">
              <input type="checkbox" />
              Remember me
            </label>
            <button type="button" className="link-btn">Forgot password?</button>
          </div>
          
          <button type="submit" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>
        
        <p>Don't have an account? 
          <button className="link-btn" onClick={() => onNavigate('signup')}>Sign up</button>
        </p>
      </div>
    </div>
  );
}
