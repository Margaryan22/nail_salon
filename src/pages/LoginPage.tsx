// src/pages/LoginPage.tsx

import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../hooks';
import { login, clearServerMessage } from '../redux/authSlice';

const LoginPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const { isLoading, serverMessage, isAuthenticated } = useAppSelector(
    (state) => state.auth
  );

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  // Очистка сообщения при входе на страницу
  useEffect(() => {
    dispatch(clearServerMessage());
  }, [dispatch]);

  // Редирект сразу после успешного логина
  useEffect(() => {
    if (isAuthenticated) {
      navigate('/profile', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!email.trim() || !password) return;

    // Только один запрос — /auth/login
    await dispatch(login({ email: email.trim(), password }));
    // Всё! Никаких fetchMe, никаких доп. запросов
    // Редирект произойдёт автоматически через useEffect выше
  };

  const togglePassword = () => setShowPassword((prev) => !prev);

  return (
    <div className='login-page-container'>
      <div className='login-card'>
        <h2 className='form-title'>Вход в аккаунт</h2>

        <form onSubmit={handleSubmit} noValidate>
          <div className='form-group'>
            <label htmlFor='email'>Email</label>
            <input
              type='email'
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder='you@example.com'
              disabled={isLoading}
              required
            />
          </div>

          <div className='form-group password-group'>
            <label htmlFor='password'>Пароль</label>
            <div className='password-input-container'>
              <input
                type={showPassword ? 'text' : 'password'}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder='••••••••'
                disabled={isLoading}
                required
              />
              <button
                type='button'
                onClick={togglePassword}
                className='password-toggle'
              >
                {showPassword ? 'Скрыть' : 'Показать'}
              </button>
            </div>
          </div>

          {/* Сообщение с бэкенда */}
          {serverMessage.text && (
            <div
              className={`response-message ${
                serverMessage.status && serverMessage.status >= 400
                  ? 'error'
                  : 'success'
              }`}
            >
              {serverMessage.text}
            </div>
          )}

          <button type='submit' disabled={isLoading} className='form-button'>
            {isLoading ? 'Входим...' : 'Войти'}
          </button>

          <p className='register-prompt'>
            Нет аккаунта? <Link to='/registration'>Зарегистрироваться</Link>
          </p>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;
