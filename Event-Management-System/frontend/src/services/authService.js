import axios from 'axios';

const API = axios.create({
  baseURL: 'http://localhost:9090', // ✅ Backend running on port 9090
  headers: {
    'Content-Type': 'application/json'
  }
});

// 🔐 Register User
export const registerUser = async (userData) => {
  const response = await API.post('/user-api/user', userData);
  return response.data;
};

// 🔐 Login User
export const loginUser = async (credentials) => {
  const response = await API.post('/user-api/login', credentials);
  return response.data;
};
