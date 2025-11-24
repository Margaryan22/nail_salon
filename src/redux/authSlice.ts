// src/redux/authSlice.ts

import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { api } from '../api';
import type { User } from '../types/userTypes';

interface ServerMessage {
  text: string;
  status: number | null;
}

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
export const login = createAsyncThunk<
  AuthLoginResponse,
  { email: string; password: string },
  { rejectValue: ServerMessage }
>('auth/login', async (credentials, { rejectWithValue }) => {
  try {
    const response = await api.post<AuthLoginResponse>(
      '/auth/login',
      credentials
    );
    return response.data;
  } catch (error: any) {
    return rejectWithValue({
      text: error.response?.data?.message || 'Неверныййййй email или пароль',
      status: error.response?.status || 403,
    });
  }
});

export const register = createAsyncThunk<
  AuthLoginResponse,
  {
    email: string;
    password: string;
    firstName: string;
    lastName: string | null;
    phone: string;
    role: 'CLIENT' | 'MASTER' | 'ADMIN';
    permissionsLevel?: number;
  },
  { rejectValue: ServerMessage }
>('auth/register', async (data, { rejectWithValue }) => {
  try {
    const response = await api.post<AuthLoginResponse>('/auth/register', data);
    return response.data;
  } catch (error: any) {
    return rejectWithValue({
      text: error.response?.data?.message || 'Ошибка регистрации',
      status: error.response?.status || 400,
    });
  }
});

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
      text: 'Не удалось загрузить профиль',
      status: error.response?.status || 401,
    });
  }
});

export const logout = createAsyncThunk('auth/logout', async () => {
  try {
    await api.post('/auth/logout');
  } catch (err) {
    console.warn('Logout endpoint не ответил');
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
    // Login
    builder
      .addCase(login.pending, (state) => {
        state.isLoading = true;
        state.serverMessage = { text: 'Входим...', status: null };
      })
      .addCase(login.fulfilled, (state, action) => {
        state.isLoading = false;
        state.isAuthenticated = true;
        state.accessToken = action.payload.accessToken;
        state.refreshToken = action.payload.refreshToken;
        localStorage.setItem('accessToken', action.payload.accessToken);
        localStorage.setItem('refreshToken', action.payload.refreshToken);
        state.serverMessage = { text: 'Успешный вход!', status: 200 };
      })
      .addCase(login.rejected, (state, action) => {
        state.isLoading = false;
        state.serverMessage = action.payload ?? {
          text: 'Ошибка входа',
          status: 500,
        };
      })

      // Register
      .addCase(register.pending, (state) => {
        state.isLoading = true;
        state.serverMessage = { text: 'Регистрируем...', status: null };
      })
      .addCase(register.fulfilled, (state, action) => {
        state.isLoading = false;
        state.isAuthenticated = true;
        state.accessToken = action.payload.accessToken;
        state.refreshToken = action.payload.refreshToken;
        localStorage.setItem('accessToken', action.payload.accessToken);
        localStorage.setItem('refreshToken', action.payload.refreshToken);
        state.serverMessage = { text: 'Регистрация успешна!', status: 201 };
      })
      .addCase(register.rejected, (state, action) => {
        state.isLoading = false;
        state.serverMessage = action.payload ?? {
          text: 'Ошибка регистрации',
          status: 500,
        };
      })

      // fetchMe
      .addCase(fetchMe.fulfilled, (state, action) => {
        state.user = action.payload;
      })

      // logout
      .addCase(logout.fulfilled, (state) => {
        state.user = null;
        state.accessToken = null;
        state.refreshToken = null;
        state.isAuthenticated = false;
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
      });
  },
});

export const { clearServerMessage } = authSlice.actions;
export default authSlice.reducer;
