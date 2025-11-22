// src/api.ts
import axios from 'axios';
import { logout } from './redux/authSlice';

const API_BASE_URL = 'https://nails-salon-back.whysargis.ru';
let store: any;

export const injectStore = (_store: any) => {
  store = _store;
};
// === Все эндпоинты ===
export const ENDPOINTS = {
  AUTH: {
    REGISTER: `${API_BASE_URL}/auth/register`,
    LOGIN: `${API_BASE_URL}/auth/login`,
    REFRESH_TOKEN: `${API_BASE_URL}/auth/refresh-token`,
    LOGOUT: `${API_BASE_URL}/auth/logout`,
    CHECK_EMAIL: `${API_BASE_URL}/auth/check-email`,
    CHECK_PHONE: `${API_BASE_URL}/auth/check-phone`,
  },
  USERS: {
    ME: `${API_BASE_URL}/users/me`,
    SEARCH: `${API_BASE_URL}/users/search`,
    MASTERS: `${API_BASE_URL}/users/role/master`,
    CLIENT: `${API_BASE_URL}/users/role/client`,
    ADMINS: `${API_BASE_URL}/users/admins`,
  },
  SERVICES: {
    ALL: `${API_BASE_URL}/services`,
    SEARCH: `${API_BASE_URL}/services/search`,
    CATEGORIES: `${API_BASE_URL}/services/categories`,
  },
  APPOINTMENTS: {
    BASE: `${API_BASE_URL}/appointments`,
    CLIENT: `${API_BASE_URL}/client`,
    DATE: `${API_BASE_URL}/appointments/date`,
    AVAILABILITY: `${API_BASE_URL}/appointments/availability`,
    CREATE: `${API_BASE_URL}/appointments`,
  },
  SCHEDULE: `${API_BASE_URL}/master_schedule`,
} as const;

// === Основной api — для всех защищённых запросов ===
export const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Подставляем accessToken, если он есть
api.interceptors.request.use((config) => {
  const accessToken = localStorage.getItem('accessToken');

  if (
    accessToken &&
    accessToken !== 'null' &&
    accessToken !== 'undefined' &&
    accessToken.trim() !== ''
  ) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});
// Рефреш токена
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) throw new Error('No refresh token');

        const { data } = await axios.post(
          `${API_BASE_URL}/auth/refresh-token`,
          {
            refreshToken,
          }
        );

        // ← ВОТ ЭТО ГЛАВНОЕ ИСПРАВЛЕНИЕ!
        const { accessToken, refreshToken: newRefreshToken } = data;

        localStorage.setItem('accessToken', accessToken);
        if (newRefreshToken) {
          localStorage.setItem('refreshToken', newRefreshToken);
        }

        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');

        if (store) {
          store.dispatch(logout());
        } else {
          window.location.href = '/login';
        }
      }
    }

    return Promise.reject(error);
  }
);
// Публичный API — без токена
export const publicApi = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});
