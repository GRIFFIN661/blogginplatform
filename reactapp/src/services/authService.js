import api from './api';

export const login = async (credentials) => {
  try {
    const response = await api.auth.login(credentials);
    const data = await response.json();
    if (data.token) {
      localStorage.setItem('token', data.token);
    }
    return data;
  } catch (error) {
    console.error('Error logging in:', error);
    return { error: 'Login failed' };
  }
};

export const signup = async (userData) => {
  try {
    const response = await api.auth.signup(userData);
    const data = await response.json();
    if (data.token) {
      localStorage.setItem('token', data.token);
    }
    return data;
  } catch (error) {
    console.error('Error signing up:', error);
    return { error: 'Signup failed' };
  }
};

export const logout = () => {
  localStorage.removeItem('token');
};

export const getToken = () => {
  return localStorage.getItem('token');
};

export const isAuthenticated = () => {
  return !!getToken();
};