// src/App.tsx

import React, { useEffect } from 'react'; // Добавлен useEffect для логики восстановления сессии
import './scss/app.scss';

import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from './hooks'; // ← обязательно должен быть
import { fetchMe } from './redux/authSlice'; // Импорт fetchMe для восстановления сессии

import RegistrationPage from './pages/RegistrationPage';
import LoginPage from './pages/LoginPage';
import ProfilePage from './pages/ProfilePage';
import ServiceCatalogPage from './pages/ServiceCatalogPage';
import UserAccountPage from './pages/UserAccountPage';
import ServicesByMaster from './pages/ServicesByMaster'; // <<< ИМПОРТ НОВОГО КОМПОНЕНТА

const App: React.FC = () => {
  const dispatch = useAppDispatch(); // Добавлен dispatch
  const { accessToken, isAuthenticated, isLoading } = useAppSelector(
    (state) => state.auth
  );
  const location = useLocation();

  // >>> ЛОГИКА ВОССТАНОВЛЕНИЯ СЕССИИ ПРИ ЗАГРУЗКЕ <<<
  useEffect(() => {
    // Проверяем, есть ли токен в состоянии (загружен из localStorage) и пользователь не авторизован
    if (accessToken && !isAuthenticated) {
      // Пытаемся получить профиль, чтобы подтвердить токен
      dispatch(fetchMe());
    }
  }, [accessToken, isAuthenticated, dispatch]);
  // >>> КОНЕЦ ЛОГИКИ ВОССТАНОВЛЕНИЯ СЕССИИ <<<

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

        {/* <<< НОВЫЙ ЗАЩИЩЕННЫЙ МАРШРУТ: Выбор услуг мастера */}
        <Route
          path='/services-by-master/:masterId' // ← теперь совпадает с navigate()
          element={
            isAuthenticated ? (
              <ServicesByMaster />
            ) : (
              <Navigate to='/login' state={{ from: location }} replace />
            )
          }
        />
        {/* КОНЕЦ НОВОГО МАРШРУТА >>> */}

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
