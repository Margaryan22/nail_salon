// src/redux/authSlice.ts

import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { api } from '../api';
import type { User } from '../types/userTypes';

interface ServerMessage {
  text: string;
  status: number | null;
}

// Это будет хранить ПОЛНЫЙ оригинальный ответ от сервера
interface RawServerResponse {
  data: any;
  status: number;
  headers?: Record<string, string>;
  timestamp: string;
}

interface RawServerError {
  error: any;
  status?: number;
  timestamp: string;
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

  // Для пользователя (можно чистить)
  serverMessage: ServerMessage;

  // ПОЛНЫЙ оригинальный ответ от сервера — ВСЕГДА видим!
  lastServerResponse: RawServerResponse | null;

  // Полная ошибка от сервера — тоже ВСЕГДА видим!
  lastServerError: RawServerError | null;
}

const initialState: AuthState = {
  user: null,
  accessToken: null,
  refreshToken: null,
  isAuthenticated: false,
  isLoading: false,
  serverMessage: { text: '', status: null },
  lastServerResponse: null,
  lastServerError: null,
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
      text: error.response?.data?.message || 'Неверный email или пароль',
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
>('auth/me', async (_, { rejectWithValue }) => {
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
    // Очищает ТОЛЬКО красивое сообщение для пользователя
    clearServerMessage: (state) => {
      state.serverMessage = { text: '', status: null };
    },
    // При желании — можно добавить полную очистку логов (но редко нужно)
    clearServerLogs: (state) => {
      state.lastServerResponse = null;
      state.lastServerError = null;
    },
  },
  extraReducers: (builder) => {
    const captureResponse = (
      state: AuthState,
      action: any,
      successMessage: string,
      successStatus = 200
    ) => {
      state.lastServerResponse = {
        data: action.payload,
        status: action.meta?.arg?.status || successStatus,
        headers: action.meta?.response?.headers,
        timestamp: new Date().toISOString(),
      };
      state.serverMessage = { text: successMessage, status: successStatus };
    };

    const captureError = (state: AuthState, action: any) => {
      state.lastServerError = {
        error: action.payload || action.error,
        status: action.payload?.status || action.error?.response?.status,
        timestamp: new Date().toISOString(),
      };
    };

    builder
      // === LOGIN ===
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

        captureResponse(state, action, 'Успешный вход!', 200);
      })
      .addCase(login.rejected, (state, action) => {
        state.isLoading = false;
        state.serverMessage = action.payload ?? {
          text: 'Ошибка входа',
          status: 500,
        };
        captureError(state, action);
      })

      // === REGISTER ===
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

        captureResponse(state, action, 'Регистрация успешна!', 201);
      })
      .addCase(register.rejected, (state, action) => {
        state.isLoading = false;
        state.serverMessage = action.payload ?? {
          text: 'Ошибка регистрации',
          status: 500,
        };
        captureError(state, action);
      })

      // === FETCH ME ===
      .addCase(fetchMe.fulfilled, (state, action) => {
        state.user = action.payload;
        state.isAuthenticated = true;
        captureResponse(state, action, 'Профиль загружен', 200);
      })
      .addCase(fetchMe.rejected, (state, action) => {
        state.user = null;
        state.accessToken = null;
        state.refreshToken = null;
        state.isAuthenticated = false;
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        captureError(state, action);
      })

      // === LOGOUT ===
      .addCase(logout.fulfilled, (state) => {
        state.user = null;
        state.accessToken = null;
        state.refreshToken = null;
        state.isAuthenticated = false;
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        state.serverMessage = { text: 'Выход выполнен', status: 200 };
      });
  },
});

export const { clearServerMessage, clearServerLogs } = authSlice.actions;
export default authSlice.reducer;
