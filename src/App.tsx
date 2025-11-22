// src/App.tsx

import React from 'react';
import './scss/app.scss';

import { Routes, Route, Navigate } from 'react-router-dom';

import RegistrationPage from './pages/RegistrartionPage';
import LoginPage from './pages/LoginPage';
import ProfilePage from './pages/ProfilePage'; // ← правильное имя
import ServiceCatalogPage from './pages/ServiceCatalogPage';
import UserAccountPage from './pages/UserAccountPage';

const App: React.FC = () => {
  return (
    <div className='app-container'>
      <Routes>
        <Route path='/' element={<Navigate to='/login' replace />} />
        {/* Авторизация */}
        <Route path='/registration' element={<RegistrationPage />} />
        <Route path='/login' element={<LoginPage />} />
        {/* Профиль с ID */}
        <Route path='/profile' element={<ProfilePage />} />{' '}
        {/* опционально — свой профиль без ID */}
        {/* Каталог */}
        <Route path='/services' element={<ServiceCatalogPage />} />
        {/* Аккаунт (если отдельно) */}
        <Route path='/account' element={<UserAccountPage />} />
        {/* 404 — опционально */}
        <Route path='*' element={<div>Страница не найдена</div>} />
      </Routes>
    </div>
  );
};

export default App;
