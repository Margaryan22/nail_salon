// src/App.tsx

import React from 'react';
import './scss/app.scss';

import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { useAppSelector } from './hooks'; // ← обязательно должен быть

import RegistrationPage from './pages/RegistrationPage';
import LoginPage from './pages/LoginPage';
import ProfilePage from './pages/ProfilePage';
import ServiceCatalogPage from './pages/ServiceCatalogPage';
import UserAccountPage from './pages/UserAccountPage';

const App: React.FC = () => {
  const { isAuthenticated, isLoading } = useAppSelector((state) => state.auth);
  const location = useLocation();

  // Пока идёт любая загрузка — показываем минимальный лоадер (или ничего)
  if (isLoading) {
    return (
      <div className='app-container'>
        <div className='global-loading'>Загрузка...</div>
      </div>
    );
  }

  return (
    <div className='app-container'>
      <Routes>
        {/* Главная — редирект в зависимости от авторизации */}
        <Route
          path='/'
          element={
            <Navigate to={isAuthenticated ? '/profile' : '/login'} replace />
          }
        />

        {/* Публичные страницы — доступны всегда */}
        <Route path='/login' element={<LoginPage />} />
        <Route path='/registration' element={<RegistrationPage />} />

        {/* ЗАЩИЩЁННЫЕ страницы — только для авторизованных */}
        <Route
          path='/profile'
          element={
            isAuthenticated ? (
              <ProfilePage />
            ) : (
              <Navigate to='/login' state={{ from: location }} replace />
            )
          }
        />

        <Route
          path='/services'
          element={
            isAuthenticated ? (
              <ServiceCatalogPage />
            ) : (
              <Navigate to='/login' state={{ from: location }} replace />
            )
          }
        />

        <Route
          path='/account'
          element={
            isAuthenticated ? (
              <UserAccountPage />
            ) : (
              <Navigate to='/login' state={{ from: location }} replace />
            )
          }
        />

        {/* 404 — кидаем на логин или главную */}
        <Route
          path='*'
          element={
            <Navigate to={isAuthenticated ? '/profile' : '/login'} replace />
          }
        />
      </Routes>
    </div>
  );
};

export default App;
