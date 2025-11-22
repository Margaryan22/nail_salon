// src/redux/store.ts
import { configureStore } from '@reduxjs/toolkit';
import authReducer from './authSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    // Добавьте здесь другие редьюсеры (например, services, booking)
  },
});

// Определяем RootState и AppDispatch для строго типизированных хуков (TypeScript)
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
