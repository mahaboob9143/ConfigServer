import { configureStore } from '@reduxjs/toolkit';
import authReducer from './authSlice';
// Add more reducers when needed

const store = configureStore({
  reducer: {
    auth: authReducer,
  },
});

export default store;
