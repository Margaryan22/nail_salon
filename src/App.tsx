// src/App.tsx

import React, { useEffect } from 'react';
import './scss/app.scss';

import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from './hooks';
import { initializeAuth } from './redux/authSlice'; // ← заменили fetchMe на initializeAuth

import RegistrationPage from './pages/RegistrationPage';
import LoginPage from './pages/LoginPage';
import ProfilePage from './pages/ProfilePage';
import ServiceCatalogPage from './pages/ServiceCatalogPage';
import UserAccountPage from './pages/UserAccountPage';
import ServicesByMaster from './pages/ServicesByMaster';
import ChooseDatePage from './pages/ChooseDatePage';
import ReadyAppointmentPage from './pages/ReadyAppointmentPage';

const App: React.FC = () => {
  const dispatch = useAppDispatch();
  const { isAuthenticated, isLoading } = useAppSelector((state) => state.auth);
  const location = useLocation();

  // НОВАЯ ЛОГИКА: один раз при загрузке приложения
  useEffect(() => {
    dispatch(initializeAuth());
  }, [dispatch]); // ← только dispatch в зависимостях

  // Пока идёт инициализация (проверка токена + fetchMe) — показываем лоадер
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

        {/* Публичные страницы */}
        <Route path='/login' element={<LoginPage />} />
        <Route path='/registration' element={<RegistrationPage />} />

        {/* ЗАЩИЩЁННЫЕ страницы */}
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

        <Route path='/choose-date/:masterId' element={<ChooseDatePage />} />

        <Route
          path='/services-by-master/:masterId'
          element={
            isAuthenticated ? (
              <ServicesByMaster />
            ) : (
              <Navigate to='/login' state={{ from: location }} replace />
            )
          }
        />

        <Route path='/appointment/confirm' element={<ReadyAppointmentPage />} />

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

        {/* 404 */}
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
