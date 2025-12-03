import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { api } from '../api';
import type { User } from '../types/userTypes';

interface ServerMessage {
  text: string;
  status: number | null;
}

export interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  serverMessage: ServerMessage;
  sessionStartTime: number | null;
}

// === ФУНКЦИЯ: проверка, жива ли сессия (меньше 7 дней) ===
const isSessionAlive = (): boolean => {
  const sessionStart = localStorage.getItem('sessionStart');
  if (!sessionStart) return false;

  const daysPassed =
    (Date.now() - Number(sessionStart)) / (1000 * 60 * 60 * 24);
  return daysPassed <= 7;
};

// === Инициализация состояния из localStorage ===
const loadStateFromStorage = (): Partial<AuthState> => {
  try {
    const accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');
    const sessionStart = localStorage.getItem('sessionStart');

    if (accessToken && refreshToken && sessionStart && isSessionAlive()) {
      return {
        accessToken,
        refreshToken,
        sessionStartTime: Number(sessionStart),
        isAuthenticated: true,
      };
    } else {
      // Если больше 7 дней — чистим всё
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('sessionStart');
      return {};
    }
  } catch (err) {
    return {};
  }
};

const initialState: AuthState = {
  user: null,
  accessToken: null,
  refreshToken: null,
  isAuthenticated: false,
  isLoading: true, // важно! пока грузим профиль — показываем лоадер
  serverMessage: { text: '', status: null },
  sessionStartTime: null,
  ...loadStateFromStorage(), // ← восстанавливаем токены и статус
};

// === ASYNC THUNKS ===

// Загрузка профиля (остается неизменным, интерцептор обработает 401)
export const fetchMe = createAsyncThunk(
  'auth/me',
  async (_, { rejectWithValue }) => {
    try {
      const response = await api.get<User>('/users/me');
      return response.data;
    } catch (error: any) {
      throw error; // Бросаем ошибку, чтобы её мог поймать initializeAuth или интерцептор
    }
  }
);

// 🔑 ИСПРАВЛЕНИЕ: Инициализация, явно возвращаем rejectWithValue при отсутствии сессии
export const initializeAuth = createAsyncThunk(
  'auth/initialize',
  async (_, { dispatch, rejectWithValue }) => {
    const token = localStorage.getItem('accessToken');
    if (token && isSessionAlive()) {
      // Если токен есть — пробуем загрузить профиль.
      // .unwrap() позволяет нам поймать ошибку, если fetchMe провалится
      return await dispatch(fetchMe()).unwrap();
    }
    // Если токена нет или сессия истекла:
    // 💡 ИСПРАВЛЕНИЕ: Явно возвращаем rejectWithValue, чтобы гарантировать,
    // что Redux получит валидное REJECTED-действие, а не undefined.
    return rejectWithValue({
      text: 'No valid session',
      status: null,
    } as ServerMessage);
  }
);

export const login = createAsyncThunk(
  'auth/login',
  async (
    credentials: { email: string; password: string },
    { rejectWithValue }
  ) => {
    try {
      const response = await api.post<{
        accessToken: string;
        refreshToken: string;
      }>('/auth/login', credentials);
      return response.data;
    } catch (error: any) {
      return rejectWithValue({
        text: error.response?.data?.message || 'Неверный email или пароль',
        status: error.response?.status || 401,
      } as ServerMessage);
    }
  }
);

export const register = createAsyncThunk(
  'auth/register',
  async (data: any, { rejectWithValue }) => {
    try {
      const response = await api.post<{
        accessToken: string;
        refreshToken: string;
      }>('/auth/register', data);
      return response.data;
    } catch (error: any) {
      return rejectWithValue({
        text: error.response?.data?.message || 'Ошибка регистрации',
        status: error.response?.status || 400,
      } as ServerMessage);
    }
  }
);

export const logout = createAsyncThunk('auth/logout', async () => {
  try {
    await api.post('/auth/logout');
  } catch (err) {
    console.warn('Logout endpoint не ответил');
  }
});

export const refreshTokens = createAsyncThunk(
  'auth/refreshTokens',
  async (_, { rejectWithValue }) => {
    try {
      const currentRefreshToken = localStorage.getItem('refreshToken');
      if (!currentRefreshToken || !isSessionAlive()) {
        throw new Error('Session expired');
      }

      const response = await api.post<{
        accessToken: string;
        refreshToken?: string;
      }>('/auth/refresh-token', {
        refreshToken: currentRefreshToken,
      });

      const newAccessToken = response.data.accessToken;
      const newRefreshToken = response.data.refreshToken ?? currentRefreshToken;

      localStorage.setItem('accessToken', newAccessToken);
      localStorage.setItem('refreshToken', newRefreshToken);

      return { accessToken: newAccessToken, refreshToken: newRefreshToken };
    } catch (error) {
      // При неудачном рефреше очищаем всё
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('sessionStart');
      return rejectWithValue({
        text: 'Сессия истекла, требуется повторный вход',
        status: 401,
      } as ServerMessage);
    }
  }
);

// === SLICE ===

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    clearServerMessage(state) {
      state.serverMessage = { text: '', status: null };
    },
    forceLogout(state) {
      // Чистим состояние и локальное хранилище
      state.user = null;
      state.accessToken = null;
      state.refreshToken = null;
      state.isAuthenticated = false;
      state.sessionStartTime = null;
      state.isLoading = false;
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('sessionStart');
    },
  },
  extraReducers: (builder) => {
    builder // === ИНИЦИАЛИЗАЦИЯ ПРИ ЗАГРУЗКЕ СТРАНИЦЫ ===
      .addCase(initializeAuth.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(initializeAuth.fulfilled, (state, action) => {
        state.isLoading = false; // Здесь action.payload — это User, полученный из fetchMe
        state.user = action.payload;
        state.isAuthenticated = true;
      })
      .addCase(initializeAuth.rejected, (state) => {
        state.isLoading = false;
        state.isAuthenticated = false;
        state.user = null;
      }) // === LOGIN & REGISTER ===

      .addCase(login.pending, (state) => {
        state.isLoading = true;
        state.serverMessage = { text: 'Входим...', status: null };
      })
      .addCase(login.fulfilled, (state, action) => {
        state.isLoading = false;
        state.isAuthenticated = true;
        state.accessToken = action.payload.accessToken;
        state.refreshToken = action.payload.refreshToken;
        state.sessionStartTime = Date.now();

        localStorage.setItem('accessToken', action.payload.accessToken);
        localStorage.setItem('refreshToken', action.payload.refreshToken);
        localStorage.setItem('sessionStart', Date.now().toString());

        state.serverMessage = { text: 'Добро пожаловать!', status: 200 };
      })
      .addCase(login.rejected, (state, action) => {
        state.isLoading = false;
        state.serverMessage = (action.payload as ServerMessage) ?? {
          text: 'Ошибка входа',
          status: 401,
        };
      })

      .addCase(register.fulfilled, (state, action) => {
        state.isLoading = false;
        state.isAuthenticated = true;
        state.accessToken = action.payload.accessToken;
        state.refreshToken = action.payload.refreshToken;
        state.sessionStartTime = Date.now();

        localStorage.setItem('accessToken', action.payload.accessToken);
        localStorage.setItem('refreshToken', action.payload.refreshToken);
        localStorage.setItem('sessionStart', Date.now().toString());

        state.serverMessage = { text: 'Регистрация успешна!', status: 201 };
      }) // === FETCH ME ===

      .addCase(fetchMe.fulfilled, (state, action) => {
        state.user = action.payload;
        state.isAuthenticated = true;
      }) // === REFRESH TOKENS ===

      .addCase(refreshTokens.fulfilled, (state, action) => {
        state.accessToken = action.payload.accessToken;
        state.refreshToken = action.payload.refreshToken;
        state.isAuthenticated = true;
        state.isLoading = false;
      })
      .addCase(refreshTokens.rejected, (state) => {
        // Полная очистка при провале refresh
        state.isAuthenticated = false;
        state.user = null;
        state.accessToken = null;
        state.refreshToken = null;
        state.sessionStartTime = null;
        state.isLoading = false; // Локальное хранилище уже очищено внутри самого thunk refreshTokens
      }) // === LOGOUT — РАБОЧАЯ ВЕРСИЯ ===

      .addCase(logout.fulfilled, (state) => {
        // Чистим состояние
        state.user = null;
        state.accessToken = null;
        state.refreshToken = null;
        state.isAuthenticated = false;
        state.sessionStartTime = null;
        state.isLoading = false; // Чистим локальное хранилище

        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('sessionStart');
      })
      .addCase(logout.rejected, (state) => {
        // Если сервер не ответил, мы все равно должны очистить локальное состояние
        state.user = null;
        state.accessToken = null;
        state.refreshToken = null;
        state.isAuthenticated = false;
        state.sessionStartTime = null;

        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('sessionStart');
      });
  },
});

export const { clearServerMessage, forceLogout } = authSlice.actions;
export default authSlice.reducer;
