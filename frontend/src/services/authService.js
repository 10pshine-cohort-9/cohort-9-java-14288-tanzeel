import axiosClient from '../api/axiosClient';

const authService = {
  register: (userData) => {
    return axiosClient.post('/auth/register', userData);
  },

  login: (loginData) => {
    return axiosClient.post('/auth/login', loginData);
  },

  logout: () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  },

  getCurrentUser: () => {
    return JSON.parse(localStorage.getItem('user'));
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('authToken');
  },

  getAuthToken: () => {
    return localStorage.getItem('authToken');
  },

  setAuthToken: (token) => {
    localStorage.setItem('authToken', token);
  },

  setUser: (user) => {
    localStorage.setItem('user', JSON.stringify(user));
  },
};

export default authService;
