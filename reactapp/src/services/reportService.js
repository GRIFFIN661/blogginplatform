import api from './api';

export const getReports = async () => {
  try {
    const response = await api.reports.getAll();
    return await response.json();
  } catch (error) {
    console.error('Error fetching reports:', error);
    return [];
  }
};

export const createReport = async (report) => {
  try {
    const response = await api.reports.create(report);
    return await response.json();
  } catch (error) {
    console.error('Error creating report:', error);
    return null;
  }
};

export const deleteBlog = async (id) => {
  try {
    const response = await api.blogs.delete(id);
    return response.ok;
  } catch (error) {
    console.error('Error deleting blog:', error);
    return false;
  }
};