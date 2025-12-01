// src/api.ts

import axios from 'axios';
import { logout } from './redux/authSlice';

const API_BASE_URL = 'https://nails-salon-back.whysargis.ru';
let store: any;

export const injectStore = (_store: any) => {
  store = _store;
};

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
  MASTER_SERVICES: {
    SERVICE_BY_MASTER: `${API_BASE_URL}/master-services/master/`,
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

// === Основной API (защищённые запросы) ===
export const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// === Публичный API (без токена) ===
export const publicApi = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// ВАЖНО: Подставляем токен ТОЛЬКО если URL НЕ начинается с /auth/
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');

  // НЕ ставим токен на запросы к /auth/**
  if (config.url?.includes('/auth/')) {
    return config;
  }

  if (token && token !== 'null' && token !== 'undefined' && token.trim()) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

// Рефреш токена + 401 обработка
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Если 401 и это НЕ запрос на рефреш или логин
    if (
      error.response?.status === 401 &&
      !originalRequest._retry &&
      !originalRequest.url?.includes('/auth/refresh-token') &&
      !originalRequest.url?.includes('/auth/login')
    ) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) throw new Error('No refresh token');

        const { data } = await publicApi.post('/auth/refresh-token', {
          refreshToken,
        });

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
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);
