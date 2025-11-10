import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/v1.0';

const api = axios.create({
  baseURL: API_BASE_URL,
});

// Add JWT token to every request
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default api;