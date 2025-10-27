import api from './api';
import { blogEvents, BLOG_EVENTS } from './blogEvents';

export const getAllBlogs = async () => {
  try {
    const response = await api.blogs.getAll();
    if (response.ok) {
      const blogs = await response.json();
      return Array.isArray(blogs) ? blogs : [];
    } else {
      console.error('Failed to fetch blogs:', response.status);
      return [];
    }
  } catch (error) {
    console.error('Error fetching blogs:', error);
    return [];
  }
};

export const getBlogById = async (id) => {
  try {
    const response = await api.blogs.getById(id);
    if (response.ok) {
      const blog = await response.json();
      return blog || null;
    } else {
      console.error('Failed to fetch blog:', response.status);
      return null;
    }
  } catch (error) {
    console.error('Error fetching blog:', error);
    return null;
  }
};

export const createBlog = async (blog) => {
  try {
    const response = await api.blogs.create(blog);
    if (response.ok) {
      const result = await response.json();
      blogEvents.emit(BLOG_EVENTS.BLOG_CREATED, result);
      return result;
    } else {
      const errorText = await response.text();
      throw new Error(`HTTP ${response.status}: ${errorText}`);
    }
  } catch (error) {
    console.error('Error creating blog:', error);
    throw error;
  }
};

export const updateBlog = async (id, blog) => {
  try {
    const response = await api.blogs.update(id, blog);
    if (response.ok) {
      const result = await response.json();
      blogEvents.emit(BLOG_EVENTS.BLOG_UPDATED, result);
      return result;
    }
    return null;
  } catch (error) {
    console.error('Error updating blog:', error);
    return null;
  }
};

export const deleteBlog = async (id) => {
  try {
    console.log('Deleting blog with ID:', id);
    const response = await api.blogs.delete(id);
    console.log('Delete response status:', response.status);
    
    if (response.ok) {
      console.log('Blog deleted successfully, emitting event');
      blogEvents.emit(BLOG_EVENTS.BLOG_DELETED, { id });
      return true;
    } else {
      console.error('Delete failed with status:', response.status);
      const errorText = await response.text();
      console.error('Error response:', errorText);
      return false;
    }
  } catch (error) {
    console.error('Error deleting blog:', error);
    throw error;
  }
};

export const getUserBlogs = async (userId) => {
  try {
    const response = await fetch(`/api/blogs/user/${userId}`);
    return await response.json();
  } catch (error) {
    console.error('Error fetching user blogs:', error);
    return [];
  }
};

export const getBlog = async (id) => {
  const blog = await getBlogById(id);
  if (!blog) {
    return {
      title: '',
      content: '',
      tags: '',
      category: '',
      seoTitle: '',
      seoDescription: '',
      published: false
    };
  }
  return blog;
};

export const incrementView = async (id) => {
  try {
    const response = await fetch(`/api/blogs/${id}/view`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' }
    });
    return response.ok;
  } catch (error) {
    console.error('Error incrementing view:', error);
    return false;
  }
};