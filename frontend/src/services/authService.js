import api from './api';

export const authService = {
  // Register new user
  register: (fullname, email, password) => {
    return api.post('/register', {
      fullname,
      email,
      password,
    });
  },

  // Login user
  login: (email, password) => {
    return api.post('/login', {
      email,
      password,
    });
  },

  // Activate account
  activate: (token) => {
    return api.get(`/activate?token=${token}`);
  },

  // Get user profile
  getProfile: () => {
    return api.get('/dashboard');
  },
};