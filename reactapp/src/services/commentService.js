import api from './api';

export const getComments = async (blogId) => {
  try {
    const response = await api.comments.getByBlogId(blogId);
    return await response.json();
  } catch (error) {
    console.error('Error fetching comments:', error);
    return [];
  }
};

export const postComment = async (blogId, text, author, userId) => {
  try {
    const response = await fetch(`/api/comments/${blogId}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ text, author, userId })
    });
    return await response.json();
  } catch (error) {
    console.error('Error posting comment:', error);
    return null;
  }
};

export const getCommentsByUser = async (userId, filter = 'all') => {
  try {
    const response = await fetch(`/api/comments/user/${userId}?filter=${filter}`);
    return await response.json();
  } catch (error) {
    console.error('Error fetching user comments:', error);
    return [];
  }
};

export const moderateComment = async (commentId, action) => {
  try {
    const response = await fetch(`/api/comments/${commentId}/moderate`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ action })
    });
    return await response.json();
  } catch (error) {
    console.error('Error moderating comment:', error);
    return null;
  }
};