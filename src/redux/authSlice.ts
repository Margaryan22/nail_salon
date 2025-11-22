// src/store/slices/authSlice.ts

import {
  createAsyncThunk,
  createSlice,
  type PayloadAction,
} from '@reduxjs/toolkit';
import { api } from '../api';
import type { User } from '../types/userTypes';

interface ServerMessage {
  text: string;
  status: number | null;
}

// Ответ от /auth/login и /auth/register
interface AuthLoginResponse {
  accessToken: string;
  refreshToken: string;
  userId: number;
  email: string;
  role: 'CLIENT' | 'MASTER' | 'ADMIN';
}

export interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  serverMessage: ServerMessage;
}

const initialState: AuthState = {
  user: null,
  accessToken: localStorage.getItem('accessToken') || null,
  refreshToken: localStorage.getItem('refreshToken') || null,
  isAuthenticated: !!localStorage.getItem('accessToken'),
  isLoading: false,
  serverMessage: { text: '', status: null },
};

// === THUNKS ===

// Логин
export const login = createAsyncThunk<
  { authData: AuthLoginResponse; user: User },
  { email: string; password: string },
  { rejectValue: ServerMessage }
>('auth/login', async (credentials, { rejectWithValue, dispatch }) => {
  try {
    const response = await api.post<AuthLoginResponse>(
      '/auth/login',
      credentials
    );
    const authData = response.data;

    // Сразу сохраняем токены
    localStorage.setItem('accessToken', authData.accessToken);
    localStorage.setItem('refreshToken', authData.refreshToken);

    // Получаем полные данные пользователя
    const userResponse = await api.get<User>('/users/me');
    const user = userResponse.data;

    return { authData, user };
  } catch (error: any) {
    return rejectWithValue({
      text: error.response?.data?.message || 'Неверный email или пароль',
      status: error.response?.status || 401,
    });
  }
});

// Регистрация (аналогично)
export const register = createAsyncThunk<
  { authData: AuthLoginResponse; user: User },
  {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phone: string;
    role: string;
  },
  { rejectValue: ServerMessage }
>('auth/register', async (data, { rejectWithValue, dispatch }) => {
  try {
    const response = await api.post<AuthLoginResponse>('/auth/register', data);
    const authData = response.data;

    localStorage.setItem('accessToken', authData.accessToken);
    localStorage.setItem('refreshToken', authData.refreshToken);

    const userResponse = await api.get<User>('/users/me');
    const user = userResponse.data;

    return { authData, user };
  } catch (error: any) {
    return rejectWithValue({
      text: error.response?.data?.message || 'Ошибка регистрации',
      status: error.response?.status || 500,
    });
  }
});

// Получение текущего пользователя
export const fetchMe = createAsyncThunk<
  User,
  void,
  { rejectValue: ServerMessage }
>('auth/fetchMe', async (_, { rejectWithValue }) => {
  try {
    const response = await api.get<User>('/users/me');
    return response.data;
  } catch (error: any) {
    return rejectWithValue({
      text: 'Сессия истекла',
      status: error.response?.status || 401,
    });
  }
});

// Логаут
export const logout = createAsyncThunk<void, void>('auth/logout', async () => {
  try {
    await api.post('/auth/logout');
  } catch (err) {
    console.warn('Сервер не ответил на logout');
  }
});

// === SLICE ===
const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    clearServerMessage: (state) => {
      state.serverMessage = { text: '', status: null };
    },
  },
  extraReducers: (builder) => {
    builder
      // Успешный логин и регистрация
      .addCase(login.fulfilled, (state, action) => {
        state.isLoading = false;
        state.isAuthenticated = true;
        state.user = action.payload.user; // ← уже полный User!
        state.accessToken = action.payload.authData.accessToken;
        state.refreshToken = action.payload.authData.refreshToken;
        state.serverMessage = { text: 'Успешный вход!', status: 200 };
      })
      .addCase(register.fulfilled, (state, action) => {
        state.isLoading = false;
        state.isAuthenticated = true;
        state.user = action.payload.user;
        state.accessToken = action.payload.authData.accessToken;
        state.refreshToken = action.payload.authData.refreshToken;
        state.serverMessage = { text: 'Регистрация успешна!', status: 200 };
      })

      // Получение профиля
      .addCase(fetchMe.fulfilled, (state, action) => {
        state.user = action.payload;
        state.isAuthenticated = true;
        state.isLoading = false;
      })

      // Логаут
      .addCase(logout.fulfilled, (state) => {
        state.user = null;
        state.accessToken = null;
        state.refreshToken = null;
        state.isAuthenticated = false;
        state.isLoading = false;
        state.serverMessage = { text: 'Вы вышли из аккаунта', status: 200 };

        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
      })

      // Загрузка
      .addCase(login.pending, (state) => {
        state.isLoading = true;
        state.serverMessage = { text: 'Вход...', status: null };
      })
      .addCase(register.pending, (state) => {
        state.isLoading = true;
        state.serverMessage = { text: 'Регистрация...', status: null };
      })

      // Ошибки
      .addMatcher(
        (action) => action.type.endsWith('/rejected'),
        (state, action: PayloadAction<ServerMessage | undefined>) => {
          state.isLoading = false;
          state.serverMessage = action.payload || {
            text: 'Ошибка',
            status: 500,
          };
        }
      );
  },
});

export const { clearServerMessage } = authSlice.actions;
export default authSlice.reducer;
