import api from './api';

export const loginUser = async (credentials) => {
  const response = await api.post('/auth/login', credentials);
  return response.data;
};

export const registerUser = async (userData) => {
  const response = await api.post('/auth/register', userData);
  return response.data;
};

export const refreshAuthToken = async (refreshToken) => {
  const response = await api.post('/auth/refresh', { refreshToken });
  return response.data;
};

export const logoutUser = async (refreshToken) => {
  try {
    if (refreshToken) {
      await api.post('/auth/logout', { refreshToken });
    }
  } catch (err) {
    console.warn('Logout notification to backend failed:', err);
  } finally {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('userName');
    localStorage.removeItem('userRole');
  }
};