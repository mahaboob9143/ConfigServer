import axios from 'axios';

const BASE_URL = 'http://localhost:9090'; // your backend

const axiosInstance = axios.create({
  baseURL: BASE_URL,
});

// Inject token into headers
axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default axiosInstance;
