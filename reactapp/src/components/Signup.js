import React, { useState } from 'react';
import { signup } from '../services/authService';

export default function Signup({ onNavigate, onLogin }) {
  const [formData, setFormData] = useState({ 
    username: '', 
    email: '', 
    password: '',
    role: 'ROLE_USER',
    firstName: '',
    lastName: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    const result = await signup(formData);
    
    if (result.error) {
      setError(result.error);
    } else {
      localStorage.setItem('token', result.token);
      const userData = result.user || { id: Date.now(), username: formData.username, role: formData.role };
      onLogin(userData);
      window.location.href = '/dashboard';
    }
    
    setLoading(false);
  };

  return (
    <div className="auth-container">
      <div className="auth-form">
        <h2>Create Account</h2>
        <p>Join our blogging community</p>
        
        {error && <div className="error-message">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <input
              type="text"
              name="firstName"
              placeholder="First Name"
              value={formData.firstName}
              onChange={handleChange}
              required
            />
            <input
              type="text"
              name="lastName"
              placeholder="Last Name"
              value={formData.lastName}
              onChange={handleChange}
              required
            />
          </div>
          
          <input
            type="text"
            name="username"
            placeholder="Username"
            value={formData.username}
            onChange={handleChange}
            required
          />
          
          <input
            type="email"
            name="email"
            placeholder="Email"
            value={formData.email}
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
            minLength="6"
          />
          
          <select
            name="role"
            value={formData.role}
            onChange={handleChange}
          >
            <option value="ROLE_USER">Blogger</option>
            <option value="ROLE_EDITOR">Editor</option>
            <option value="ROLE_ADMIN">Administrator</option>
          </select>
          
          <button type="submit" disabled={loading}>
            {loading ? 'Creating Account...' : 'Sign Up'}
          </button>
        </form>
        
        <p>Already have an account? 
          <button className="link-btn" onClick={() => onNavigate('login')}>Login</button>
        </p>
      </div>
    </div>
  );
}
