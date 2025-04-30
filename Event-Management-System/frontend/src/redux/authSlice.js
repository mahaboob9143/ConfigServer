import { createSlice } from '@reduxjs/toolkit';

const initialToken = localStorage.getItem('token');
const initialRole = localStorage.getItem('role');

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    token: initialToken || null,
    role: initialRole || null,
    isAuthenticated: !!initialToken,
  },
  reducers: {
    loginSuccess: (state, action) => {
      const { token, role } = action.payload;
      state.token = token;
      state.role = role;
      state.isAuthenticated = true;
      localStorage.setItem('token', token);
      localStorage.setItem('role', role);
    },
    logout: (state) => {
      state.token = null;
      state.role = null;
      state.isAuthenticated = false;
      localStorage.removeItem('token');
      localStorage.removeItem('role');
    },
  },
});

export const { loginSuccess, logout } = authSlice.actions;
export default authSlice.reducer;
