const API_BASE_URL = process.env.NODE_ENV === 'production' ? 'http://localhost:8080' : '';

// Generic API methods
export const get = async (url) => {
  try {
    const response = await fetch(`${API_BASE_URL}${url}`);
    return await response.json();
  } catch (error) {
    console.error('API GET error:', error);
    return null;
  }
};

export const post = async (url, data) => {
  try {
    const response = await fetch(`${API_BASE_URL}${url}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    return await response.json();
  } catch (error) {
    console.error('API POST error:', error);
    return null;
  }
};

const api = {
  // Blog endpoints
  blogs: {
    getAll: () => fetch(`${API_BASE_URL}/api/blogs`),
    getById: (id) => fetch(`${API_BASE_URL}/api/blogs/${id}`),
    create: (blog) => fetch(`${API_BASE_URL}/api/blogs`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(blog)
    }),
    update: (id, blog) => fetch(`${API_BASE_URL}/api/blogs/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(blog)
    }),
    delete: (id) => fetch(`${API_BASE_URL}/api/blogs/${id}`, {
      method: 'DELETE'
    })
  },

  // Comment endpoints
  comments: {
    getByBlogId: (blogId) => fetch(`${API_BASE_URL}/api/comments/${blogId}`),
    create: (blogId, text) => fetch(`${API_BASE_URL}/api/comments/${blogId}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ text })
    })
  },

  // Report endpoints
  reports: {
    getAll: () => fetch(`${API_BASE_URL}/api/reports`),
    create: (report) => fetch(`${API_BASE_URL}/api/reports`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(report)
    })
  },

  // Auth endpoints
  auth: {
    login: (credentials) => fetch(`${API_BASE_URL}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials)
    }),
    signup: (userData) => fetch(`${API_BASE_URL}/api/auth/signup`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userData)
    })
  }
};

export default api;